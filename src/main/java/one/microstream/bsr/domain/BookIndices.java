package one.microstream.bsr.domain;

import org.eclipse.store.gigamap.types.BinaryIndexerLong;
import org.eclipse.store.gigamap.types.BinaryIndexerString;
import org.eclipse.store.gigamap.types.IndexerString;

public final class BookIndices
{
	// the BinaryIndexers are good for high-cardinality (distinct fields like uids)
	// the normal Indexers are good for low-cardinality (non-distinct fields like gender)

	public static final BinaryIndexerLong<Book> ID = new BinaryIndexerLong.Abstract<Book>()
	{
		@Override
		protected Long getLong(final Book entity)
		{
			return entity.getId();
		}
	};

	/*
	 * eventhough it's very likely that these are all unique, to be able to make
	 * queries like .contains() we are required to use a normal Indexer
	 */
	public static final IndexerString<Book> TITLE = new IndexerString.Abstract<Book>()
	{
		@Override
		protected String getString(final Book entity)
		{
			return entity.getTitle();
		}
	};

	public static final BinaryIndexerString<Book> ISBN = new BinaryIndexerString.Abstract<Book>()
	{
		@Override
		protected String getString(final Book entity)
		{
			return entity.getIsbn();
		}
	};

	private BookIndices()
	{
	}
}
