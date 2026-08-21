package com.github.muehmar.gradle.openapi.issues.issue266;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * Issue 266: the property used as discriminator of a oneOf composition is a referenced enum which
 * is mapped to a custom type with a conversion. The fixed discriminator value of a subschema is
 * rendered into the builder of the parent dto, so it has to be converted to the mapped api type
 * instead of being assigned as a plain enum constant.
 *
 * <p>The conversion is defined on {@link MediaKindConversions}, i.e. on a class which differs from
 * the mapped type {@link MediaKind}. That class appears nowhere else in the generated parent dto,
 * so its import is only present if the refs registered by the rendered conversion are transferred
 * to the writer of the dto — otherwise the generated code does not compile. This test is therefore
 * primarily compile-level regression coverage.
 */
class Issue266MappedEnumDiscriminatorTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void builder_when_composedFromSubSchema_then_discriminatorValueConvertedToMappedType() {
    // The builder of the parent dto sets the fixed discriminator value 'book' of the subschema,
    // converted to the mapped type.
    final MappedBookDto book =
        MappedBookDto.fullMappedBookDtoBuilder()
            .setKind(MediaKindConversions.fromDto(MappedDiscriminatorKindDto.BOOK))
            .setTitle("Faust")
            .build();

    final MappedMediaDto media =
        MappedMediaDto.fullMappedMediaDtoBuilder().setMappedBookDto(book).build();

    assertEquals(MediaKindConversions.fromDto(MappedDiscriminatorKindDto.BOOK), media.getKind());
  }

  @Test
  void serialize_when_composedFromSubSchema_then_discriminatorSerializedAsEnumValue()
      throws Exception {
    final MappedMovieDto movie =
        MappedMovieDto.fullMappedMovieDtoBuilder()
            .setKind(MediaKindConversions.fromDto(MappedDiscriminatorKindDto.MOVIE))
            .setDuration(120)
            .build();

    final MappedMediaDto media =
        MappedMediaDto.fullMappedMediaDtoBuilder().setMappedMovieDto(movie).build();

    // MapperFactory enables SORT_PROPERTIES_ALPHABETICALLY, so properties are emitted sorted.
    assertEquals("{\"duration\":120,\"kind\":\"movie\"}", MAPPER.writeValueAsString(media));
  }

  @Test
  void deserialize_when_discriminatorMatchesSubSchema_then_foldsToThatSchema() throws Exception {
    final MappedMediaDto media =
        MAPPER.readValue("{\"kind\":\"book\",\"title\":\"Faust\"}", MappedMediaDto.class);

    final Set<ConstraintViolation<MappedMediaDto>> violations = validate(media);
    assertEquals(0, violations.size());
    assertTrue(media.isValid());

    final String title =
        media.foldOneOf(book -> book.getTitle(), movie -> String.valueOf(movie.getDuration()));
    assertEquals("Faust", title);
  }

  @Test
  void deserialize_when_discriminatorMatchesSubSchema_then_getterReturnsMappedType()
      throws Exception {
    final MappedMediaDto media =
        MAPPER.readValue("{\"kind\":\"movie\",\"duration\":120}", MappedMediaDto.class);

    final MappedMovieDto movie = media.foldOneOf(book -> null, m -> m);

    assertEquals(MediaKindConversions.fromDto(MappedDiscriminatorKindDto.MOVIE), movie.getKind());
  }
}
