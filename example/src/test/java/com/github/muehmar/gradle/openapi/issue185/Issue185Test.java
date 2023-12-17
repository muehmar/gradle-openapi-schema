package com.github.muehmar.gradle.openapi.issue185;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class Issue185Test {

  private static final ObjectMapper MAPPER = MapperFactory.mapper();

  @Test
  void
      userDtoBuilder_when_userBaseDtoHasAdditionalNameProperty_then_additionalNamePropertyDiscarded()
          throws JsonProcessingException {
    final UserBaseDto userBaseDto =
        UserBaseDto.userBaseDtoBuilder()
            .setId(1234)
            .andOptionals()
            .addAdditionalProperty("name", "additional-property-name")
            .build();
    final UserDto userDto =
        UserDto.userDtoBuilder().setUserBaseDto(userBaseDto).setName("name").build();

    assertEquals(Collections.emptyMap(), userDto.getAdditionalProperties());
    assertEquals("{\"id\":1234,\"name\":\"name\"}", MAPPER.writeValueAsString(userDto));
  }

  @Test
  void userDtoBuilder_when_addAdditionalNameProperty_then_additionalNamePropertyDiscarded()
      throws JsonProcessingException {
    final UserBaseDto userBaseDto = UserBaseDto.userBaseDtoBuilder().setId(1234).build();
    final UserDto userDto =
        UserDto.userDtoBuilder()
            .setUserBaseDto(userBaseDto)
            .setName("name")
            .andOptionals()
            .addAdditionalProperty("name", "additional-property-name")
            .build();

    assertEquals(Collections.emptyMap(), userDto.getAdditionalProperties());
    assertEquals("{\"id\":1234,\"name\":\"name\"}", MAPPER.writeValueAsString(userDto));
  }

  @Test
  void userDtoBuilder_when_addAdditionalIdProperty_then_additionalIdPropertyDiscarded()
      throws JsonProcessingException {
    final UserBaseDto userBaseDto = UserBaseDto.userBaseDtoBuilder().setId(1234).build();
    final UserDto userDto =
        UserDto.userDtoBuilder()
            .setUserBaseDto(userBaseDto)
            .setName("name")
            .andOptionals()
            .addAdditionalProperty("id", "additional-property-id")
            .build();

    assertEquals(Collections.emptyMap(), userDto.getAdditionalProperties());
    assertEquals("{\"id\":1234,\"name\":\"name\"}", MAPPER.writeValueAsString(userDto));
  }
}
