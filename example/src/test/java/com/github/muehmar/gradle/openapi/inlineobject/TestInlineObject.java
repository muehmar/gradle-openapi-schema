package com.github.muehmar.gradle.openapi.inlineobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import openapischema.example.api.inlineobject.model.CustomerDto;
import openapischema.example.api.inlineobject.model.CustomerInlineObjectAnotherObjectDto;
import openapischema.example.api.inlineobject.model.CustomerInlineObjectDto;
import org.junit.jupiter.api.Test;

class TestInlineObject {

  @Test
  void customerDtoIsSuccessfullyGeneratedAndCanBeUsed() {
    final CustomerInlineObjectAnotherObjectDto anotherObjectDto =
        CustomerInlineObjectAnotherObjectDto.newBuilder()
            .setKey("key")
            .andAllOptionals()
            .setData("data")
            .build();
    final CustomerInlineObjectDto customerInlineObjectDto =
        CustomerInlineObjectDto.newBuilder()
            .setKey("key")
            .andAllOptionals()
            .setData("data")
            .setAnotherObject(anotherObjectDto)
            .build();

    final CustomerDto customerDto =
        CustomerDto.newBuilder().setInlineObject(customerInlineObjectDto).andAllOptionals().build();

    assertEquals(customerInlineObjectDto, customerDto.getInlineObject());
    assertEquals(
        Optional.of(anotherObjectDto), customerDto.getInlineObject().getAnotherObjectOpt());
  }
}
