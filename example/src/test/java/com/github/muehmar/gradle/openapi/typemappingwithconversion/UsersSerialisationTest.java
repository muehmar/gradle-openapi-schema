package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Optionals.opt;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.customList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.ListObjectDto.fullListObjectDtoBuilder;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class UsersSerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_ListObjectDto_then_correctJson() throws JsonProcessingException {
    final ListObjectDto ListObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    assertEquals(
        "{\"emails\":[\"email-1\"],\"ids\":[\"id-1\"],\"phones\":[\"phone-1\"],\"usernames\":[\"username-1\"]}",
        MAPPER.writeValueAsString(ListObjectDto));
  }

  @Test
  void writeValueAsString_when_ListObjectDtoWithNullableItems_then_correctJson()
      throws JsonProcessingException {
    final ListObjectDto listObjectDto =
        fullListObjectDtoBuilder()
            .setIds_(customList(opt(customString("id-1")), empty()))
            .setUsernames_(opt(customList(opt(customString("username-1")), empty())))
            .setEmails_(opt(customList(opt(customString("email-1")), empty())))
            .setPhones_(Tristate.ofValue(customList(opt(customString("phone-1")), empty())))
            .build();

    assertEquals(
        "{\"emails\":[\"email-1\",null],\"ids\":[\"id-1\",null],\"phones\":[\"phone-1\",null],\"usernames\":[\"username-1\",null]}",
        MAPPER.writeValueAsString(listObjectDto));
  }

  @Test
  void writeValueAsString_when_listObjectDtoAbsentOrNullable_then_correctJson()
      throws JsonProcessingException {
    final ListObjectDto listObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(empty())
            .setEmails(empty())
            .setPhones(Tristate.ofAbsent())
            .build();

    assertEquals(
        "{\"ids\":[\"id-1\"],\"usernames\":null}", MAPPER.writeValueAsString(listObjectDto));
  }

  @Test
  void writeValueAsString_when_listObjectDtoTristateNull_then_correctJson()
      throws JsonProcessingException {
    final ListObjectDto listObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(empty())
            .setEmails(empty())
            .setPhones(Tristate.ofNull())
            .build();

    assertEquals(
        "{\"ids\":[\"id-1\"],\"phones\":null,\"usernames\":null}",
        MAPPER.writeValueAsString(listObjectDto));
  }
}
