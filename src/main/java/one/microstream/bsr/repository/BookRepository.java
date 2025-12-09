package one.microstream.bsr.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterLockScope;
import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.gigamap.lucene.LuceneIndex;
import org.eclipse.store.gigamap.types.GigaMap;

import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.DataRoot;
import one.microstream.bsr.gigamap.GigaMapBookIndices;
import one.microstream.bsr.lucene.BookDocumentPopulator;

@Singleton
public class BookRepository extends ClusterLockScope
{
    private static final int DEFAULT_PAGE_SIZE = 512;

    private final GigaMap<Book> books;
    private final LuceneIndex<Book> luceneIndex;

    public BookRepository(
        final LockedExecutor executor,
        final RootProvider<DataRoot> rootProvider,
        final LuceneIndex<Book> luceneIndex
    )
    {
        super(executor);
        this.books = rootProvider.root().books();
        this.luceneIndex = luceneIndex;
    }

    public Optional<Book> getById(final UUID id)
    {
        return this.read(() -> this.books.query(GigaMapBookIndices.ID.is(id)).findFirst());
    }

    public Optional<Book> getByISBN(final String isbn)
    {
        return this.read(() -> this.books.query(GigaMapBookIndices.ISBN.is(isbn)).findFirst());
    }

    /**
     * Searches books by title using a {@link WildcardQuery}
     */
    public List<Book> searchByTitle(final String titleWildcardSearch)
    {
        final var query = new WildcardQuery(new Term(BookDocumentPopulator.TITLE_FIELD, titleWildcardSearch));
        return this.read(() -> this.luceneIndex.query(query, DEFAULT_PAGE_SIZE));
    }

    public List<Book> searchByGenre(final Set<String> genres)
    {
        // TODO replace with lucene search TermInSetQuery
        return this.read(() ->
        {
            try (final var storedBooks = this.books.query().stream())
            {
                return storedBooks.limit(DEFAULT_PAGE_SIZE)
                    .filter(b -> b.genres().containsAll(genres))
                    .toList();
            }
        });
    }

    public void insert(final Book book)
    {
        this.write(() ->
        {
            this.books.add(book);
            this.books.store();
        });
    }

    public void insertAll(final Iterable<Book> books)
    {
        this.write(() ->
        {
            this.books.addAll(books);
            this.books.store();
        });
    }

    /**
     * Updates all the fields of the book in the storage with the specified book
     * matching the id.
     * 
     * @param book the book containing all the updated fields and the same id as the
     *             book in the storage to update
     * @return <code>true</code> if the book was found and updated
     */
    public boolean update(final Book book)
    {
        return this.write(() ->
        {
            final Book storedBook = this.getById(book.id()).orElse(null);
            if (storedBook != null)
            {
                this.books.replace(storedBook, book);
                this.books.store();
                return true;
            }
            return false;
        });
    }

    /**
     * @return <code>true</code> if the book has been removed from the storage
     */
    public boolean delete(final UUID bookId)
    {
        return this.write(() ->
        {
            final long id = this.books.query(GigaMapBookIndices.ID.is(bookId))
                .findFirst()
                .map(this.books::remove)
                .orElse(-1L);
            if (id != -1)
            {
                this.books.store();
                return true;
            }
            return false;
        });
    }

    public boolean deleteAll(final Iterable<UUID> bookIds)
    {
        return this.write(() ->
        {
            boolean removed = false;
            for (final UUID id : bookIds)
            {
                final var book = this.books.query(GigaMapBookIndices.ID.is(id)).findFirst().orElse(null);
                if (book != null)
                {
                    this.books.remove(book);
                    removed = true;
                }
            }
            if (removed)
            {
                this.books.store();
            }
            return removed;
        });
    }
}
