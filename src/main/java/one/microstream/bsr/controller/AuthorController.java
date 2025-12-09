package one.microstream.bsr.controller;

import java.util.List;
import java.util.UUID;

import io.micronaut.core.annotation.NonNull;
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
import one.microstream.bsr.dto.AuthorDto;
import one.microstream.bsr.dto.GetAuthorByIdDto;
import one.microstream.bsr.dto.InsertAuthorDto;
import one.microstream.bsr.dto.SearchAuthorByNameDto;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.service.AuthorService;

@Controller("/book")
public class AuthorController
{
    private final AuthorService authors;

    public AuthorController(final AuthorService authors)
    {
        this.authors = authors;
    }

    @Put
    public void put(@NonNull @Valid @Body final InsertAuthorDto author)
    {
        this.authors.insert(author);
    }

    @Put("/batch")
    public void putBatch(@NonNull @NotEmpty @Body final List<@NonNull @Valid InsertAuthorDto> authorDtos)
    {
        this.authors.insertAll(authorDtos);
    }

    @Patch
    public void patch(@NonNull @Valid @Body final AuthorDto author)
    {
        this.authors.update(author);
    }

    @Delete
    public void delete(@NonNull @Body final UUID authorId)
    {
        this.authors.delete(authorId);
    }

    @Delete("/batch")
    public void deleteBatch(@NonNull @NotEmpty @Body final List<@NonNull UUID> authorIds)
        throws InvalidAuthorIdException
    {
        this.authors.deleteAll(authorIds);
    }

    @Get("/name")
    public List<SearchAuthorByNameDto> getName(@NonNull @NotBlank @QueryValue final String nameSearch)
    {
        return this.authors.searchByName(nameSearch);
    }

    @Get("/id")
    public GetAuthorByIdDto getId(@NonNull @QueryValue final UUID authorId)
    {
        return this.authors.getById(authorId).orElse(null);
    }
}
