package one.microstream.bsr.dto;

import java.time.LocalDate;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import one.microstream.bsr.domain.Book;

@Serdeable
public record BookReferenceDto(
	@PositiveOrZero long id,
	@NonNull @NotBlank String isbn,
	@NonNull @NotBlank String title,
	@NonNull LocalDate publicationDate,
	@PositiveOrZero int edition,
	@PositiveOrZero int availableQuantity,
	@PositiveOrZero int priceEuroCent,
	@Positive long author,
	@Positive long publisher
)
{
	public BookReferenceDto(final Book book)
	{
		this(
			book.getId(),
			book.getIsbn(),
			book.getTitle(),
			book.getPublicationDate(),
			book.getEdition(),
			book.getAvailableQuantity(),
			book.getPriceEuroCent(),
			book.getAuthor().getId(),
			book.getPublisher().getId()
		);
	}
}
