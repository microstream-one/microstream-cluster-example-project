package one.microstream.bsr.exception;

import java.util.UUID;

public class InvalidAuthorIdException extends RuntimeException
{
    public InvalidAuthorIdException(final UUID authorId)
    {
        super("Could not find author with id " + authorId);
    }
}
