package one.microstream.bsr.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterLockScope;
import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.storage.types.StorageManager;

import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.DataRoot;

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
     * List all genres contained in the storage in an unmodifiable {@link Set}.
     * 
     * @return an unmodifiable {@link Set} containing all genres
     */
    public Set<String> list()
    {
        return Collections.unmodifiableSet(this.read(() -> this.genres));
    }

    /**
     * Inserts the {@link Genre} into the storage.
     * 
     * @param genre the genre to insert
     * @return <code>true</code> if the genre did not already exist in the storage
     */
    public boolean insert(final String genre) throws NullPointerException
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
     * Inserts all the {@link Genre}s into the storage. <code>null</code> elements
     * are ignored.
     * 
     * @param genres the genres to insert
     * @return <code>true</code> if any genre did not already exist in the storage
     */
    public boolean insertAll(final Collection<String> genres) throws NullPointerException
    {
        // the Set might allow null-values, so we filter
        final Set<String> nonNullGenres = genres.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());
        return this.write(() ->
        {
            final boolean modified = this.genres.addAll(nonNullGenres);
            if (modified)
            {
                this.storageManager.store(this.genres);
            }
            return modified;
        });
    }

    /**
     * Deletes the genre from the storage.
     * 
     * @param genre the genre to delete
     * @return <code>true</code> if the storage contained the genre
     */
    public boolean delete(final String genre) throws NullPointerException
    {
        return this.write(() ->
        {
            final boolean modified = this.genres.remove(genre);
            if (modified)
            {
                this.storageManager.store(this.genres);
            }
            return modified;
        });
    }

    public boolean deleteAll(final Collection<String> genres)
    {
        return this.write(() ->
        {
            final boolean removed = this.genres.removeAll(genres);
            if (removed)
            {
                this.storageManager.store(this.genres);
            }
            return removed;
        });
    }
}
