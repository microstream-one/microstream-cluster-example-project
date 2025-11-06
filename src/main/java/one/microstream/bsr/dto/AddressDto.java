package one.microstream.bsr.dto;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import one.microstream.bsr.domain.Address;

@Serdeable
public record AddressDto(
	@NonNull @NotBlank String address,
	@Nullable String address2,
	@Nullable String zip,
	@NonNull @NotBlank String city,
	@NonNull @NotBlank String country
)
{
	public AddressDto(@NonNull final Address address)
	{
		this(address.getAddress(), address.getAddress2(), address.getZip(), address.getCity(), address.getCountry());
	}
}
