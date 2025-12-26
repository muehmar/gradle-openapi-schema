package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class PosologyDeserialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void readValue_whenPosology_thenDeserialisedCorrectly() throws Exception {
    final String json = "[\"1\",\"2\",\"3\",\"0\"]";

    final PosologyDto posologyDto = MAPPER.readValue(json, PosologyDto.class);

    assertEquals(
        CustomList.fromList(Stream.of("1", "2", "3", "0").map(CustomString::new).collect(toList())),
        posologyDto.getItems());
  }
}
