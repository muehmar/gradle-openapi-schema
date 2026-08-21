package com.github.muehmar.gradle.openapi.inlineobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class TestInlineObject {

  @Test
  void customerDtoIsSuccessfullyGeneratedAndCanBeUsed() {
    final CustomerInlineObjectAnotherObjectDto anotherObjectDto =
        CustomerInlineObjectAnotherObjectDto.builder()
            .setKey("key")
            .andAllOptionals()
            .setData("data")
            .build();
    final CustomerInlineObjectDto customerInlineObjectDto =
        CustomerInlineObjectDto.builder()
            .setKey("key")
            .andAllOptionals()
            .setData("data")
            .setAnotherObject(anotherObjectDto)
            .build();

    final CustomerDto customerDto =
        CustomerDto.builder().setInlineObject(customerInlineObjectDto).andAllOptionals().build();

    assertEquals(customerInlineObjectDto, customerDto.getInlineObject());
    assertEquals(
        Optional.of(anotherObjectDto), customerDto.getInlineObject().getAnotherObjectOpt());
  }
}
