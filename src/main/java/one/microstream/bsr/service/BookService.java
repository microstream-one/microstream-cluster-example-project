package one.microstream.bsr.service;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.WildcardQuery;
import org.eclipse.serializer.util.iterables.ChainedIterables;

import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.dto.InsertBookDto;
import one.microstream.bsr.gigamap.GigaMapBookIndices;
import one.microstream.bsr.lucene.BookDocumentPopulator;
import one.microstream.bsr.repository.BookRepository;

@Singleton
public final class BookService
{
    private final BookRepository repo;

    public BookService(final BookRepository repo)
    {
        this.repo = repo;
    }

    public Optional<Book> getById(final UUID id)
    {
        if (id == null)
        {
            throw new IllegalArgumentException("id is null");
        }
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

    public void insert(final Book book)
    {
        this.write(() ->
        {
            this.books.add(book);
            this.books.store();
        });
    }

    public void insertAll(final Iterable<InsertBookDto> books)
    {ChainedIterables<T>
        final var bookConverterIterable = new Iterable<Book>()
        {
            @Override
            public Iterator<Book> iterator()
            {
                final Iterator<InsertBookDto> booksIterator = books.iterator();

                return new Iterator<Book>()
                {
                    @Override
                    public boolean hasNext()
                    {
                        return booksIterator.hasNext();
                    }

                    @Override
                    public Book next()
                    {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };
            }
        };

        this.repo.insertAll(bookConverterIterable);
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
        return this.repo.update(book);
    }

    public boolean delete(final UUID id)
    {
        return this.repo.delete(new Book(id, null, null, null, 0, null, null, null));
    }
}
