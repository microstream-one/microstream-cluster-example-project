package one.microstream.bsr.exception;

import java.util.UUID;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class MissingBookException extends HttpStatusException
{
    public MissingBookException(final UUID bookId)
    {
        super(HttpStatus.NOT_FOUND, "Could not find book with id " + bookId);
    }
}
