package com.github.muehmar.gradle.openapi.issues.issue185;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Collections;
import org.junit.jupiter.api.Test;

public class Issue185Test {

  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void
      userDtoBuilder_when_userBaseDtoHasAdditionalNameProperty_then_additionalNamePropertyDiscarded()
          throws Exception {
    final UserBaseDto userBaseDto =
        UserBaseDto.userBaseDtoBuilder()
            .setId(1234)
            .andOptionals()
            .addAdditionalProperty("name", "additional-property-name")
            .build();
    final UserDto userDto =
        UserDto.userDtoBuilder().setUserBaseDto(userBaseDto).setName("name").build();

    assertEquals(Collections.emptyList(), userDto.getAdditionalProperties());
    assertEquals("{\"id\":1234,\"name\":\"name\"}", MAPPER.writeValueAsString(userDto));
  }

  @Test
  void userDtoBuilder_when_addAdditionalNameProperty_then_additionalNamePropertyDiscarded()
      throws Exception {
    final UserBaseDto userBaseDto = UserBaseDto.userBaseDtoBuilder().setId(1234).build();
    final UserDto userDto =
        UserDto.userDtoBuilder()
            .setUserBaseDto(userBaseDto)
            .setName("name")
            .andOptionals()
            .addAdditionalProperty("name", "additional-property-name")
            .build();

    assertEquals(Collections.emptyList(), userDto.getAdditionalProperties());
    assertEquals("{\"id\":1234,\"name\":\"name\"}", MAPPER.writeValueAsString(userDto));
  }

  @Test
  void userDtoBuilder_when_addAdditionalIdProperty_then_additionalIdPropertyDiscarded()
      throws Exception {
    final UserBaseDto userBaseDto = UserBaseDto.userBaseDtoBuilder().setId(1234).build();
    final UserDto userDto =
        UserDto.userDtoBuilder()
            .setUserBaseDto(userBaseDto)
            .setName("name")
            .andOptionals()
            .addAdditionalProperty("id", "additional-property-id")
            .build();

    assertEquals(Collections.emptyList(), userDto.getAdditionalProperties());
    assertEquals("{\"id\":1234,\"name\":\"name\"}", MAPPER.writeValueAsString(userDto));
  }
}
