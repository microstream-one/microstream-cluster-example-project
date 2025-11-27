package one.microstream.bsr.repository;

import java.util.List;

import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterLockScope;
import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterStorageManager;
import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.storage.types.StorageManager;

import jakarta.inject.Singleton;
import one.microstream.bsr.DataRoot;
import one.microstream.bsr.domain.Author;
import one.microstream.bsr.exception.IndexAlreadyExistsException;

@Singleton
public class AuthorRepository extends ClusterLockScope
{
	private static final int PAGE_SIZE_LIMIT = 250;

	private final List<Author> authors;
	private final StorageManager storage;

	public AuthorRepository(final ClusterStorageManager<DataRoot> storageManager, final LockedExecutor executor)
	{
		super(executor);
		this.storage = storageManager;
		this.authors = storageManager.root().get().getAuthors();
	}

	public Author getAuthorByEmail(final String email)
	{
		return this.read(() -> this.authors.stream().filter(a -> a.getEmail().equals(email)).findAny().orElse(null));
	}

	public Author getAuthorById(final long id)
	{
		return this.read(() -> this.authors.stream().filter(a -> a.getId() == id).findAny().orElse(null));
	}

	public List<Author> searchAuthorsByName(final String name)
	{
		return this.searchAuthorsByName(name, 1);
	}

	public List<Author> searchAuthorsByName(final String name, final int page)
	{
		return this.searchAuthorsByName(name, page, PAGE_SIZE_LIMIT);
	}

	public List<Author> searchAuthorsByName(final String _name, final int page, final int pageSize)
	{
		final String name = _name.toLowerCase(); // java lambdas man...

		final int offset = (page - 1) * pageSize;
		final int limit = Math.max(pageSize, PAGE_SIZE_LIMIT);

		return this.read(
			() -> this.authors.stream()
				.filter(a -> a.getFullName().toLowerCase().contains(name))
				.skip(offset)
				.limit(limit)
				.toList()
		);
	}

	public void insert(final Author author) throws IndexAlreadyExistsException
	{
		this.write(() ->
		{
			this.ensureUniqueIndex(author);
			author.setId(this.authors.size() + 1L);
			this.authors.add(author);
			this.storage.store(this.authors);
		});
	}

	public void insertAll(final List<Author> moreAuthors) throws IndexAlreadyExistsException
	{
		this.write(() ->
		{
			for (final Author author : moreAuthors)
			{
				this.ensureUniqueIndex(author);

				if (moreAuthors.stream().filter(b -> b.getEmail().equals(author.getEmail())).count() > 1)
				{
					throw new IndexAlreadyExistsException("Authors with duplicate email found in batch save.");
				}
			}

			final long nextId = this.authors.size() + 1L;
			for (int i = 0; i < moreAuthors.size(); i++)
			{
				moreAuthors.get(i).setId(nextId + i);
			}

			this.authors.addAll(moreAuthors);
			this.storage.store(this.authors);
		});

	}

	public long countAuthors()
	{
		return this.read(this.authors::size);
	}

	public void clearAuthors()
	{
		this.write(() ->
		{
			this.authors.clear();
			this.storage.store(this.authors);
		});
	}

	private void ensureUniqueIndex(final Author author) throws IndexAlreadyExistsException
	{
		if (this.authors.stream().anyMatch(b -> b.getEmail().equals(author.getEmail())))
		{
			throw new IndexAlreadyExistsException("Author with email %s already exists.".formatted(author.getEmail()));
		}
	}
}
