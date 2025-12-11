package one.microstream.bsr.exception;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class InvalidGenreException extends HttpStatusException
{
    public InvalidGenreException(final String genre)
    {
        super(HttpStatus.BAD_REQUEST, "Genre '%s' does not exist".formatted(genre));
    }
}
