package com.github.muehmar.gradle.openapi.issues.issue438;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A property whose name equals the companion flag field of one of its siblings, see issue #438. The
 * flag field is private and only ever read within its own class, hence it is renamed while the
 * property keeps its name; that the dtos compile at all is the main assertion.
 */
class Issue438FlagFieldTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_siblingNamedAfterThePresenceFlag_then_bothPropertiesMapped()
      throws Exception {
    final PresenceFlagFieldCollisionDto dto =
        MAPPER.readValue(
            "{\"name\":\"aName\",\"isNamePresent\":\"aValue\"}",
            PresenceFlagFieldCollisionDto.class);

    // The property, not the flag of 'name'.
    assertEquals("aValue", dto.getIsNamePresent());
    assertEquals(Optional.of("aName"), dto.getNameOpt());
  }

  @Test
  void serialize_when_siblingNamedAfterThePresenceFlag_then_bothPropertiesWritten()
      throws Exception {
    final PresenceFlagFieldCollisionDto dto =
        PresenceFlagFieldCollisionDto.builder()
            .setName("aName")
            .setIsNamePresent("aValue")
            .andOptionals()
            .build();

    assertEquals(
        "{\"isNamePresent\":\"aValue\",\"name\":\"aName\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void
      serialize_when_requiredNullablePropertyIsExplicitlyNull_then_flagOfTheSiblingNotConfusedWithIt()
          throws Exception {
    final PresenceFlagFieldCollisionDto dto =
        MAPPER.readValue(
            "{\"name\":null,\"isNamePresent\":\"aValue\"}", PresenceFlagFieldCollisionDto.class);

    // 'name' is present and null, which only the renamed flag field can express.
    assertEquals(Optional.empty(), dto.getNameOpt());
    assertEquals("aValue", dto.getIsNamePresent());
    assertTrue(dto.isValid());
    assertEquals("{\"isNamePresent\":\"aValue\",\"name\":null}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void validate_when_requiredNullablePropertyIsAbsent_then_invalid() throws Exception {
    final PresenceFlagFieldCollisionDto dto =
        MAPPER.readValue("{\"isNamePresent\":\"aValue\"}", PresenceFlagFieldCollisionDto.class);

    // The flag of 'name' stays false even though the sibling of that name is set.
    assertFalse(dto.isValid());
    assertFalse(validate(dto).isEmpty());
  }

  @Test
  void deserialize_when_siblingNamedAfterTheNotNullFlag_then_bothPropertiesMapped()
      throws Exception {
    final NotNullFlagFieldCollisionDto dto =
        MAPPER.readValue(
            "{\"name\":\"aName\",\"isNameNotNull\":\"aValue\"}",
            NotNullFlagFieldCollisionDto.class);

    assertEquals(Optional.of("aName"), dto.getNameOpt());
    assertEquals("aValue", dto.getIsNameNotNull());
    assertTrue(dto.isValid());
    assertTrue(validate(dto).isEmpty());
  }

  @Test
  void serialize_when_optionalPropertyIsAbsent_then_onlyTheSiblingWritten() throws Exception {
    final NotNullFlagFieldCollisionDto dto =
        NotNullFlagFieldCollisionDto.builder().setIsNameNotNull("aValue").andOptionals().build();

    assertEquals(Optional.empty(), dto.getNameOpt());
    assertEquals("{\"isNameNotNull\":\"aValue\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_siblingNamedAfterTheNullFlag_then_bothPropertiesMapped() throws Exception {
    final NullFlagFieldCollisionDto dto =
        MAPPER.readValue(
            "{\"name\":null,\"isNameNull\":\"aValue\"}", NullFlagFieldCollisionDto.class);

    // The tristate is driven by the renamed flag field, not by the sibling property.
    assertEquals(Tristate.ofNull(), dto.getNameTristate());
    assertEquals("aValue", dto.getIsNameNull());
  }

  @Test
  void serialize_when_tristatePropertyIsAbsent_then_onlyTheSiblingWritten() throws Exception {
    final NullFlagFieldCollisionDto dto =
        MAPPER.readValue("{\"isNameNull\":\"aValue\"}", NullFlagFieldCollisionDto.class);

    assertEquals(Tristate.ofAbsent(), dto.getNameTristate());
    assertEquals("{\"isNameNull\":\"aValue\"}", MAPPER.writeValueAsString(dto));
  }
}
