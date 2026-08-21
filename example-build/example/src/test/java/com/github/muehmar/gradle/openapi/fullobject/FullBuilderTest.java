package com.github.muehmar.gradle.openapi.fullobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class FullBuilderTest {

  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void fullBuilderForFullObject_when_called_then_needsToSetAllProperties() throws Exception {
    final FullObjectDto dto =
        FullObjectDto.fullBuilder()
            .setColor(BaseDataDto.ColorEnum.GREEN)
            .setSchema("schema")
            .setAdminDto(AdminDto.fullBuilder().setType("type").setAdminname("adminname").build())
            .setRoute("route")
            .setMessage("message")
            .build();

    assertEquals(
        "{\"adminname\":\"adminname\",\"color\":\"green\",\"message\":\"message\",\"type\":\"Admin\",\"schema\":\"schema\",\"route\":\"route\"}",
        MAPPER.writeValueAsString(dto));
  }
}
