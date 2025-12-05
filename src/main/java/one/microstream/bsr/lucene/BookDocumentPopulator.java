package one.microstream.bsr.lucene;

import org.apache.lucene.document.Document;
import org.eclipse.store.gigamap.lucene.DocumentPopulator;

import one.microstream.bsr.domain.Book;

public final class BookDocumentPopulator extends DocumentPopulator<Book>
{
    public static String TITLE_FIELD = "title";

    @Override
    public void populate(final Document document, final Book entity)
    {
        document.add(createTextField(TITLE_FIELD, entity.title()));
    }
}
