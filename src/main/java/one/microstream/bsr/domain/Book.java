package one.microstream.bsr.domain;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * @param id              the unique identifier
 * @param isbn            the isbn identifier
 * @param title           the title of the book
 * @param description     the description of the book which can usually be found
 *                        on the back
 * @param pages           how many pages the book has
 * @param genres          the genres of the book
 * @param publicationDate when the book was published
 * @param author          the author of the book
 */
public record Book(
    UUID id,
    String isbn,
    String title,
    String description,
    int pages,
    Set<Genre> genres,
    LocalDate publicationDate,
    Author author
)
{
}
