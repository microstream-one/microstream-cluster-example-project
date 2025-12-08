package one.microstream.bsr.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.eclipse.datagrid.cluster.nodelibrary.types.ClusterLockScope;
import org.eclipse.serializer.concurrency.LockedExecutor;
import org.eclipse.store.gigamap.types.GigaMap;

import io.micronaut.eclipsestore.RootProvider;
import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Author;
import one.microstream.bsr.domain.DataRoot;
import one.microstream.bsr.gigamap.GigaMapAuthorIndices;

@Singleton
public class AuthorRepository extends ClusterLockScope
{
    private static final int DEFAULT_PAGE_SIZE = 512;

    private final GigaMap<Author> authors;

    public AuthorRepository(
        final LockedExecutor executor,
        final RootProvider<DataRoot> rootProvider
    )
    {
        super(executor);
        this.authors = rootProvider.root().authors();
    }

    public Optional<Author> getById(final UUID id)
    {
        return this.read(() -> this.authors.query(GigaMapAuthorIndices.ID.is(id)).findFirst());
    }

    /**
     * 
     * @param containsNameSearch
     * @return
     * @see String#contains(CharSequence)
     */
    public List<Author> searchByName(final String containsNameSearch)
    {
        // TODO: replace with lucene search
        return this.read(() ->
        {
            try (final var storedAuthors = this.authors.query().stream())
            {
                return storedAuthors.limit(DEFAULT_PAGE_SIZE).toList();
            }
        });
    }

    public void insert(final Author author)
    {
        this.write(() ->
        {
            this.authors.add(author);
            this.authors.store();
        });
    }

    public void insertAll(final Iterable<Author> authors)
    {
        this.write(() ->
        {
            this.authors.addAll(authors);
            this.authors.store();
        });
    }

    /**
     * Updates all the fields of the author in the storage with the specified author
     * matching the id.
     * 
     * @param author the author containing all the updated fields and the same id as
     *               the author in the storage to update
     * @return <code>true</code> if the author was found and updated
     */
    public boolean update(final Author author)
    {
        return this.write(() ->
        {
            final Author storedAuthor = this.getById(author.id()).orElse(null);
            final boolean exists = storedAuthor != null;
            if (exists)
            {
                this.authors.replace(storedAuthor, author);
            }
            return exists;
        });
    }

    /**
     * Removes the author in the storage that matches the specified author id.
     * 
     * @param author the author to remove (checks for matching id)
     * @return <code>true</code> if the author has been removed from the storage
     */
    public boolean delete(final Author author)
    {
        final long removedId = this.write(() -> this.authors.remove(author));
        return removedId != -1;
    }
}
