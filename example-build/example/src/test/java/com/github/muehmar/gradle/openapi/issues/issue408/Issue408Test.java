package com.github.muehmar.gradle.openapi.issues.issue408;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomString;
import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Issue 408: required additional properties must respect type mappings with conversion. The value
 * schema of the additional properties is class-mapped to {@link CustomString}: the builder setters
 * accept the custom type (the staged builder generated uncompilable code before) and the getter
 * returns the custom type, while the value is stored and serialized in its internal representation.
 */
class Issue408Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void builder_when_requiredMappedAdditionalPropertySet_then_customTypedRoundTrip()
      throws Exception {
    final DataDto dto =
        DataDto.fullDataDtoBuilder()
            .addAdditionalProperty("reqAp", CustomString.fromString("hello"))
            .build();

    assertEquals(CustomString.fromString("hello"), dto.getReqAp());
    assertEquals("{\"reqAp\":\"hello\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_requiredMappedAdditionalProperty_then_customTypedGetter() throws Exception {
    final DataDto dto = MAPPER.readValue("{\"reqAp\":\"hello\",\"other\":\"x\"}", DataDto.class);

    assertEquals(CustomString.fromString("hello"), dto.getReqAp());
  }

  @Test
  void builder_when_requiredNullableMappedAdditionalPropertySet_then_customTypedRoundTrip()
      throws Exception {
    // The nullable value schema produces the Optional setter overload added for #394.
    final NullableDataDto dto =
        NullableDataDto.fullNullableDataDtoBuilder()
            .addAdditionalProperty("reqAp", CustomString.fromString("hello"))
            .build();

    assertEquals(Optional.of(CustomString.fromString("hello")), dto.getReqAp());
    assertEquals("{\"reqAp\":\"hello\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_requiredNullableMappedAdditionalPropertyNull_then_emptyOptional()
      throws Exception {
    final NullableDataDto dto = MAPPER.readValue("{\"reqAp\":null}", NullableDataDto.class);

    assertEquals(Optional.empty(), dto.getReqAp());
  }
}
