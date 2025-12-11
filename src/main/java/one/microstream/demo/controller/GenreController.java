package one.microstream.demo.controller;

import java.util.Set;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Delete;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Put;
import jakarta.validation.constraints.NotBlank;
import one.microstream.demo.repository.GenreRepository;

@Controller("/genre")
public class GenreController
{
    private final GenreRepository genres;

    public GenreController(final GenreRepository genres)
    {
        this.genres = genres;
    }

    @Put("/{genre}")
    public boolean insert(@NonNull @NotBlank @PathVariable final String genre)
    {
        return this.genres.insert(genre);
    }

    @Get
    public Set<String> list()
    {
        return this.genres.list();
    }

    @Delete("/{genre}")
    public void delete(@NonNull @NotBlank @PathVariable final String genre)
    {
        this.genres.delete(genre);
    }
}
