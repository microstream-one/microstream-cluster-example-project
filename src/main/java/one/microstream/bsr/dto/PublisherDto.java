package one.microstream.bsr.dto;

import java.util.List;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import one.microstream.bsr.domain.Publisher;

@Serdeable
public record PublisherDto(
	@Nullable @PositiveOrZero Long id,
	@NonNull @NotBlank String email,
	@NonNull @NotBlank String company,
	@NonNull @NotEmpty List<AddressDto> addresses
)
{
	public PublisherDto(@NonNull final Publisher publisher)
	{
		this(
			publisher.getId(),
			publisher.getEmail(),
			publisher.getCompany(),
			publisher.getAddresses().stream().map(AddressDto::new).toList()
		);
	}
}
