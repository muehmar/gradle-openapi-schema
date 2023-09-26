package com.github.muehmar.gradle.openapi.nullability;

import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import java.util.stream.Stream;
import openapischema.example.api.nullability.model.UserDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TestSerialisation {
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeAll
  static void setupMapper() {
    MAPPER.setConfig(
        MAPPER.getSerializationConfig().with(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY));
  }

  @Test
  void deserialize_when_onlyIdPresent_then_correctDeserialized() throws JsonProcessingException {
    final UserDto dto = MAPPER.readValue("{\"id\":\"123abc\",\"username\":null}", UserDto.class);

    assertEquals("123abc", dto.getId());
    assertEquals(empty(), dto.getUsernameOpt());
    assertEquals(empty(), dto.getEmailOpt());
    assertEquals(Tristate.ofAbsent(), dto.getPhoneTristate());
  }

  @Test
  void deserialize_when_usernameNotNull_then_correctDeserialized() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue("{\"id\":\"123abc\",\"username\":\"Dexter\"}", UserDto.class);

    assertEquals("123abc", dto.getId());
    assertEquals(Optional.of("Dexter"), dto.getUsernameOpt());
    assertEquals(empty(), dto.getEmailOpt());
    assertEquals(Tristate.ofAbsent(), dto.getPhoneTristate());
  }

  @Test
  void deserialize_when_emailPresent_then_correctDeserialized() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":\"Dexter\",\"email\":\"hello@github.com\"}",
            UserDto.class);

    assertEquals("123abc", dto.getId());
    assertEquals(Optional.of("Dexter"), dto.getUsernameOpt());
    assertEquals(Optional.of("hello@github.com"), dto.getEmailOpt());
    assertEquals(Tristate.ofAbsent(), dto.getPhoneTristate());
  }

  @Test
  void deserialize_when_phoneNull_then_correctDeserialized() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":\"Dexter\",\"email\":\"hello@github.com\",\"phone\":null}",
            UserDto.class);

    assertEquals("123abc", dto.getId());
    assertEquals(Optional.of("Dexter"), dto.getUsernameOpt());
    assertEquals(Optional.of("hello@github.com"), dto.getEmailOpt());
    assertEquals(Tristate.ofNull(), dto.getPhoneTristate());
  }

  @Test
  void deserialize_when_phonePresent_then_correctDeserialized() throws JsonProcessingException {
    final UserDto dto =
        MAPPER.readValue(
            "{\"id\":\"123abc\",\"username\":\"Dexter\",\"email\":\"hello@github.com\",\"phone\":\"00411234567\"}",
            UserDto.class);

    assertEquals("123abc", dto.getId());
    assertEquals(Optional.of("Dexter"), dto.getUsernameOpt());
    assertEquals(Optional.of("hello@github.com"), dto.getEmailOpt());
    assertEquals(Tristate.ofValue("00411234567"), dto.getPhoneTristate());
  }

  @Test
  void serialize_when_usernameAbsent_then_serializedAsNull() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.builder()
            .setId("123abc")
            .setUsername(empty())
            .andAllOptionals()
            .setEmail(empty())
            .setPhone(Tristate.ofAbsent())
            .build();

    final String output = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"id\":\"123abc\",\"username\":null}", output);
  }

  @Test
  void serialize_when_usernamePresent_then_serialized() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.builder()
            .setId("123abc")
            .setUsername("Dexter")
            .andAllOptionals()
            .setEmail(empty())
            .setPhone(Tristate.ofAbsent())
            .build();

    final String output = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"id\":\"123abc\",\"username\":\"Dexter\"}", output);
  }

  @Test
  void serialize_when_emailPresent_then_serialized() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.builder()
            .setId("123abc")
            .setUsername(empty())
            .andAllOptionals()
            .setEmail("hello@mail.ch")
            .setPhone(Tristate.ofAbsent())
            .build();
    final String output = MAPPER.writeValueAsString(userDto);

    assertEquals("{\"email\":\"hello@mail.ch\",\"id\":\"123abc\",\"username\":null}", output);
  }

  @ParameterizedTest
  @MethodSource("phoneVariants")
  void serialize_when_phoneAbsent_then_noPhoneSerialized(
      Tristate<String> phone, String expectedOutput) throws JsonProcessingException {
    final UserDto userDto =
        UserDto.builder()
            .setId("123abc")
            .setUsername(empty())
            .andAllOptionals()
            .setEmail(empty())
            .setPhone(phone)
            .build();
    final String output = MAPPER.writeValueAsString(userDto);

    assertEquals(expectedOutput, output);
  }

  public static Stream<Arguments> phoneVariants() {
    return Stream.of(
        Arguments.of(Tristate.ofNull(), "{\"id\":\"123abc\",\"phone\":null,\"username\":null}"),
        Arguments.of(Tristate.ofAbsent(), "{\"id\":\"123abc\",\"username\":null}"),
        Arguments.of(
            Tristate.ofValue("00411234567"),
            "{\"id\":\"123abc\",\"phone\":\"00411234567\",\"username\":null}"));
  }
}
