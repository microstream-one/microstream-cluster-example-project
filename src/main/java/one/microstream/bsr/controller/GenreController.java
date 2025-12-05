package one.microstream.bsr.controller;

import java.util.Set;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import one.microstream.bsr.domain.Genre;
import one.microstream.bsr.repository.GenreRepository;

@Controller("/genre")
public class GenreController
{
    private final GenreRepository genres;

    public GenreController(final GenreRepository genres)
    {
        this.genres = genres;
    }

    @Get
    public Set<Genre> get()
    {
        return this.genres.list();
    }
}
