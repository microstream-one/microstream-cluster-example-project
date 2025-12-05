package one.microstream.bsr.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.serde.annotation.Serdeable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import one.microstream.bsr.domain.Genre;

@Serdeable
public record InsertBookDto(
    @NonNull @NotBlank String isbn,
    @NonNull @NotBlank String title,
    @NonNull @NotBlank String description,
    @Positive int pages,
    @NonNull Set<Genre> genres,
    @NonNull LocalDate publicationDate,
    @NonNull @NotBlank UUID authorId
)
{
}
