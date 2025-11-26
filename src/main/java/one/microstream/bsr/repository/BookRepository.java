package one.microstream.bsr.repository;

import java.util.List;

import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.gigamap.types.GigaMap;

import jakarta.inject.Singleton;
import one.microstream.bsr.DataRoot;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.BookIndices;
import one.microstream.bsr.exception.IndexAlreadyExistsException;
import one.microstream.enterprise.cluster.nodelibrary.types.ClusterLockScope;
import one.microstream.enterprise.cluster.nodelibrary.types.ClusterStorageManager;

@Singleton
public class BookRepository extends ClusterLockScope
{
	private static final int PAGE_SIZE_LIMIT = 250;
	private final GigaMap<Book> books;

	public BookRepository(final ClusterStorageManager<DataRoot> storageManager, final LockedExecutor executor)
	{
		super(executor);
		this.books = storageManager.root().get().getBooks();
	}

	public Book getBookByISBN(final String isbn)
	{
		throw new RuntimeException();
		//return this.read(() -> this.books.query(BookIndices.ISBN.is(isbn)).findFirst().orElse(null));
	}

	public Book getBookById(final long id)
	{
		return this.read(() -> this.books.get(id - 1));
		//return this.read(() -> this.books.query(BookIndices.ID.is(id)).findFirst().orElse(null));
	}

	public List<Book> searchBooksByTitle(final String title)
	{
		return this.searchBooksByTitle(title, 1, PAGE_SIZE_LIMIT);
	}

	public List<Book> searchBooksByTitle(final String title, final int page)
	{
		return this.searchBooksByTitle(title, page, PAGE_SIZE_LIMIT);
	}

	public List<Book> searchBooksByTitle(final String title, final int page, final int pageSize)
	{
		final int offset = (page - 1) * pageSize;
		final int limit = Math.max(pageSize, PAGE_SIZE_LIMIT);

		throw new RuntimeException();
		//return this.read(() -> this.books.query(BookIndices.TITLE.containsIgnoreCase(title)).toList(offset, limit));
	}

	public void insert(final Book book) throws IndexAlreadyExistsException
	{
		this.write(() ->
		{
			this.ensureUniqueIndex(book);
			book.setId(this.books.size() + 1L);
			this.books.add(book);
			this.books.store();
		});
	}

	public void insertAll(final List<Book> moreBooks) throws IndexAlreadyExistsException
	{
		this.write(() ->
		{
			for (final Book book : moreBooks)
			{
				this.ensureUniqueIndex(book);

				if (moreBooks.stream().filter(b -> b.getIsbn().equals(book.getIsbn())).count() > 1)
				{
					throw new IndexAlreadyExistsException("Books with duplicate isbn / id found in batch save.");
				}
			}

			final long nextId = this.books.size() + 1L;
			for (int i = 0; i < moreBooks.size(); i++)
			{
				moreBooks.get(i).setId(nextId + i);
			}

			this.books.addAll(moreBooks);
			this.books.store();
		});

	}

	public long countBooks()
	{
		return this.read(this.books::size);
	}

	public void clearBooks()
	{
		this.write(() ->
		{
			this.books.clear();
			this.books.store();
		});
	}

	private void ensureUniqueIndex(final Book book) throws IndexAlreadyExistsException
	{
//		if (this.books.query(BookIndices.ISBN.like(book)).findFirst().isPresent())
//		{
//			throw new IndexAlreadyExistsException("Book with isbn %s already exists.".formatted(book.getIsbn()));
//		}
	}
}
