package com.github.muehmar.gradle.openapi.fullobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.HashMap;
import java.util.Optional;
import openapischema.example.api.fullobject.model.AdminDto;
import openapischema.example.api.fullobject.model.BaseDataDto;
import openapischema.example.api.fullobject.model.FullObjectDto;
import openapischema.example.api.fullobject.model.UserDto;
import org.junit.jupiter.api.Test;

class FullObjectSerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_adminDto_then_correctJson() throws JsonProcessingException {
    final BaseDataDto baseDataDto =
        BaseDataDto.newBuilder().setColor(BaseDataDto.ColorEnum.RED).build();
    final AdminDto adminDto =
        AdminDto.newBuilder()
            .setType("type")
            .setAdminname("adminname")
            .andAllOptionals()
            .addAdditionalProperty("admin-prop", "value")
            .build();

    final FullObjectDto dto =
        FullObjectDto.newBuilder()
            .setBaseDataDto(baseDataDto)
            .setAdminDto(adminDto)
            .setRoute("route")
            .andAllOptionals()
            .setMessage("message")
            .addAdditionalProperty("hello", "world!")
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals(
        "{\"adminname\":\"adminname\",\"color\":\"red\",\"message\":\"message\",\"type\":\"Admin\",\"admin-prop\":\"value\",\"route\":\"route\",\"hello\":\"world!\"}",
        json);
  }

  @Test
  void deserialize_when_json_then_correctOutput() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    assertEquals(Optional.of("world!"), dto.getAdditionalProperty("hello"));

    final HashMap<String, String> additionalProperties = new HashMap<>();
    additionalProperties.put("hello", "world!");
    additionalProperties.put("admin-prop", "value");
    assertEquals(additionalProperties, dto.getAdditionalProperties());

    final BaseDataDto expectedBaseDataDto =
        BaseDataDto.newBuilder()
            .setColor(BaseDataDto.ColorEnum.RED)
            .andAllOptionals()
            .setAdditionalProperties(new HashMap<>(additionalProperties))
            .addAdditionalProperty("type", "User")
            .addAdditionalProperty("username", "username")
            .addAdditionalProperty("message", "message")
            .build();

    final HashMap<String, Object> adminAdditionalProperties = new HashMap<>(additionalProperties);
    adminAdditionalProperties.put("message", "message");
    adminAdditionalProperties.put("color", BaseDataDto.ColorEnum.RED);

    final UserDto expectedUser =
        UserDto.newBuilder()
            .setType("User")
            .setUsername("username")
            .andAllOptionals()
            .setAdditionalProperties(adminAdditionalProperties)
            .build();

    assertEquals("message", dto.getMessageOr(""));
    assertEquals(expectedBaseDataDto, dto.getBaseDataDto());
    dto.foldOneOf(
        adminDto -> fail("Not expected to get an AdminDto"),
        userDto -> {
          assertEquals(expectedUser, userDto);
          return null;
        });
  }
}
