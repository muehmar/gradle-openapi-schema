package com.github.muehmar.gradle.openapi;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.model.GenderDto;
import OpenApiSchema.example.api.model.LanguageDto;
import OpenApiSchema.example.api.model.UserDto;
import OpenApiSchema.example.api.model.UserGroupDto;
import OpenApiSchema.example.api.model.UserGroupLanguagesDto;
import OpenApiSchema.example.api.model.UserHobbiesDto;
import OpenApiSchema.example.api.model.UserInterestsDto;
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

class TestSerialisation {

  @Test
  void serializeAndDeserializeWithJackson() throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    final UserDto userBruce =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(555555L)
            .setUser("Bruce")
            .setCity("Winterthur")
            .andOptionals()
            .setRole(UserDto.RoleEnum.ADMIN)
            .build();

    final HashMap<String, ArrayList<UserInterestsDto>> interests = new HashMap<>();
    interests.put("IT", new ArrayList<>(singletonList(new UserInterestsDto("Programming", 10))));

    final HashMap<String, String> currencies = new HashMap<>();
    currencies.put("CHF", "Swiss Francs");

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
            .setLocationId("1TEST")
            .setBirthday(LocalDate.now())
            .setGender(GenderDto.FEMALE)
            .setAge(45)
            .setLastLogin(LocalDateTime.now())
            .setRole(UserDto.RoleEnum.USER)
            .setCurrencies(currencies)
            .setInterests(interests)
            .setLanguages(languages)
            .setHobbies(hobbies)
            .setData("Data")
            .build();

    final UserDto userFred =
        UserDto.newBuilder()
            .setId(UUID.randomUUID())
            .setExternalId(123456789L)
            .setUser("Fred")
            .setCity("San Diego")
            .build();

    final ArrayList<UserDto> members = new ArrayList<>();
    members.add(userBruce);
    members.add(userPaul);
    members.add(userFred);
    final ArrayList<UserGroupLanguagesDto> userGroupLanguages = new ArrayList<>();
    userGroupLanguages.add(new UserGroupLanguagesDto("GER", "German"));
    userGroupLanguages.add(new UserGroupLanguagesDto("END", "English"));
    final UserGroupDto userGroup =
        UserGroupDto.newBuilder()
            .andAllOptionals()
            .setOwner(userBruce)
            .setMembers(members)
            .setLanguages(userGroupLanguages)
            .build();

    final String json = writer.writeValueAsString(userGroup);
    System.out.println(json);

    final UserGroupDto deserializedUserGroup = mapper.readValue(json, UserGroupDto.class);

    assertEquals(userGroup, deserializedUserGroup);
  }
}
