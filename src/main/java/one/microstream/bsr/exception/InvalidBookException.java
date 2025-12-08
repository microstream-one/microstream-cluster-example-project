package one.microstream.bsr.exception;

import java.util.UUID;

public class InvalidBookException extends RuntimeException
{
    public InvalidBookException(final UUID bookId)
    {
        super("Could not find book with id " + bookId);
    }
}
