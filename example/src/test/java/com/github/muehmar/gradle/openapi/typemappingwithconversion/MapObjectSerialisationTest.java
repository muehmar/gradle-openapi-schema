package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Maps.map;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.MapObjectDto.fullMapObjectDtoBuilder;
import static java.util.Collections.emptyMap;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class MapObjectSerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_mapObjectDto_then_correctJson() throws JsonProcessingException {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(map("usernames-k-1", customString("usernames-v-1")))
            .setEmailsMap(map("emails-k-1", customString("emails-v-1")))
            .setPhonesMap(map("phones-k-1", customString("phones-v-1")))
            .build();

    assertEquals(
        "{\"emailsMap\":{\"emails-k-1\":\"emails-v-1\"},\"idsMap\":{\"id-k-1\":\"id-v-1\"},\"phonesMap\":{\"phones-k-1\":\"phones-v-1\"},\"usernamesMap\":{\"usernames-k-1\":\"usernames-v-1\"}}",
        MAPPER.writeValueAsString(mapObjectDto));
  }

  @Test
  void writeValueAsString_when_mapObjectDtoAbsentOrNullable_then_correctJson()
      throws JsonProcessingException {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(emptyMap())
            .setUsernamesMap(empty())
            .setEmailsMap(empty())
            .setPhonesMap(Tristate.ofAbsent())
            .build();

    assertEquals("{\"idsMap\":{},\"usernamesMap\":null}", MAPPER.writeValueAsString(mapObjectDto));
  }

  @Test
  void writeValueAsString_when_mapObjectDtoTristateNull_then_correctJson()
      throws JsonProcessingException {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(emptyMap())
            .setUsernamesMap(empty())
            .setEmailsMap(empty())
            .setPhonesMap(Tristate.ofNull())
            .build();

    assertEquals(
        "{\"idsMap\":{},\"phonesMap\":null,\"usernamesMap\":null}",
        MAPPER.writeValueAsString(mapObjectDto));
  }
}
