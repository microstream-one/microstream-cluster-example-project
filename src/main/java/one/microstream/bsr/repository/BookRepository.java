package one.microstream.bsr.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterLockScope;
import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.gigamap.lucene.LuceneIndex;
import org.eclipse.store.gigamap.types.GigaMap;
import org.eclipse.store.storage.types.StorageManager;

import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Author;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.DataRoot;
import one.microstream.bsr.dto.GetBookById;
import one.microstream.bsr.dto.InsertBook;
import one.microstream.bsr.dto.SearchBookByAuthor;
import one.microstream.bsr.dto.SearchBookByGenre;
import one.microstream.bsr.dto.SearchBookByTitle;
import one.microstream.bsr.dto.UpdateBook;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.exception.InvalidBookException;
import one.microstream.bsr.exception.InvalidGenreException;
import one.microstream.bsr.exception.IsbnAlreadyExistsException;
import one.microstream.bsr.gigamap.GigaMapAuthorIndices;
import one.microstream.bsr.gigamap.GigaMapBookIndices;
import one.microstream.bsr.lucene.BookDocumentPopulator;

@Singleton
public class BookRepository extends ClusterLockScope
{
    private static final int DEFAULT_PAGE_SIZE = 512;

    private final GigaMap<Book> books;
    private final GigaMap<Author> authors;
    private final LuceneIndex<Book> luceneIndex;
    private final Set<String> genres;
    private final StorageManager storageManager;

    public BookRepository(
        final LockedExecutor executor,
        final RootProvider<DataRoot> rootProvider,
        final StorageManager storageManager,
        final LuceneIndex<Book> luceneIndex
    )
    {
        super(executor);
        final var root = rootProvider.root();
        this.books = root.books();
        this.authors = root.authors();
        this.genres = root.genres();
        this.luceneIndex = luceneIndex;
        this.storageManager = storageManager;
    }

    public List<GetBookById> insert(final List<InsertBook> insert)
    {
        final var returnDtos = new ArrayList<GetBookById>(insert.size());

        this.write(() ->
        {
            this.validateInsert(insert);

            // these are the authors that will have to be modified from the insert
            final Map<UUID, Author> cachedAuthors = new HashMap<>();
            for (final var insertBook : insert)
            {
                cachedAuthors.computeIfAbsent(
                    insertBook.authorId(),
                    id -> this.authors.query(GigaMapAuthorIndices.ID.is(id))
                        .findFirst()
                        .orElseThrow(() -> new InvalidAuthorIdException(id))
                );
            }

            final List<Book> newBooks = insert.stream()
                .map(
                    b -> new Book(
                        UUID.randomUUID(),
                        b.isbn(),
                        b.title(),
                        b.description(),
                        b.pages(),
                        b.genres(),
                        b.publicationDate(),
                        cachedAuthors.get(b.authorId())
                    )
                )
                .toList();

            if (!newBooks.isEmpty())
            {
                this.books.addAll(newBooks);
                this.books.store();
            }

            for (final var book : newBooks)
            {
                // add the new books to the author book lists
                book.author().books().add(book);

                // add as return value
                returnDtos.add(GetBookById.from(book));
            }

            // only store the changed book lists
            this.storageManager.storeAll(cachedAuthors.values());
        });

        return Collections.unmodifiableList(returnDtos);
    }

    private void validateInsert(final List<InsertBook> insert)
    {
        for (final var book : insert)
        {
            // check for isbn uniqueness in the insert and the storage
            final String isbn = book.isbn();
            if (
                insert.stream().map(b -> b.isbn()).filter(isbn::equals).count() > 1
                    || this.books.query(GigaMapBookIndices.ISBN.is(isbn))
                        .findFirst()
                        .isPresent()
            )
            {
                throw new IsbnAlreadyExistsException(isbn);
            }

            // check if genres exist
            for (final var genre : book.genres())
            {
                if (!this.genres.contains(genre))
                {
                    throw new InvalidGenreException(genre);
                }
            }
        }
    }

    public Optional<GetBookById> getById(final UUID id)
    {
        return this.read(() -> this.books.query(GigaMapBookIndices.ID.is(id)).findFirst()).map(GetBookById::from);
    }

    public Optional<GetBookById> getByISBN(final String isbn)
    {
        return this.read(() -> this.books.query(GigaMapBookIndices.ISBN.is(isbn)).findFirst().map(GetBookById::from));
    }

    /**
     * Searches books by title using a {@link WildcardQuery}
     */
    public List<SearchBookByTitle> searchByTitle(final String titleWildcardSearch)
    {
        final var query = new WildcardQuery(new Term(BookDocumentPopulator.TITLE_FIELD, titleWildcardSearch));
        return this.read(() -> this.luceneIndex.query(query, DEFAULT_PAGE_SIZE))
            .stream()
            .map(SearchBookByTitle::from)
            .toList();
    }

    public List<SearchBookByGenre> searchByGenre(final Set<String> genres)
    {
        // TODO replace with lucene search TermInSetQuery
        final var storedBooks = this.read(() ->
        {
            try (final var queryStream = this.books.query().stream())
            {
                return queryStream.limit(DEFAULT_PAGE_SIZE)
                    .filter(b -> b.genres().containsAll(genres))
                    .toList();
            }
        });
        // need to re-stream again, or else we would violate the read lock
        return storedBooks.stream().map(SearchBookByGenre::from).toList();
    }

    public List<SearchBookByAuthor> searchByAuthor(final UUID id)
    {
        return this.read(
            () -> this.authors.query(GigaMapAuthorIndices.ID.is(id))
                .findFirst()
                .orElseThrow(() -> new InvalidAuthorIdException(id))
        )
            .books()
            .stream()
            .map(SearchBookByAuthor::from)
            .toList();
    }

    /**
     * Updates all the fields of the book in the storage with the specified book
     * matching the id.
     * 
     * @param book the book containing all the updated fields and the same id as the
     *             book in the storage to update
     * @return <code>true</code> if the book was found and updated
     */
    public void update(final UUID id, final UpdateBook update)
    {
        this.write(() ->
        {
            final Book storedBook = this.books.query(GigaMapBookIndices.ID.is(id))
                .findFirst()
                .orElseThrow(() -> new InvalidBookException(id));
            this.books.replace(
                storedBook,
                new Book(
                    id,
                    update.isbn(),
                    update.title(),
                    update.description(),
                    update.pages(),
                    update.genres(),
                    update.publicationDate(),
                    storedBook.author()
                )
            );
            this.books.store();
        });
    }

    /**
     * @return <code>true</code> if the book has been removed from the storage
     */
    public void delete(final Iterable<UUID> ids)
    {
        this.write(() ->
        {
            final var cachedBooks = new ArrayList<Book>();
            for (final UUID id : ids)
            {
                // ensure books exist
                cachedBooks.add(
                    this.books.query(GigaMapBookIndices.ID.is(id))
                        .findFirst()
                        .orElseThrow(() -> new InvalidBookException(id))
                );
            }
            if (!cachedBooks.isEmpty())
            {
                cachedBooks.forEach(this.books::remove);
                this.books.store();
            }
        });
    }
}
