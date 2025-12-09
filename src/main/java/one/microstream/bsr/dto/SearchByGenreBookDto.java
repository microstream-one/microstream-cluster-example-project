package one.microstream.bsr.dto;

import java.util.Set;
import java.util.UUID;

import io.micronaut.core.annotation.Introspected;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import one.microstream.bsr.domain.Book;

@Serdeable
@Introspected
public record SearchByGenreBookDto(
    @NonNull UUID id,
    @NonNull @NotBlank String title,
    @NonNull Set<@NonNull @NotBlank String> genres,
    @NonNull UUID authorId
)
{
    public static SearchByGenreBookDto from(final Book book)
    {
        return new SearchByGenreBookDto(
            book.id(),
            book.title(),
            book.genres(),
            book.author().id()
        );
    }
}
