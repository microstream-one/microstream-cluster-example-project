package one.microstream.bsr.bean;

import org.eclipse.store.gigamap.lucene.LuceneContext;
import org.eclipse.store.gigamap.lucene.LuceneIndex;

import io.micronaut.context.annotation.Factory;
import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.DataRoot;

@Factory
public class BookLuceneIndexFactory
{
    @Singleton
    public LuceneIndex<Book> buildLuceneBookIndices(
        final RootProvider<DataRoot> storageManager,
        final LuceneContext<Book> bookLuceneContext
    )
    {
        return storageManager.root()
            .books()
            .index()
            .register(LuceneIndex.Category(bookLuceneContext));
    }
}
