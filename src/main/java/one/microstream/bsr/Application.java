package one.microstream.bsr;

import org.slf4j.LoggerFactory;

import io.micronaut.runtime.Micronaut;

public class Application
{
	public static void main(final String[] args)
	{
		LoggerFactory.getLogger(Application.class).info("Yeppers");
		Micronaut.run(Application.class, args);
	}
}
