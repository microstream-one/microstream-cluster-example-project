package one.microstream.bsr.controller;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
import one.microstream.bsr.dto.GetAuthorById;
import one.microstream.bsr.dto.InsertAuthor;
import one.microstream.bsr.dto.SearchAuthorByName;
import one.microstream.bsr.dto.UpdateAuthor;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.repository.AuthorRepository;

@Controller("/author")
public class AuthorController
{
    private final AuthorRepository authors;

    public AuthorController(final AuthorRepository authors)
    {
        this.authors = authors;
    }

    @Post
    public void insert(@NonNull @NotEmpty @Body final List<@NonNull @Valid InsertAuthor> insert)
    {
        this.authors.insert(insert);
    }

    @Put("/{id}")
    public void update(@NonNull @PathVariable final UUID id, @NonNull @Valid @Body final UpdateAuthor update)
    {
        this.authors.update(id, update);
    }

    @Delete("/{id}")
    public void delete(@NonNull @PathVariable final UUID id)
    {
        this.authors.delete(Arrays.asList(id));
    }

    @Delete("/batch")
    public void deleteBatch(@NonNull @NotEmpty @Format("csv") @QueryValue final Iterable<@NonNull UUID> ids)
        throws InvalidAuthorIdException
    {
        this.authors.delete(ids);
    }

    @Get("/name")
    public List<SearchAuthorByName> searchByName(@NonNull @NotBlank @QueryValue final String nameSearch)
    {
        return this.authors.searchByName(nameSearch);
    }

    @Get("/id")
    public GetAuthorById getById(@NonNull @QueryValue final UUID authorId)
    {
        return this.authors.getById(authorId).orElse(null);
    }
}
