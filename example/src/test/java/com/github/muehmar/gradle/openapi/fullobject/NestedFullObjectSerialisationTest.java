package com.github.muehmar.gradle.openapi.fullobject;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.NullableAdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NestedFullObjectSerialisationTest {
  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void serialize_when_adminDto_then_correctJson() throws JsonProcessingException {
    final BaseDataDto baseDataDto =
        BaseDataDto.builder().setColor(BaseDataDto.ColorEnum.RED).setSchema("schema").build();
    final AdminDto adminDto =
        AdminDto.builder()
            .setType("type")
            .setAdminname("adminname")
            .andAllOptionals()
            .addAdditionalProperty("admin-prop", "value")
            .build();

    final FullObjectDto fullObjectDto =
        FullObjectDto.builder()
            .setBaseDataDto(baseDataDto)
            .setAdminDto(adminDto)
            .setRoute("route")
            .andAllOptionals()
            .setMessage("message")
            .addAdditionalProperty("hello", "world!")
            .build();

    final NestedFullObjectDto dto =
        NestedFullObjectDto.builder()
            .setFullObjectDto(fullObjectDto)
            .setAmount(15)
            .andAllOptionals()
            .setCode("code")
            .build();

    final String json = MAPPER.writeValueAsString(dto);

    assertEquals(
        "{\"adminname\":\"adminname\",\"amount\":15,\"code\":\"code\",\"color\":\"red\",\"message\":\"message\",\"type\":\"Admin\",\"schema\":\"schema\",\"admin-prop\":\"value\",\"route\":\"route\",\"hello\":\"world!\"}",
        json);
  }

  @Test
  void deserialize_when_json_then_correctOutput() throws JsonProcessingException {
    final NestedFullObjectDto dto =
        MAPPER.readValue(
            "{\"adminname\":\"adminname\",\"amount\":15,\"code\":\"code\",\"color\":\"red\",\"message\":\"message\",\"type\":\"Admin\",\"admin-prop\":\"value\",\"hello\":\"world!\"}",
            NestedFullObjectDto.class);

    assertEquals(Tristate.ofValue("world!"), dto.getAdditionalProperty("hello"));

    final HashMap<String, String> additionalProperties = new HashMap<>();
    additionalProperties.put("hello", "world!");
    additionalProperties.put("admin-prop", "value");
    assertEquals(
        additionalProperties,
        dto.getAdditionalProperties().stream()
            .collect(
                toMap(NullableAdditionalProperty::getName, prop -> prop.getValue().orElse(null))));
    assertEquals(15, dto.getAmount());
    assertEquals(Optional.of("code"), dto.getCodeOpt());

    final BaseDataDto expectedBaseDataDto =
        BaseDataDto.builder()
            .setColor(BaseDataDto.ColorEnum.RED)
            .setSchema("schema")
            .andAllOptionals()
            .setAdditionalProperties(new HashMap<>(additionalProperties))
            .addAdditionalProperty("type", "Admin")
            .addAdditionalProperty("adminname", "adminname")
            .addAdditionalProperty("message", "message")
            .build();

    final HashMap<String, Object> adminAdditionalProperties = new HashMap<>(additionalProperties);
    adminAdditionalProperties.put("message", "message");
    adminAdditionalProperties.put("color", BaseDataDto.ColorEnum.RED);

    final AdminDto expectedAdminDto =
        AdminDto.builder()
            .setType("Admin")
            .setAdminname("adminname")
            .andAllOptionals()
            .setAdditionalProperties((Map<String, String>) (Object) (adminAdditionalProperties))
            .build();

    dto.foldAnyOf(
        fullObjectDto -> {
          assertEquals("message", fullObjectDto.getMessageOr(""));
          assertEquals(expectedBaseDataDto, fullObjectDto.getBaseDataDto());
          fullObjectDto.foldOneOf(
              adminDto -> {
                assertEquals(expectedAdminDto, adminDto);
                return null;
              },
              userDto -> fail());
          return "";
        },
        memberDto -> fail("No member DTO expected"));
  }
}
