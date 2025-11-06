package one.microstream.bsr.repository;

import java.util.List;

import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.storage.types.StorageManager;

import jakarta.inject.Singleton;
import one.microstream.bsr.ChunkedList;
import one.microstream.bsr.DataRoot;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.exception.IndexAlreadyExistsException;
import one.microstream.enterprise.cluster.nodelibrary.types.ClusterLockScope;
import one.microstream.enterprise.cluster.nodelibrary.types.ClusterStorageManager;

@Singleton
public class BookRepository extends ClusterLockScope
{
	private static final int PAGE_SIZE_LIMIT = 250;
	private final ChunkedList<Book> books;
	private final StorageManager storage;

	public BookRepository(final ClusterStorageManager<DataRoot> storageManager, final LockedExecutor executor)
	{
		super(executor);
		this.storage = storageManager;
		this.books = storageManager.root().get().getBooks();
	}

	public Book getBookByISBN(final String isbn)
	{
		return this.read(() -> this.books.stream().filter(b -> b.getIsbn().equals(isbn)).findAny().orElse(null));
	}

	public Book getBookById(final long id)
	{
		return this.read(() -> this.books.stream().filter(b -> b.getId() == id).findAny().orElse(null));
	}

	public List<Book> searchBooksByTitle(final String title)
	{
		return this.searchBooksByTitle(title, 1, PAGE_SIZE_LIMIT);
	}

	public List<Book> searchBooksByTitle(final String title, final int page)
	{
		return this.searchBooksByTitle(title, page, PAGE_SIZE_LIMIT);
	}

	public List<Book> searchBooksByTitle(final String _title, final int page, final int pageSize)
	{
		final String title = _title.toLowerCase(); // java lambdas man...

		final int offset = (page - 1) * pageSize;
		final int limit = Math.max(pageSize, PAGE_SIZE_LIMIT);

		return this.read(
			() -> this.books.stream()
				.filter(b -> b.getTitle().toLowerCase().contains(title))
				.skip(offset)
				.limit(limit)
				.toList()
		);
	}

	public void insert(final Book book) throws IndexAlreadyExistsException
	{
		this.write(() ->
		{
			this.ensureUniqueIndex(book);
			book.setId(this.books.size() + 1L);
			this.books.add(book, this.storage);
			this.storage.store(this.books);
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

			this.books.addAll(moreBooks, this.storage);
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
			this.books.clear(this.storage);
			this.storage.store(this.books);
		});
	}

	private void ensureUniqueIndex(final Book book) throws IndexAlreadyExistsException
	{
		if (this.books.stream().anyMatch(b -> b.getIsbn().equals(book.getIsbn())))
		{
			throw new IndexAlreadyExistsException("Book with isbn %s already exists.".formatted(book.getIsbn()));
		}
	}
}
