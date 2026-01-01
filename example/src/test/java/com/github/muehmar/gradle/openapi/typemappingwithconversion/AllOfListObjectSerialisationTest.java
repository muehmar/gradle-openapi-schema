package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Optionals.opt;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.AllOfListObjectAllOfDto.fullAllOfListObjectAllOfDtoBuilder;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.customList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.ListObjectDto.fullListObjectDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

public class AllOfListObjectSerialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void writeValueAsString_whenSuperUser_thenSerialiseCorrectly() throws Exception {
    final AllOfListObjectDto allOfListObjectDto =
        AllOfListObjectDto.builder()
            .setListObjectDto(
                fullListObjectDtoBuilder()
                    .setIds(customList(customString("id-1")))
                    .setUsernames(customList(customString("username-1")))
                    .setEmails(customList(customString("email-1")))
                    .setPhones(customList(customString("phone-1")))
                    .build())
            .setAllOfListObjectAllOfDto(
                fullAllOfListObjectAllOfDtoBuilder()
                    .setSuperUserId(opt(customString("super-user-id-1")))
                    .build())
            .build();

    assertEquals(
        "{\"emails\":[\"email-1\"],\"ids\":[\"id-1\"],\"phones\":[\"phone-1\"],\"superUserId\":\"super-user-id-1\",\"usernames\":[\"username-1\"]}",
        MAPPER.writeValueAsString(allOfListObjectDto));
  }
}
