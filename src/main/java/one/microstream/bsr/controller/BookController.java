package one.microstream.bsr.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.stream.Streams;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.convert.format.Format;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Patch;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import one.microstream.bsr.dto.BookDto;
import one.microstream.bsr.dto.InsertBookDto;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.exception.InvalidGenreException;
import one.microstream.bsr.service.BookService;

@Controller("/book")
public class BookController
{
    private final BookService books;

    public BookController(final BookService books)
    {
        this.books = books;
    }

    @Put
    public void put(@NonNull @Valid @Body final InsertBookDto book) throws InvalidAuthorIdException
    {
        this.books.insert(book);
    }

    @Put("/batch")
    public void putBatch(@NonNull @NotEmpty @Body final List<@NonNull @Valid InsertBookDto> books)
        throws InvalidAuthorIdException
    {
        this.books.insertAll(books);
    }

    @Patch
    public void patch(@NonNull @Valid @Body final BookDto book)
    {
        this.books.update(book);
    }

    @Delete
    public void delete(@NonNull @Valid @Body final BookDto book)
    {
        this.books.delete(book);
    }

    @Delete("/batch")
    public void deleteBatch(@NonNull @NotEmpty @Body final List<@NonNull @Valid BookDto> books)
        throws InvalidAuthorIdException
    {
        this.books.deleteAll(books);
    }

    @Get("/author")
    public List<BookDto> getAuthor(@NonNull @Body final UUID authorId) throws InvalidAuthorIdException
    {
        return this.books.searchByAuthor(authorId);
    }

    @Get("/title")
    public List<BookDto> getTitle(@NonNull @NotBlank @QueryValue final String titleSearch)
    {
        return this.books.searchByTitle(titleSearch);
    }

    @Get("/genre")
    public List<BookDto> getGenre(@NonNull @NotEmpty @Format("csv") @QueryValue final Iterable<String> genres)
        throws InvalidGenreException
    {
        final Set<String> genresSet = Streams.of(genres).collect(Collectors.toUnmodifiableSet());
        return this.books.searchByGenre(genresSet);
    }

    @Get("/isbn")
    public List<BookDto> getIsbn(@NonNull @NotBlank @QueryValue final String isbn)
    {
        return this.books.getByISBN(isbn);
    }
}
