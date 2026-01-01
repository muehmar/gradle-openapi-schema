package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class PosologySerialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void writeValueAsString_whenPosology_thenSerialiseCorrectly() throws Exception {
    final PosologyDto posologyDto =
        PosologyDto.fromItems(
            CustomList.fromList(
                Stream.of("1", "2", "3", "0").map(CustomString::new).collect(toList())));

    final String json = MAPPER.writeValueAsString(posologyDto);

    assertEquals("[\"1\",\"2\",\"3\",\"0\"]", json);
  }
}
