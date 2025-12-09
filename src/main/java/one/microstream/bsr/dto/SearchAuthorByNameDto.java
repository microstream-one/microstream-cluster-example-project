package one.microstream.bsr.dto;

import java.util.UUID;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import one.microstream.bsr.domain.Author;

@Serdeable
@Introspected
public record SearchAuthorByNameDto(
    @NonNull UUID id,
    @NonNull @NotBlank String name
)
{
    public static SearchAuthorByNameDto from(final Author author)
    {
        return new SearchAuthorByNameDto(author.id(), author.name());
    }
}
