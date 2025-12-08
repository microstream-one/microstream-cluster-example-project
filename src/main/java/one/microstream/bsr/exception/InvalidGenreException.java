package one.microstream.bsr.exception;

public class InvalidGenreException extends RuntimeException
{
    public InvalidGenreException(final String genre)
    {
        super("Could not find genre '%s'".formatted(genre));
    }
}
