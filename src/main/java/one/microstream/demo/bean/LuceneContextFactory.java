package one.microstream.demo.bean;

import java.nio.file.Paths;

import org.eclipse.store.gigamap.lucene.LuceneContext;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import one.microstream.demo.domain.Book;
import one.microstream.demo.lucene.BookDocumentPopulator;

@Factory
public class LuceneContextFactory
{
    @Singleton
    public LuceneContext<Book> buildBookLuceneContext(
        @Property(name = "app.lucene.index.book.storage-directory") final String storageDirectory
    )
    {
        return LuceneContext.New(Paths.get(storageDirectory), new BookDocumentPopulator());
    }
}
