package one.microstream.bsr.dto;

import java.util.List;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import one.microstream.bsr.domain.Author;

@Serdeable
public record AuthorDto(
	@Nullable @PositiveOrZero Long id,
	@NonNull @NotBlank String email,
	@NonNull @NotBlank String firstname,
	@NonNull @NotBlank String lastname,
	@NonNull @NotEmpty List<AddressDto> addresses
)
{
	public AuthorDto(@NonNull final Author author)
	{
		this(
			author.getId(),
			author.getEmail(),
			author.getFirstname(),
			author.getLastname(),
			author.getAddresses().stream().map(AddressDto::new).toList()
		);
	}
}
