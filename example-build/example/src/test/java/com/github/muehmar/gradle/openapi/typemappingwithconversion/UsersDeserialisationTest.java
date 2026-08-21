package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Optionals.opt;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.customList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.ListObjectDto.fullListObjectDtoBuilder;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class UsersDeserialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void readValue_when_ListObjectDto_then_matchExpected() throws Exception {
    final ListObjectDto ListObjectDto =
        MAPPER.readValue(
            "{\"emails\":[\"email-1\"],\"ids\":[\"id-1\"],\"phones\":[\"phone-1\"],\"usernames\":[\"username-1\"]}",
            ListObjectDto.class);

    final ListObjectDto expectedListObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    assertEquals(expectedListObjectDto, ListObjectDto);
  }

  @Test
  void readValue_when_ListObjectDtoWithNullableItems_then_matchExpected() throws Exception {
    final ListObjectDto ListObjectDto =
        MAPPER.readValue(
            "{\"emails\":[\"email-1\",null],\"ids\":[\"id-1\",null],\"phones\":[\"phone-1\",null],\"usernames\":[\"username-1\",null]}",
            ListObjectDto.class);

    final ListObjectDto expectedListObjectDto =
        fullListObjectDtoBuilder()
            .setIds_(customList(opt(customString("id-1")), empty()))
            .setUsernames_(opt(customList(opt(customString("username-1")), empty())))
            .setEmails_(opt(customList(opt(customString("email-1")), empty())))
            .setPhones_(Tristate.ofValue(customList(opt(customString("phone-1")), empty())))
            .build();

    assertEquals(expectedListObjectDto, ListObjectDto);
  }

  @Test
  void readValue_when_ListObjectDtoAbsentOrNullable_then_matchExpected() throws Exception {
    final ListObjectDto listObjectDto =
        MAPPER.readValue("{\"ids\":[\"id-1\"],\"usernames\":null}", ListObjectDto.class);

    final ListObjectDto expectedListObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(empty())
            .setEmails(empty())
            .setPhones(Tristate.ofAbsent())
            .build();

    assertEquals(expectedListObjectDto, listObjectDto);
  }

  @Test
  void readValue_when_listObjectDtoTristateNull_then_matchExpected() throws Exception {
    final ListObjectDto listObjectDto =
        MAPPER.readValue(
            "{\"ids\":[\"id-1\"],\"phones\":null,\"usernames\":null}", ListObjectDto.class);

    final ListObjectDto expectedListObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(empty())
            .setEmails(empty())
            .setPhones(Tristate.ofNull())
            .build();

    assertEquals(expectedListObjectDto, listObjectDto);
  }
}
