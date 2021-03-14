package com.github.muehmar.gradle.openapi;

import OpenApiSchema.example.api.model.LanguageDto;
import OpenApiSchema.example.api.model.UserDto;
import OpenApiSchema.example.api.model.UserGroupDto;
import OpenApiSchema.example.api.model.UserHobbiesDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestSerialisation {

  @Test
  void serializeAndDeserializeWithJackson() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    final UserDto userBruce =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(555555)
            .setUser("Bruce")
            .setCity("Winterthur")
            .andOptionals()
            .setRole(UserDto.RoleEnum.Admin)
            .build();

    final LanguageDto langGerman = LanguageDto.newBuilder().setKey(55).setName("Deutsch").build();
    final LanguageDto langEnglish = LanguageDto.newBuilder().setKey(987).setName("English").build();
    final HashMap<String, LanguageDto> languages = new HashMap<>();
    languages.put("ger", langGerman);
    languages.put("eng", langEnglish);

    final UserHobbiesDto userHobbySport = UserHobbiesDto.newBuilder().setName("Sport").build();
    final HashMap<String, UserHobbiesDto> hobbies = new HashMap<>();
    hobbies.put("sport", userHobbySport);

    final UserDto userPaul =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(12L)
            .setUser("Paul")
            .setCity("Moskau")
            .andAllOptionals()
            .setBirthday(LocalDate.now())
            .setAge(45)
            .setLastLogin(LocalDateTime.now())
            .setRole(UserDto.RoleEnum.User)
            .setLanguages(languages)
            .setHobbies(hobbies)
            .build();

    final UserDto userFred =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(123456789)
            .setUser("Fred")
            .setCity("San Diego")
            .build();

    final ArrayList<UserDto> members = new ArrayList<>();
    members.add(userBruce);
    members.add(userPaul);
    members.add(userFred);
    final UserGroupDto userGroup =
        UserGroupDto.newBuilder().andAllOptionals().setOwner(userBruce).setMembers(members).build();

    final String json = writer.writeValueAsString(userGroup);
    System.out.println(json);

    final UserGroupDto deserializedUserGroup = mapper.readValue(json, UserGroupDto.class);

    assertEquals(userGroup, deserializedUserGroup);
  }
}
