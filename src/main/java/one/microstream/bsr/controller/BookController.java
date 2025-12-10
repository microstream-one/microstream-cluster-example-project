package one.microstream.bsr.controller;

import java.util.Arrays;
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
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import one.microstream.bsr.dto.GetBookById;
import one.microstream.bsr.dto.InsertBook;
import one.microstream.bsr.dto.SearchBookByAuthor;
import one.microstream.bsr.dto.SearchBookByGenre;
import one.microstream.bsr.dto.SearchBookByTitle;
import one.microstream.bsr.dto.UpdateBook;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.exception.InvalidGenreException;
import one.microstream.bsr.repository.BookRepository;

@Controller("/book")
public class BookController
{
    private final BookRepository books;

    public BookController(final BookRepository books)
    {
        this.books = books;
    }

    @Post
    public List<GetBookById> insert(@NonNull @NotEmpty @Body final List<@NonNull @Valid InsertBook> insert)
        throws InvalidAuthorIdException
    {
        return this.books.insert(insert);
    }

    @Put("/{id}")
    public void update(@NonNull @PathVariable final UUID id, @NonNull @Valid @Body final UpdateBook update)
    {
        this.books.update(id, update);
    }

    @Delete("/{id}")
    public void delete(@NonNull @PathVariable final UUID id)
    {
        this.books.delete(Arrays.asList(id));
    }

    @Delete("/batch")
    public void deleteBatch(@NonNull @NotEmpty @Body final List<@NonNull UUID> ids)
        throws InvalidAuthorIdException
    {
        this.books.delete(ids);
    }

    @Get("/author/{id}")
    public List<SearchBookByAuthor> searchByAuthor(@NonNull @PathVariable final UUID id) throws InvalidAuthorIdException
    {
        return this.books.searchByAuthor(id);
    }

    @Get("/title")
    public List<SearchBookByTitle> getTitle(@NonNull @NotBlank @QueryValue final String titleSearch)
    {
        return this.books.searchByTitle(titleSearch);
    }

    @Get("/genre")
    public List<SearchBookByGenre> getGenre(
        @NonNull @Format("csv") @QueryValue final Iterable<String> genres
    )
        throws InvalidGenreException
    {
        final Set<String> genresSet = Streams.of(genres).collect(Collectors.toUnmodifiableSet());
        return this.books.searchByGenre(genresSet);
    }

    @Get("/isbn")
    public GetBookById getIsbn(@NonNull @NotBlank @QueryValue final String isbn)
    {
        return this.books.getByISBN(isbn).orElse(null);
    }

    @Get("/id")
    public GetBookById getId(@NonNull @QueryValue final UUID id)
    {
        return this.books.getById(id).orElse(null);
    }
}
