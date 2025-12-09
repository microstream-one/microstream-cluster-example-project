package one.microstream.bsr.exception;

public class IsbnAlreadyExistsException extends RuntimeException
{
    public IsbnAlreadyExistsException(final String isbn)
    {
        super("Book with ISBN '%s' already exists".formatted(isbn));
    }
}
