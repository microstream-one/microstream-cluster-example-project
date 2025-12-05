package one.microstream.bsr.bean;

import java.nio.file.Paths;

import org.eclipse.store.gigamap.lucene.LuceneContext;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.lucene.BookDocumentPopulator;

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
