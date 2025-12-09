package one.microstream.bsr.dto;

import java.util.UUID;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import one.microstream.bsr.domain.Author;

@Serdeable
@Introspected
public record AuthorDto(
    @NonNull UUID id,
    @NonNull @NotBlank String name,
    @NonNull @NotBlank String about
)
{
    public static AuthorDto from(final Author author)
    {
        return new AuthorDto(author.id(), author.name(), author.about());
    }
}
