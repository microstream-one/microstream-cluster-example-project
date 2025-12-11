package one.microstream.bsr.exception;

import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;

public class IsbnAlreadyExistsException extends HttpStatusException
{
    public IsbnAlreadyExistsException(final String isbn)
    {
        super(HttpStatus.BAD_REQUEST, "Book with ISBN '%s' already exists".formatted(isbn));
    }
}
