package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class PosologySerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_whenPosology_thenSerialiseCorrectly() throws JsonProcessingException {
    final PosologyDto posologyDto =
        PosologyDto.fromItems(
            CustomList.fromList(
                Stream.of("1", "2", "3", "0").map(CustomString::new).collect(toList())));

    final String json = MAPPER.writeValueAsString(posologyDto);

    assertEquals("[\"1\",\"2\",\"3\",\"0\"]", json);
  }
}
