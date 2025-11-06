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
import one.microstream.bsr.domain.Publisher;
import one.microstream.bsr.dto.PublisherDto;
import one.microstream.bsr.exception.IndexAlreadyExistsException;
import one.microstream.bsr.repository.PublisherRepository;

@Observed
@Controller("/publisher")
public class PublisherController
{
	private final PublisherRepository publishers;

	public PublisherController(final PublisherRepository publishers)
	{
		this.publishers = publishers;
	}

	@Error(exception = IndexAlreadyExistsException.class, status = HttpStatus.BAD_REQUEST)
	public String handleIndexAlreadyExistsException(final IndexAlreadyExistsException e)
	{
		return e.getMessage();
	}

	@Get("/count")
	public long getCount()
	{
		return this.publishers.countPublishers();
	}

	@Get("/search")
	public List<PublisherDto> searchPublishers(
		@QueryValue("company") @NonNull @NotBlank final String company,
		@QueryValue("page") @Nullable @PositiveOrZero final Integer page,
		@QueryValue("pageSize") @Nullable @PositiveOrZero final Integer pageSize
	)
	{
		final List<Publisher> searchedPublishers;
		if (pageSize == null && page == null)
		{
			searchedPublishers = this.publishers.searchPublishersByCompany(company);
		}
		else if (pageSize == null)
		{
			searchedPublishers = this.publishers.searchPublishersByCompany(company, page);
		}
		else
		{
			searchedPublishers = this.publishers.searchPublishersByCompany(company, page, pageSize);
		}
		return searchedPublishers.stream().map(PublisherDto::new).toList();
	}

	@Get("/{email}")
	public PublisherDto getPublisherByEmail(@NonNull @NotBlank @PathVariable final String email)
	{
		return Optional.ofNullable(this.publishers.getPublisherByEmail(email)).map(PublisherDto::new).orElse(null);
	}

	@Get("/id/{id}")
	public PublisherDto getPublisherById(@NonNull @PathVariable @Positive final Long id)
	{
		return Optional.ofNullable(this.publishers.getPublisherById(id)).map(PublisherDto::new).orElse(null);
	}

	@Put
	public void putPublisher(@Body @Valid @NonNull final PublisherDto dto)
	{
		this.publishers.insert(new Publisher(dto));
	}

	@Put("/batch")
	public void putPublisherBatch(@NotEmpty @Body @NonNull final List<@Valid @NonNull PublisherDto> dto)
	{
		try
		{
			this.publishers.insertAll(dto.stream().map(Publisher::new).toList());
		}
		catch (final IllegalArgumentException e)
		{
			throw new HttpStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@Post("/clear")
	public void clearPublishers()
	{
		this.publishers.clearPublishers();
	}
}
