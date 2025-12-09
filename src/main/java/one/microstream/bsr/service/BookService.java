package one.microstream.bsr.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.lang3.stream.Streams;
import org.apache.lucene.search.WildcardQuery;

import jakarta.inject.Singleton;
import one.microstream.bsr.domain.Author;
import one.microstream.bsr.domain.Book;
import one.microstream.bsr.dto.BookDto;
import one.microstream.bsr.dto.InsertBookDto;
import one.microstream.bsr.dto.SearchByAuthorBookDto;
import one.microstream.bsr.dto.SearchByGenreBookDto;
import one.microstream.bsr.dto.SearchByTitleBookDto;
import one.microstream.bsr.exception.InvalidAuthorIdException;
import one.microstream.bsr.exception.InvalidBookException;
import one.microstream.bsr.exception.InvalidGenreException;
import one.microstream.bsr.repository.AuthorRepository;
import one.microstream.bsr.repository.BookRepository;
import one.microstream.bsr.repository.GenreRepository;

@Singleton
public class BookService
{
    private final BookRepository books;
    private final AuthorRepository authors;
    private final GenreRepository genres;

    public BookService(final BookRepository books, final AuthorRepository authors, final GenreRepository genres)
    {
        this.books = books;
        this.authors = authors;
        this.genres = genres;
    }

    public Optional<BookDto> getById(final UUID id)
    {
        return this.books.getById(id).map(BookDto::from);
    }

    public Optional<BookDto> getByISBN(final String isbn)
    {
        return this.books.getByISBN(isbn).map(BookDto::from);
    }

    /**
     * Searches books by title using a {@link WildcardQuery}
     */
    public List<SearchByTitleBookDto> searchByTitle(final String titleWildcardSearch)
    {
        return this.books.searchByTitle(titleWildcardSearch).stream().map(SearchByTitleBookDto::from).toList();
    }

    public List<SearchByAuthorBookDto> searchByAuthor(final UUID authorId) throws InvalidAuthorIdException
    {
        final Author author = this.authors.getById(authorId).orElseThrow(() -> new InvalidAuthorIdException(authorId));
        return author.books().stream().map(SearchByAuthorBookDto::from).toList();
    }

    public List<SearchByGenreBookDto> searchByGenre(final Set<String> genres) throws InvalidGenreException
    {
        return this.books.searchByGenre(genres).stream().map(SearchByGenreBookDto::from).toList();
    }

    /**
     * 
     * @param book
     * @throws InvalidAuthorIdException if book contained an invalid author id
     */
    public void insert(final InsertBookDto book) throws InvalidAuthorIdException
    {
        this.books.insert(this.toBook(book));
    }

    /**
     * 
     * @param books
     * @throws InvalidAuthorIdException if a book contained an invalid author id
     */
    public void insertAll(final Iterable<InsertBookDto> books) throws InvalidAuthorIdException
    {
        final List<Book> convertedBooks = Streams.of(books).map(this::toBook).toList();
        this.books.insertAll(convertedBooks);
    }

    public boolean update(final BookDto book)
    {
        return this.books.update(this.toBook(book));
    }

    public boolean delete(final UUID bookId)
    {
        return this.books.delete(bookId);
    }

    public boolean deleteAll(final Iterable<UUID> bookIds)
    {
        return this.books.deleteAll(bookIds);
    }

    private Book toBook(final InsertBookDto dto) throws InvalidAuthorIdException
    {
        final var author = this.ensureExistingAuthor(dto.authorId());
        final var ensuredGenres = this.ensureExistingGenres(dto.genres());
        final var id = UUID.randomUUID();
        return new Book(
            id,
            dto.isbn(),
            dto.title(),
            dto.description(),
            dto.pages(),
            ensuredGenres,
            dto.publicationDate(),
            author
        );
    }

    private Book toBook(final BookDto dto) throws InvalidAuthorIdException, InvalidGenreException
    {
        if (this.getById(dto.id()).isEmpty())
        {
            throw new InvalidBookException(dto.id());
        }
        final var author = this.ensureExistingAuthor(dto.authorId());
        final var ensuredGenres = this.ensureExistingGenres(dto.genres());
        return new Book(
            dto.id(),
            dto.isbn(),
            dto.title(),
            dto.description(),
            dto.pages(),
            ensuredGenres,
            dto.publicationDate(),
            author
        );
    }

    private Author ensureExistingAuthor(final UUID authorId)
    {
        return this.authors.getById(authorId).orElseThrow(() -> new InvalidAuthorIdException(authorId));
    }

    private Set<String> ensureExistingGenres(final Set<String> genres) throws InvalidGenreException
    {
        final var storedGenres = this.genres.list();
        for (final var genre : genres)
        {
            if (!storedGenres.contains(genre))
            {
                throw new InvalidGenreException("Could not find genre '%s'".formatted(genre));
            }
        }
        return storedGenres;
    }
}
