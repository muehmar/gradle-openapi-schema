package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Optionals.opt;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.customList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.ListObjectDto.fullListObjectDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

public class AllOfListObjectDeserialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void readValue_then_allOfListObjectDto_then_matchExpected() throws JsonProcessingException {
    final AllOfListObjectDto allOfListObjectDto =
        MAPPER.readValue(
            "{\"emails\":[\"email-1\"],\"ids\":[\"id-1\"],\"phones\":[\"phone-1\"],\"usernames\":[\"username-1\"],\"superUserId\":\"super-user-id-1\"}",
            AllOfListObjectDto.class);

    final AllOfListObjectDto expectedAllOfListObjectDto =
        AllOfListObjectDto.builder()
            .setListObjectDto(
                fullListObjectDtoBuilder()
                    .setIds(customList(customString("id-1")))
                    .setUsernames(customList(customString("username-1")))
                    .setEmails(customList(customString("email-1")))
                    .setPhones(customList(customString("phone-1")))
                    .build())
            .setAllOfListObjectAllOfDto(
                AllOfListObjectAllOfDto.fullAllOfListObjectAllOfDtoBuilder()
                    .setSuperUserId(opt(customString("super-user-id-1")))
                    .build())
            .build();

    assertEquals(expectedAllOfListObjectDto, allOfListObjectDto);
  }
}
