package one.microstream.bsr.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterLockScope;
import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.gigamap.types.GigaMap;

import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Author;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.DataRoot;
import one.microstream.bsr.dto.GetAuthorById;
import one.microstream.bsr.dto.InsertAuthor;
import one.microstream.bsr.dto.InsertAuthor.InsertAuthorBookDto;
import one.microstream.bsr.dto.SearchAuthorByName;
import one.microstream.bsr.dto.UpdateAuthor;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.exception.InvalidGenreException;
import one.microstream.bsr.exception.IsbnAlreadyExistsException;
import one.microstream.bsr.gigamap.GigaMapAuthorIndices;
import one.microstream.bsr.gigamap.GigaMapBookIndices;

@Singleton
public class AuthorRepository extends ClusterLockScope
{
    private static final int DEFAULT_PAGE_SIZE = 512;

    private final GigaMap<Author> authors;
    private final GigaMap<Book> books;
    private final Set<String> genres;

    public AuthorRepository(
        final LockedExecutor executor,
        final RootProvider<DataRoot> rootProvider
    )
    {
        super(executor);
        final var root = rootProvider.root();
        this.authors = root.authors();
        this.books = root.books();
        this.genres = root.genres();
    }

    public Optional<GetAuthorById> getById(final UUID id)
    {
        return this.read(() -> this.authors.query(GigaMapAuthorIndices.ID.is(id)).findFirst()).map(GetAuthorById::from);
    }

    /**
     * 
     * @param containsNameSearch
     * @return
     * @see String#contains(CharSequence)
     */
    public List<SearchAuthorByName> searchByName(final String containsNameSearch)
    {
        return this.read(() ->
        {
            try (
                final var storedAuthors = this.authors.query(
                    GigaMapAuthorIndices.NAME.containsIgnoreCase(containsNameSearch)
                ).stream()
            )
            {
                return storedAuthors
                    .limit(DEFAULT_PAGE_SIZE)
                    .map(SearchAuthorByName::from)
                    .toList();
            }
        });
    }

    public List<GetAuthorById> insert(final List<InsertAuthor> insert) throws IsbnAlreadyExistsException
    {
        final var returnDtos = new ArrayList<GetAuthorById>(insert.size());

        this.write(() ->
        {
            this.validateInsert(insert);

            boolean modifiedBooks = false;

            for (final var insertAuthor : insert)
            {
                final var author = new Author(
                    UUID.randomUUID(),
                    insertAuthor.name(),
                    insertAuthor.about(),
                    new HashSet<>()
                );
                returnDtos.add(GetAuthorById.from(author));

                List<Book> authorBooks = null;
                if (insertAuthor.books() != null)
                {
                    authorBooks = insertAuthor.books()
                        .stream()
                        .map(
                            b -> new Book(
                                UUID.randomUUID(),
                                b.isbn(),
                                b.title(),
                                b.description(),
                                b.pages(),
                                b.genres(),
                                b.publicationDate(),
                                author
                            )
                        )
                        .toList();
                    author.books().addAll(authorBooks);
                }

                this.authors.add(author);

                if (authorBooks != null)
                {
                    this.books.addAll(authorBooks);
                    modifiedBooks = true;
                }
            }

            if (!insert.isEmpty())
            {
                this.authors.store();

                if (modifiedBooks)
                {
                    this.books.store();
                }
            }
        });

        return Collections.unmodifiableList(returnDtos);
    }

    private void validateInsert(final List<InsertAuthor> insert)
    {
        final List<InsertAuthorBookDto> insertBooks = insert.stream()
            .filter(a -> a.books() != null)
            .flatMap(a -> a.books().stream())
            .toList();

        for (final var book : insertBooks)
        {
            // check for isbn uniqueness in the insert and the storage
            final String isbn = book.isbn();
            if (
                insertBooks.stream().map(b -> b.isbn()).filter(isbn::equals).count() > 1
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

    /**
     * Updates all the fields of the author in the storage with the specified author
     * matching the id.
     * 
     * @param author the author containing all the updated fields and the same id as
     *               the author in the storage to update
     * @return <code>true</code> if the author was found and updated
     */
    public void update(final UUID id, final UpdateAuthor update)
    {
        this.write(() ->
        {
            final Author author = this.authors.query(GigaMapAuthorIndices.ID.is(id))
                .findFirst()
                .orElseThrow(() -> new InvalidAuthorIdException(id));
            this.authors.replace(author, new Author(id, update.name(), update.about(), author.books()));
            this.authors.store();
        });
    }

    public void delete(final Iterable<UUID> ids)
    {
        this.write(() ->
        {
            final var cachedAuthors = new ArrayList<Author>();
            for (final UUID id : ids)
            {
                // ensure authors exist
                cachedAuthors.add(
                    this.authors.query(GigaMapAuthorIndices.ID.is(id))
                        .findFirst()
                        .orElseThrow(() -> new InvalidAuthorIdException(id))
                );
            }
            if (!cachedAuthors.isEmpty())
            {
                for (final var author : cachedAuthors)
                {
                    for (final var book : author.books())
                    {
                        this.books.remove(book);
                    }
                    this.authors.remove(author);
                }
                this.books.store();
                this.authors.store();
            }
        });
    }
}
