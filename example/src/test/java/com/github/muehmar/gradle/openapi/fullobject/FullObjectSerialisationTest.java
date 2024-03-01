package com.github.muehmar.gradle.openapi.fullobject;

import static com.github.muehmar.gradle.openapi.fullobject.AdminDto.adminDtoBuilder;
import static com.github.muehmar.gradle.openapi.fullobject.BaseDataDto.baseDataDtoBuilder;
import static com.github.muehmar.gradle.openapi.fullobject.FullObjectDto.fullObjectDtoBuilder;
import static com.github.muehmar.gradle.openapi.fullobject.UserDto.userDtoBuilder;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.NullableAdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class FullObjectSerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_adminDto_then_correctJson() throws JsonProcessingException {
    final BaseDataDto baseDataDto =
        baseDataDtoBuilder().setColor(BaseDataDto.ColorEnum.RED).setSchema("schema").build();
    final AdminDto adminDto =
        adminDtoBuilder()
            .setType("type")
            .setAdminname("adminname")
            .andAllOptionals()
            .addAdditionalProperty("admin-prop", "value")
            .build();

    final FullObjectDto dto =
        fullObjectDtoBuilder()
            .setBaseDataDto(baseDataDto)
            .setAdminDto(adminDto)
            .setRoute("route")
            .andAllOptionals()
            .setMessage("message")
            .addAdditionalProperty("hello", "world!")
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals(
        "{\"adminname\":\"adminname\",\"color\":\"red\",\"message\":\"message\",\"type\":\"Admin\",\"schema\":\"schema\",\"admin-prop\":\"value\",\"route\":\"route\",\"hello\":\"world!\"}",
        json);
  }

  @Test
  void deserialize_when_json_then_correctOutput() throws JsonProcessingException {
    final FullObjectDto dto =
        MAPPER.readValue(
            "{\"color\":\"red\",\"type\":\"User\",\"username\":\"username\",\"message\":\"message\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            FullObjectDto.class);

    assertEquals(Tristate.ofValue("world!"), dto.getAdditionalProperty("hello"));

    final HashMap<String, String> additionalProperties = new HashMap<>();
    additionalProperties.put("hello", "world!");
    additionalProperties.put("admin-prop", "value");
    assertEquals(
        additionalProperties,
        dto.getAdditionalProperties().stream()
            .collect(
                toMap(NullableAdditionalProperty::getName, prop -> prop.getValue().orElse(null))));

    final BaseDataDto expectedBaseDataDto =
        baseDataDtoBuilder()
            .setColor(BaseDataDto.ColorEnum.RED)
            .setSchema("schema")
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
        userDtoBuilder()
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
