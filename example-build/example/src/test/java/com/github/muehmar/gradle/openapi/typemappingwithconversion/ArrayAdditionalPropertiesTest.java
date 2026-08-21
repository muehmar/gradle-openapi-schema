package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.typemappingwithconversion.ArrayAdditionalPropertiesDto.fullArrayAdditionalPropertiesDtoBuilder;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.customList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.AdditionalProperty;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

public class ArrayAdditionalPropertiesTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  private static final ArrayAdditionalPropertiesPropertyDto ARRAY_0 =
      ArrayAdditionalPropertiesPropertyDto.fromItems(customList(customString("value-0")));
  private static final ArrayAdditionalPropertiesDto DTO =
      fullArrayAdditionalPropertiesDtoBuilder().addAdditionalProperty("key-0", ARRAY_0).build();
  private static final String JSON = "{\"key-0\":[\"value-0\"]}";

  @Test
  void serialize_when_dto_then_correctJson() throws Exception {
    assertEquals(JSON, MAPPER.writeValueAsString(DTO));
  }

  @Test
  void deserialize_when_json_then_correctDto() throws Exception {
    final ArrayAdditionalPropertiesDto actual =
        MAPPER.readValue(JSON, ArrayAdditionalPropertiesDto.class);
    assertEquals(DTO, actual);

    final List<AdditionalProperty<ArrayAdditionalPropertiesPropertyDto>> additionalProperties =
        actual.getAdditionalProperties();

    assertEquals(
        Collections.singletonList(new AdditionalProperty<>("key-0", ARRAY_0)),
        additionalProperties);
  }
}
