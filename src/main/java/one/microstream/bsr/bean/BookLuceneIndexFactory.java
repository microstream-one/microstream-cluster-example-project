package one.microstream.bsr.bean;

import org.eclipse.store.gigamap.lucene.LuceneContext;
import org.eclipse.store.gigamap.lucene.LuceneIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.micronaut.context.annotation.Factory;
import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.domain.DataRoot;

@Factory
public class BookLuceneIndexFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(BookLuceneIndexFactory.class);

    @Singleton
    public LuceneIndex<Book> buildLuceneBookIndices(
        final RootProvider<DataRoot> storageManager,
        final LuceneContext<Book> bookLuceneContext
    )
    {
        @SuppressWarnings("unchecked")
        LuceneIndex<Book> index = storageManager.root().books().index().get(LuceneIndex.class);
        if (index == null)
        {
            LOG.info("Creating new lucene index");
            index = storageManager.root()
                .books()
                .index()
                .register(LuceneIndex.Category(bookLuceneContext));
        }
        return index;
    }
}
