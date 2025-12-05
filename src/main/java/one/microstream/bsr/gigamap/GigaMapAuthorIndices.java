package one.microstream.bsr.gigamap;

import java.util.UUID;

import org.eclipse.store.gigamap.types.BinaryIndexerString;
import org.eclipse.store.gigamap.types.BinaryIndexerUUID;

import one.microstream.bsr.domain.Author;

public final class GigaMapAuthorIndices
{
    public static final BinaryIndexerUUID<Author> ID = new BinaryIndexerUUID.Abstract<>()
    {
        @Override
        protected UUID getUUID(final Author entity)
        {
            return entity.id();
        }
    };

    public static final BinaryIndexerString<Author> NAME = new BinaryIndexerString.Abstract<>()
    {
        protected String getString(final Author entity)
        {
            return entity.name();
        }
    };

    private GigaMapAuthorIndices()
    {
    }
}
