package one.microstream.bsr;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import io.micronaut.runtime.EmbeddedApplication;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;

@MicronautTest
class MicrostreamRestAPITest 
{
//	private static Path storageDir;
//
//	@BeforeAll
//	static void setup() throws IOException
//	{
//		storageDir = Files.createTempDirectory("books-rest-api-test-storage");
//	}
//
//	@AfterAll
//	static void destory() throws IOException
//	{
//		// delete storage dir
//		try (final var files = Files.walk(storageDir))
//		{
//			for (final var file : files.toList().reversed())
//			{
//				Files.delete(file);
//			}
//		}
//	}
//
//	@Override
//	public @NonNull Map<String, String> getProperties()
//	{
//		return Map.of("eclipsestore.storage.main.storage-directory", storageDir.toString());
//	}
//
	@Inject
	EmbeddedApplication<?> application;

	@Test
	void testItWorks()
	{
		Assertions.assertTrue(this.application.isRunning());
	}

}
