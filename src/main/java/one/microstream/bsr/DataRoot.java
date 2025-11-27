
package one.microstream.bsr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.store.gigamap.types.GigaMap;

import one.microstream.bsr.domain.Author;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.Publisher;

public class DataRoot
{
	private final GigaMap<Book> books = GigaMap.<Book>Builder()
//		.withBitmapIdentityIndex(BookIndices.ID)
//		.withBitmapIndex(BookIndices.ISBN)
//		.withBitmapIndex(BookIndices.TITLE)
		.build();
	private final List<Author> authors = new ArrayList<>();
	private final List<Publisher> publishers = new ArrayList<>();

	public GigaMap<Book> getBooks()
	{
		return this.books;
	}

	public List<Author> getAuthors()
	{
		return this.authors;
	}

	public List<Publisher> getPublishers()
	{
		return this.publishers;
	}
}
