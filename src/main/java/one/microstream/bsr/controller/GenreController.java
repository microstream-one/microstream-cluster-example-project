package one.microstream.bsr.controller;

import java.util.Set;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Put;
import jakarta.validation.constraints.NotBlank;
import one.microstream.bsr.repository.GenreRepository;

@Controller("/genre")
public class GenreController
{
    private final GenreRepository genres;

    public GenreController(final GenreRepository genres)
    {
        this.genres = genres;
    }

    @Put
    public boolean put(@NonNull @NotBlank @Body final String genre)
    {
        return this.genres.insert(genre);
    }

    @Put("/batch")
    public boolean putBatch(@NonNull @NotBlank @Body final Set<@NonNull @NotBlank String> genres)
    {
        return this.genres.insertAll(genres);
    }

    @Get
    public Set<String> get()
    {
        return this.genres.list();
    }

    @Delete
    public boolean delete(@NonNull @NotBlank final String genre)
    {
        return this.genres.delete(genre);
    }

    @Delete("/batch")
    public boolean deleteBatch(@NonNull @NotBlank @Body final Set<@NonNull @NotBlank String> genres)
    {
        return this.genres.deleteAll(genres);
    }
}
