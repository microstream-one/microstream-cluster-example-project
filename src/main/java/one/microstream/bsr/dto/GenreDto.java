package one.microstream.bsr.dto;

import java.io.IOException;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.core.type.Argument;
import io.micronaut.serde.Decoder;
import io.micronaut.serde.Encoder;
import io.micronaut.serde.annotation.Serdeable;

@Serdeable.Serializable(using = GenreDto.Serde.class)
@Serdeable.Deserializable(using = GenreDto.Serde.class)
public record GenreDto(String value)
{
    public class Serde implements io.micronaut.serde.Serde<GenreDto>
    {
        @Override
        public void serialize(
            @NonNull final Encoder encoder,
            @NonNull final EncoderContext context,
            @NonNull final Argument<? extends GenreDto> type,
            @NonNull final GenreDto value
        ) throws IOException
        {
            encoder.encodeString(value.value());
        }

        @Override
        public @Nullable GenreDto deserialize(
            @NonNull final Decoder decoder,
            @NonNull final DecoderContext context,
            @NonNull final Argument<? super GenreDto> type
        ) throws IOException
        {
            return new GenreDto(decoder.decodeString());
        }
    }
}
