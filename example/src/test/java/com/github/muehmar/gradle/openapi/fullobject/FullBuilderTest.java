package com.github.muehmar.gradle.openapi.fullobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import openapischema.example.api.fullobject.model.AdminDto;
import openapischema.example.api.fullobject.model.BaseDataDto;
import openapischema.example.api.fullobject.model.FullObjectDto;
import org.junit.jupiter.api.Test;

class FullBuilderTest {

  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void fullBuilderForFullObject_when_called_then_needsToSetAllProperties()
      throws JsonProcessingException {
    final FullObjectDto dto =
        FullObjectDto.fullBuilder()
            .setColor(BaseDataDto.ColorEnum.GREEN)
            .setAdminDto(AdminDto.fullBuilder().setType("tyüe").setAdminname("adminname").build())
            .setMessage("message")
            .build();

    assertEquals(
        "{\"adminname\":\"adminname\",\"color\":\"green\",\"message\":\"message\",\"type\":\"Admin\"}",
        MAPPER.writeValueAsString(dto));
  }
}
