package one.microstream.bsr.controller;

import java.util.List;
import java.util.Optional;

import io.micrometer.observation.annotation.Observed;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Error;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Put;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import one.microstream.bsr.domain.Author;
import one.microstream.bsr.dto.AuthorDto;
import one.microstream.bsr.exception.IndexAlreadyExistsException;
import one.microstream.bsr.repository.AuthorRepository;

@Observed
@Controller("/author")
public class AuthorController
{
	private final AuthorRepository authors;

	public AuthorController(final AuthorRepository authors)
	{
		this.authors = authors;
	}

	@Error(exception = IndexAlreadyExistsException.class, status = HttpStatus.BAD_REQUEST)
	public String handleIndexAlreadyExistsException(final IndexAlreadyExistsException e)
	{
		return e.getMessage();
	}

	@Get("/count")
	public long getCount()
	{
		return this.authors.countAuthors();
	}

	@Get("/search")
	public List<AuthorDto> searchAuthors(
		@QueryValue("name") @NonNull @NotBlank final String name,
		@QueryValue("page") @Nullable @PositiveOrZero final Integer page,
		@QueryValue("pageSize") @Nullable @PositiveOrZero final Integer pageSize
	)
	{
		final List<Author> searchedAuthors;
		if (pageSize == null && page == null)
		{
			searchedAuthors = this.authors.searchAuthorsByName(name);
		}
		else if (pageSize == null)
		{
			searchedAuthors = this.authors.searchAuthorsByName(name, page);
		}
		else
		{
			searchedAuthors = this.authors.searchAuthorsByName(name, page, pageSize);
		}
		return searchedAuthors.stream().map(AuthorDto::new).toList();
	}

	@Get("/{email}")
	public AuthorDto getAuthorByEmail(@NonNull @NotBlank @PathVariable final String email)
	{
		return Optional.ofNullable(this.authors.getAuthorByEmail(email)).map(AuthorDto::new).orElse(null);
	}

	@Get("/id/{id}")
	public AuthorDto getAuthorById(@NonNull @PathVariable @Positive final Long id)
	{
		return Optional.ofNullable(this.authors.getAuthorById(id)).map(AuthorDto::new).orElse(null);
	}

	@Put
	public void putAuthor(@Body @Valid @NonNull final AuthorDto dto)
	{
		this.authors.insert(new Author(dto));
	}

	@Put("/batch")
	public void putAuthorBatch(@NotEmpty @Body @NonNull final List<@Valid @NonNull AuthorDto> dto)
	{
		try
		{
			this.authors.insertAll(dto.stream().map(Author::new).toList());
		}
		catch (final IllegalArgumentException e)
		{
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@Post("/clear")
	public void clearAuthors()
	{
		this.authors.clearAuthors();
	}
}
