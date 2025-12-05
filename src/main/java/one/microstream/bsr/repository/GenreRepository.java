package one.microstream.bsr.repository;

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
import one.microstream.bsr.domain.Genre;

@Singleton
public class GenreRepository extends ClusterLockScope
{
    private final Set<Genre> genres;
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
    public Set<Genre> list()
    {
        return Collections.unmodifiableSet(this.read(() -> this.genres));
    }

    /**
     * Inserts the {@link Genre} into the storage.
     * 
     * @param genre the genre to insert
     * @return <code>true</code> if the genre did not already exist in the storage
     * @throws NullPointerException if genre is <code>null</code>
     */
    public boolean insert(final Genre genre) throws NullPointerException
    {
        Objects.requireNonNull(genre, "genre is null");
        return this.write(() -> this.genres.add(genre));
    }

    /**
     * Inserts all the {@link Genre}s into the storage. <code>null</code> elements
     * are ignored.
     * 
     * @param genres the genres to insert
     * @return <code>true</code> if any genre did not already exist in the storage
     * @throws NullPointerException if genres is <code>null</code>
     */
    public boolean insertAll(final Set<Genre> genres) throws NullPointerException
    {
        Objects.requireNonNull(genres, "genres is null");
        final Set<Genre> nonNullGenres = genres.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toUnmodifiableSet());
        return this.write(() ->
        {
            final boolean modified = this.genres.addAll(nonNullGenres);
            this.storageManager.store(this.genres);
            return modified;
        });
    }

    /**
     * Deletes the genre from the storage.
     * 
     * @param genre the genre to delete
     * @return <code>true</code> if the storage contained the genre
     * @throws NullPointerException if genre is <code>null</code>
     */
    public boolean delete(final Genre genre) throws NullPointerException
    {
        Objects.requireNonNull(genre, "genre is null");
        return this.write(() -> this.genres.remove(genre));
    }
}
