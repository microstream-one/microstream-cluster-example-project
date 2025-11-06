package one.microstream.bsr.repository;

import java.util.List;

import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.storage.types.StorageManager;

import jakarta.inject.Singleton;
import one.microstream.bsr.DataRoot;
import one.microstream.bsr.domain.Publisher;
import one.microstream.bsr.exception.IndexAlreadyExistsException;
import one.microstream.enterprise.cluster.nodelibrary.types.ClusterLockScope;
import one.microstream.enterprise.cluster.nodelibrary.types.ClusterStorageManager;

@Singleton
public class PublisherRepository extends ClusterLockScope
{
	private static final int PAGE_SIZE_LIMIT = 250;

	private final List<Publisher> publishers;
	private final StorageManager storage;

	public PublisherRepository(final ClusterStorageManager<DataRoot> storageManager, final LockedExecutor executor)
	{
		super(executor);
		this.storage = storageManager;
		this.publishers = storageManager.root().get().getPublishers();
	}

	public Publisher getPublisherByEmail(final String email)
	{
		return this.read(() -> this.publishers.stream().filter(a -> a.getEmail().equals(email)).findAny().orElse(null));
	}

	public Publisher getPublisherById(final long id)
	{
		return this.read(() -> this.publishers.stream().filter(a -> a.getId() == id).findAny().orElse(null));
	}

	public List<Publisher> searchPublishersByCompany(final String company)
	{
		return this.searchPublishersByCompany(company, 1);
	}

	public List<Publisher> searchPublishersByCompany(final String company, final int page)
	{
		return this.searchPublishersByCompany(company, page, PAGE_SIZE_LIMIT);
	}

	public List<Publisher> searchPublishersByCompany(final String _company, final int page, final int pageSize)
	{
		final String company = _company.toLowerCase(); // java lambdas man...

		final int offset = (page - 1) * pageSize;
		final int limit = Math.max(pageSize, PAGE_SIZE_LIMIT);

		return this.read(
			() -> this.publishers.stream()
				.filter(a -> a.getCompany().toLowerCase().contains(company))
				.skip(offset)
				.limit(limit)
				.toList()
		);
	}

	public void insert(final Publisher publisher) throws IndexAlreadyExistsException
	{
		this.write(() ->
		{
			this.ensureUniqueIndex(publisher);
			publisher.setId(this.publishers.size() + 1L);
			this.publishers.add(publisher);
			this.storage.store(this.publishers);
		});
	}

	public void insertAll(final List<Publisher> morePublishers) throws IndexAlreadyExistsException
	{
		this.write(() ->
		{
			for (final Publisher publisher : morePublishers)
			{
				this.ensureUniqueIndex(publisher);

				if (morePublishers.stream().filter(b -> b.getEmail().equals(publisher.getEmail())).count() > 1)
				{
					throw new IndexAlreadyExistsException("Publishers with duplicate email found in batch save.");
				}
			}

			final long nextId = this.publishers.size() + 1L;
			for (int i = 0; i < morePublishers.size(); i++)
			{
				morePublishers.get(i).setId(nextId + i);
			}

			this.publishers.addAll(morePublishers);
			this.storage.store(this.publishers);
		});

	}

	public long countPublishers()
	{
		return this.read(this.publishers::size);
	}

	public void clearPublishers()
	{
		this.write(() ->
		{
			this.publishers.clear();
			this.storage.store(this.publishers);
		});
	}

	private void ensureUniqueIndex(final Publisher publisher) throws IndexAlreadyExistsException
	{
		if (this.publishers.stream().anyMatch(b -> b.getEmail().equals(publisher.getEmail())))
		{
			throw new IndexAlreadyExistsException(
				"Publisher with email %s already exists.".formatted(publisher.getEmail())
			);
		}
	}
}
