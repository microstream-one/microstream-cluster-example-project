package one.microstream.bsr.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.stream.Streams;

import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Author;
import one.microstream.bsr.dto.AuthorDto;
import one.microstream.bsr.dto.GetAuthorByIdDto;
import one.microstream.bsr.dto.InsertAuthorDto;
import one.microstream.bsr.dto.SearchAuthorByNameDto;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.repository.AuthorRepository;

@Singleton
public class AuthorService
{
    private final AuthorRepository authors;

    public AuthorService(final AuthorRepository authors)
    {
        this.authors = authors;
    }

    public Optional<GetAuthorByIdDto> getById(final UUID id)
    {
        return this.authors.getById(id).map(GetAuthorByIdDto::from);
    }

    public List<SearchAuthorByNameDto> searchByName(final String containsNameSearch)
    {
        return this.authors.searchByName(containsNameSearch).stream().map(SearchAuthorByNameDto::from).toList();
    }

    public void insert(final InsertAuthorDto author)
    {
        this.authors.insert(this.toAuthor(author));
    }

    public void insertAll(final Iterable<InsertAuthorDto> authors)
    {
        final var convertedAuthors = Streams.of(authors).map(this::toAuthor).toList();
        this.authors.insertAll(convertedAuthors);
    }

    public boolean update(final AuthorDto author)
    {
        return this.authors.update(this.toAuthor(author));
    }

    public boolean delete(final UUID authorId)
    {
        return this.authors.delete(authorId);
    }

    public boolean deleteAll(final Iterable<UUID> authorIds)
    {
        return this.authors.deleteAll(authorIds);
    }

    private Author toAuthor(final InsertAuthorDto dto)
    {
        return new Author(UUID.randomUUID(), dto.name(), dto.about(), Collections.unmodifiableSet(new HashSet<>()));
    }

    private Author toAuthor(final AuthorDto dto) throws InvalidAuthorIdException
    {
        if (this.getById(dto.id()).isEmpty())
        {
            throw new InvalidAuthorIdException(dto.id());
        }
        return new Author(UUID.randomUUID(), dto.name(), dto.about(), Collections.unmodifiableSet(new HashSet<>()));
    }
}
