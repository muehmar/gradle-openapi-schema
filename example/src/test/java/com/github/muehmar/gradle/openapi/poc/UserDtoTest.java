package com.github.muehmar.gradle.openapi.poc;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class UserDtoTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize() throws JsonProcessingException {
    final UserDto userDto =
        UserDto.userDtoBuilder()
            .setId("id")
            .setUsername("username")
            .andOptionals()
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("foo", Tristate.ofValue("bar"))
            .addAdditionalProperty("baz", Tristate.ofAbsent())
            .addAdditionalProperty("qux", Tristate.ofNull())
            .build();

    final String json = MAPPER.writeValueAsString(userDto);
    assertEquals(
        "{\"id\":\"id\",\"username\":\"username\",\"qux\":null,\"foo\":\"bar\",\"hello\":\"world\"}",
        json);
  }

  @Test
  void deserialize() throws JsonProcessingException {
    final String json =
        "{\"id\":\"id\",\"username\":\"username\",\"qux\":null,\"foo\":\"bar\",\"hello\":\"world\"}";

    final UserDto deserializedDto = MAPPER.readValue(json, UserDto.class);

    final UserDto userDto =
        UserDto.userDtoBuilder()
            .setId("id")
            .setUsername("username")
            .andOptionals()
            .addAdditionalProperty("hello", "world")
            .addAdditionalProperty("foo", Tristate.ofValue("bar"))
            .addAdditionalProperty("baz", Tristate.ofAbsent())
            .addAdditionalProperty("qux", Tristate.ofNull())
            .build();

    assertEquals(userDto, deserializedDto);

    assertEquals(Tristate.ofValue("world"), deserializedDto.getAdditionalProperty("hello"));
    assertEquals(Tristate.ofValue("bar"), deserializedDto.getAdditionalProperty("foo"));
    assertEquals(Tristate.ofAbsent(), deserializedDto.getAdditionalProperty("baz"));
    assertEquals(Tristate.ofNull(), deserializedDto.getAdditionalProperty("qux"));
  }
}
