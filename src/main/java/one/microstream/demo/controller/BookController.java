package one.microstream.demo.controller;

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
import one.microstream.demo.dto.GetBookById;
import one.microstream.demo.dto.InsertBook;
import one.microstream.demo.dto.SearchBookByAuthor;
import one.microstream.demo.dto.SearchBookByGenre;
import one.microstream.demo.dto.SearchBookByTitle;
import one.microstream.demo.dto.UpdateBook;
import one.microstream.demo.repository.BookRepository;

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
    public void deleteBatch(@NonNull @Format("csv") @QueryValue final List<@NonNull UUID> ids)
    {
        this.books.delete(ids);
    }

    @Get("/id/{id}")
    public GetBookById getById(@NonNull @PathVariable final UUID id)
    {
        return this.books.getById(id);
    }

    @Get("/isbn/{isbn}")
    public GetBookById getByIsbn(@NonNull @NotBlank @PathVariable final String isbn)
    {
        return this.books.getByISBN(isbn);
    }

    @Get("/author/{id}")
    public List<SearchBookByAuthor> searchByAuthor(@NonNull @PathVariable final UUID id)
    {
        return this.books.searchByAuthor(id);
    }

    @Get("/title")
    public List<SearchBookByTitle> searchByTitle(@NonNull @NotBlank @QueryValue final String search)
    {
        return this.books.searchByTitle(search);
    }

    @Get("/genre")
    public List<SearchBookByGenre> searchByGenre(
        @NonNull @NotBlank @QueryValue final String genres
    )
    {
        // @Format("csv") doesn't work for single values
        final Set<String> genresSet = Streams.of(genres.split(","))
            .filter(s -> !s.isBlank())
            .collect(Collectors.toUnmodifiableSet());
        return this.books.searchByGenre(genresSet);
    }
}
