package com.github.muehmar.gradle.openapi.nullableitemslist;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class SerialisationTest {
  private static final ObjectMapper OBJECT_MAPPER = MapperFactory.mapper();

  @Test
  void writeValueAsString_when_standardSetters_then_matchJson() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Collections.singletonList("user-1234"))
            .setEmails(Collections.singletonList("email-1234"))
            .setPhones(Collections.singletonList("phone-1234"))
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"usernames\":[\"user-1234\"]}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_standardOverloadedSetters_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Optional.of(Collections.singletonList("user-1234")))
            .setEmails(Optional.of(Collections.singletonList("email-1234")))
            .setPhones(Tristate.ofValue(Collections.singletonList("phone-1234")))
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"usernames\":[\"user-1234\"]}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_standardOverloadedEmptySetters_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Optional.empty())
            .setEmails(Optional.empty())
            .setPhones(Tristate.ofNull())
            .build();

    assertEquals(
        "{\"ids\":[\"id-1234\"],\"phones\":null,\"usernames\":null}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_standardOverloadedSettersAndAbsentTristate_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds(Collections.singletonList("id-1234"))
            .setUsernames(Optional.empty())
            .setEmails(Optional.empty())
            .setPhones(Tristate.ofAbsent())
            .build();

    assertEquals(
        "{\"ids\":[\"id-1234\"],\"usernames\":null}", OBJECT_MAPPER.writeValueAsString(userDto));
  }

  // -*-----
  @Test
  void writeValueAsString_when_nullableItemsSettersAndPresentItems_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(Collections.singletonList(Optional.of("id-1234")))
            .setUsernames_(Collections.singletonList(Optional.of("user-1234")))
            .setEmails_(Collections.singletonList(Optional.of("email-1234")))
            .setPhones_(Collections.singletonList(Optional.of("phone-1234")))
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"usernames\":[\"user-1234\"]}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_nullableItemsSettersAndAbsentItems_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(Collections.singletonList(Optional.empty()))
            .setUsernames_(Collections.singletonList(Optional.empty()))
            .setEmails_(Collections.singletonList(Optional.empty()))
            .setPhones_(Collections.singletonList(Optional.empty()))
            .build();

    assertEquals(
        "{\"emails\":[null],\"ids\":[null],\"phones\":[null],\"usernames\":[null]}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_overloadedNullableItemsSettersAndPresentItems_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(Collections.singletonList(Optional.of("id-1234")))
            .setUsernames_(Optional.of(Collections.singletonList(Optional.of("user-1234"))))
            .setEmails_(Optional.of(Collections.singletonList(Optional.of("email-1234"))))
            .setPhones_(Tristate.ofValue(Collections.singletonList(Optional.of("phone-1234"))))
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\"],\"ids\":[\"id-1234\"],\"phones\":[\"phone-1234\"],\"usernames\":[\"user-1234\"]}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_overloadedNullableItemsSettersAndMixedItems_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(
                Arrays.asList(Optional.of("id-1234"), Optional.empty(), Optional.of("id-5678")))
            .setUsernames_(
                Optional.of(
                    Arrays.asList(
                        Optional.of("user-1234"), Optional.empty(), Optional.of("user-5678"))))
            .setEmails_(
                Optional.of(
                    Arrays.asList(
                        Optional.of("email-1234"), Optional.of("email-5678"), Optional.empty())))
            .setPhones_(
                Tristate.ofValue(
                    Arrays.asList(
                        Optional.empty(), Optional.of("phone-1234"), Optional.of("phone-5678"))))
            .build();

    assertEquals(
        "{\"emails\":[\"email-1234\",\"email-5678\",null],\"ids\":[\"id-1234\",null,\"id-5678\"],\"phones\":[null,\"phone-1234\",\"phone-5678\"],\"usernames\":[\"user-1234\",null,\"user-5678\"]}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_overloadedNullableItemsSettersAndAbsentItems_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(Collections.singletonList(Optional.empty()))
            .setUsernames_(Optional.of(Collections.singletonList(Optional.empty())))
            .setEmails_(Optional.of(Collections.singletonList(Optional.empty())))
            .setPhones_(Tristate.ofValue(Collections.singletonList(Optional.empty())))
            .build();

    assertEquals(
        "{\"emails\":[null],\"ids\":[null],\"phones\":[null],\"usernames\":[null]}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_overloadedNullableItemsSettersAndNoLists_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(Collections.singletonList(Optional.empty()))
            .setUsernames_(Optional.empty())
            .setEmails_(Optional.empty())
            .setPhones_(Tristate.ofNull())
            .build();

    assertEquals(
        "{\"ids\":[null],\"phones\":null,\"usernames\":null}",
        OBJECT_MAPPER.writeValueAsString(userDto));
  }

  @Test
  void writeValueAsString_when_overloadedNullableItemsSettersAndTristateAbsent_then_matchJson()
      throws JsonProcessingException {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setIds_(Collections.singletonList(Optional.empty()))
            .setUsernames_(Optional.empty())
            .setEmails_(Optional.empty())
            .setPhones_(Tristate.ofAbsent())
            .build();

    assertEquals("{\"ids\":[null],\"usernames\":null}", OBJECT_MAPPER.writeValueAsString(userDto));
  }
}
