package one.microstream.bsr.exception;

public class IndexAlreadyExistsException extends RuntimeException
{
	public IndexAlreadyExistsException()
	{
	}

	public IndexAlreadyExistsException(final String msg)
	{
		super(msg);
	}

	public IndexAlreadyExistsException(final Throwable cause)
	{
		super(cause);
	}

	public IndexAlreadyExistsException(final String msg, final Throwable cause)
	{
		super(msg, cause);
	}
}
