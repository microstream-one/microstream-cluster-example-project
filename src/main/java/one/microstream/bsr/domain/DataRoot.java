
package one.microstream.bsr.domain;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.store.gigamap.types.GigaMap;

import one.microstream.bsr.gigamap.GigaMapAuthorIndices;
import one.microstream.bsr.gigamap.GigaMapBookIndices;

public final class DataRoot
{
    private final GigaMap<Author> authors = GigaMap.<Author>Builder()
        .withBitmapIdentityIndex(GigaMapAuthorIndices.ID)
        .withBitmapIndex(GigaMapAuthorIndices.NAME)
        .build();
    private final GigaMap<Book> books = GigaMap.<Book>Builder()
        .withBitmapIdentityIndex(GigaMapBookIndices.ID)
        .withBitmapIndex(GigaMapBookIndices.ISBN)
        .withBitmapIndex(GigaMapBookIndices.PUBLICATION)
        .build();
    private final Set<Genre> genres = new HashSet<>();

    public DataRoot()
    {
        super();
    }

    public GigaMap<Author> authors()
    {
        return this.authors;
    }

    public GigaMap<Book> books()
    {
        return this.books;
    }

    public Set<Genre> genres()
    {
        return this.genres;
    }
}
