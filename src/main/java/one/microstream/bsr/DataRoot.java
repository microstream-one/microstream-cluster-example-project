
package one.microstream.bsr;

import java.util.ArrayList;
import java.util.List;

import one.microstream.bsr.domain.Author;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.Publisher;

public class DataRoot
{
	private final ChunkedList<Book> books = new ChunkedList<>(1000);
	private final List<Author> authors = new ArrayList<>();
	private final List<Publisher> publishers = new ArrayList<>();

	public DataRoot()
	{
	}

	public ChunkedList<Book> getBooks()
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
