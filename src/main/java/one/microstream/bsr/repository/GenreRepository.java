package one.microstream.bsr.repository;

import java.util.Collections;
import java.util.Set;

import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterLockScope;
import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.storage.types.StorageManager;

import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.DataRoot;
import one.microstream.bsr.exception.MissingGenreException;

@Singleton
public class GenreRepository extends ClusterLockScope
{
    private final Set<String> genres;
    private final StorageManager storageManager;

    public GenreRepository(
        final LockedExecutor executor,
        final RootProvider<DataRoot> rootProvider,
        final StorageManager storageManager
    )
    {
        super(executor);
        this.genres = rootProvider.root().genres();
        this.storageManager = storageManager;
    }

    /**
     * Inserts the {@link Genre} into the storage.
     * 
     * @param genre the genre to insert
     * @return <code>true</code> if the genre did not already exist in the storage
     */
    public boolean insert(final String genre)
    {
        return this.write(() ->
        {
            final boolean modified = this.genres.add(genre);
            if (modified)
            {
                this.storageManager.store(this.genres);
            }
            return modified;
        });
    }

    /**
     * List all genres contained in the storage in an unmodifiable {@link Set}.
     * 
     * @return an unmodifiable {@link Set} containing all genres
     */
    public Set<String> list()
    {
        return this.read(() -> Collections.unmodifiableSet(this.genres));
    }

    /**
     * Deletes the genre from the storage.
     * 
     * @param genre the genre to delete
     * @return <code>true</code> if the storage contained the genre
     */
    public void delete(final String genre) throws MissingGenreException
    {
        this.write(() ->
        {
            final boolean modified = this.genres.remove(genre);
            if (!modified)
            {
                throw new MissingGenreException(genre);
            }
            this.storageManager.store(this.genres);
        });
    }
}
