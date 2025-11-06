package one.microstream.bsr.controller;

import java.util.List;
import java.util.Optional;

import io.micrometer.observation.annotation.Observed;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.dto.BookDto;
import one.microstream.bsr.dto.BookReferenceDto;
import one.microstream.bsr.exception.IndexAlreadyExistsException;
import one.microstream.bsr.repository.AuthorRepository;
import one.microstream.bsr.repository.BookRepository;
import one.microstream.bsr.repository.PublisherRepository;

@Observed
@Controller("/book")
public class BookController
{
	private final BookRepository books;
	private final AuthorRepository authors;
	private final PublisherRepository publishers;

	public BookController(
		final BookRepository books,
		final AuthorRepository authors,
		final PublisherRepository publishers
	)
	{
		this.books = books;
		this.authors = authors;
		this.publishers = publishers;
	}

	@Error(exception = IndexAlreadyExistsException.class, status = HttpStatus.BAD_REQUEST)
	public String handleIndexAlreadyExistsException(final IndexAlreadyExistsException e)
	{
		return e.getMessage();
	}

	@Get("/count")
	public long getCount()
	{
		return this.books.countBooks();
	}

	@Get("/search")
	public List<BookDto> searchBook(
		@QueryValue("title") @NonNull @NotBlank final String title,
		@QueryValue("page") @Nullable @PositiveOrZero final Integer page,
		@QueryValue("pageSize") @Nullable @PositiveOrZero final Integer pageSize
	)
	{
		final List<Book> searchedBooks;
		if (pageSize == null && page == null)
		{
			searchedBooks = this.books.searchBooksByTitle(title);
		}
		else if (pageSize == null)
		{
			searchedBooks = this.books.searchBooksByTitle(title, page);
		}
		else
		{
			searchedBooks = this.books.searchBooksByTitle(title, page, pageSize);
		}
		return searchedBooks.stream().map(BookDto::new).toList();
	}

	@Get("/{isbn}")
	public BookDto getBookByIsbn(@NonNull @NotBlank @PathVariable final String isbn)
	{
		return Optional.ofNullable(this.books.getBookByISBN(isbn)).map(BookDto::new).orElse(null);
	}

	@Get("/id/{id}")
	public BookDto getBookById(@NonNull @PathVariable @Positive final Long id)
	{
		return Optional.ofNullable(this.books.getBookById(id)).map(BookDto::new).orElse(null);
	}

	@Put
	public void putBook(@Body @Valid @NonNull final BookReferenceDto dto)
	{
		this.books.insert(this.convertDtoToBook(dto));
	}

	@Put("/batch")
	public void putBookBatch(@NotEmpty @Body @NonNull final List<@Valid @NonNull BookReferenceDto> dto)
	{
		try
		{
			this.books.insertAll(dto.stream().map(this::convertDtoToBook).toList());
		}
		catch (final IllegalArgumentException e)
		{
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	private Book convertDtoToBook(final BookReferenceDto dto) throws HttpStatusException
	{
		final var book = new Book(dto);
		final var author = this.authors.getAuthorById(dto.author());
		if (author == null)
		{
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Could not find author with id " + dto.author());
		}
		final var publisher = this.publishers.getPublisherById(dto.publisher());
		if (publisher == null)
		{
			throw new HttpStatusException(HttpStatus.NOT_FOUND, "Could not find publisher with id " + dto.publisher());
		}
		book.setAuthor(author);
		book.setPublisher(publisher);
		return book;
	}

	@Post("/clear")
	public void clearBooks()
	{
		this.books.clearBooks();
	}
}
