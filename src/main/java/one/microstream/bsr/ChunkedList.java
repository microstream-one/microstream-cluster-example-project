package one.microstream.bsr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.serializer.persistence.types.PersistenceStoring;
import org.eclipse.serializer.reference.Lazy;

public class ChunkedList<T>
{
	private final List<Lazy<List<T>>> data = new ArrayList<>();
	private final int chunkSize;

	public ChunkedList(final int chunkSize)
	{
		this.chunkSize = chunkSize;
	}

	private Lazy<List<T>> appendDataList()
	{
		final Lazy<List<T>> newList = Lazy.Reference(new ArrayList<T>(this.chunkSize));
		this.data.add(newList);
		return newList;
	}

	public void add(final T item, final PersistenceStoring storing)
	{
		boolean addedDataList = false;

		if (this.data.isEmpty())
		{
			this.appendDataList();
			addedDataList = true;
		}

		var lastList = this.data.getLast();
		if (lastList.get().size() >= this.chunkSize)
		{
			lastList = this.appendDataList();
			addedDataList = true;
		}

		lastList.get().add(item);

		if (addedDataList)
		{
			storing.store(this.data);
		}
		else
		{
			storing.store(lastList.get());
		}
	}

	public void addAll(final Collection<T> items, final PersistenceStoring storing)
	{
		if (this.data.isEmpty())
		{
			this.appendDataList();
			storing.store(this.data);
		}

		for (final var item : items)
		{
			var lastList = this.data.getLast();
			// when a chunk is full, store it as well as the next reference
			if (lastList.get().size() >= this.chunkSize)
			{
				final var newList = this.appendDataList();
				storing.storeAll(lastList.get(), newList);
				lastList = newList;
			}

			lastList.get().add(item);
		}

		storing.store(this.data.getLast().get());
	}

	public T get(final int index)
	{
		return this.data.get(index / this.chunkSize).get().get(index % this.chunkSize);
	}

	public long size() throws ArithmeticException
	{
		if (this.data.isEmpty())
		{
			return 0L;
		}
		else
		{
			final long completedListsTotal = (this.data.size() - 1L) * this.chunkSize;
			return completedListsTotal + this.data.getLast().get().size();
		}
	}

	public void clear(final PersistenceStoring storing)
	{
		this.data.clear();
		storing.store(this.data);
	}

	public Stream<T> stream()
	{
		return this.data.stream().flatMap(l -> l.get().stream());
	}
}
