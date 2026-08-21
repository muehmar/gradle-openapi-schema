package com.github.muehmar.gradle.openapi.issues.issue229;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class DiscriminatorSerialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @ParameterizedTest
  @EnumSource(UserTypeDto.class)
  void writeValueAsString_when_adminDto_then_correctJson(UserTypeDto userTypeDto) throws Exception {
    final AdminDto adminDto =
        AdminDto.builder()
            .setAdminname("admin-name")
            .setUsertype(userTypeDto)
            .andAllOptionals()
            .build();
    final AdminOrUserDto dto = AdminOrUserDto.builder().setAdminDto(adminDto).build();

    assertEquals(
        "{\"adminname\":\"admin-name\",\"usertype\":\"admin\"}", MAPPER.writeValueAsString(dto));
  }

  @ParameterizedTest
  @EnumSource(UserTypeDto.class)
  void writeValueAsString_when_userDto_then_correctJson(UserTypeDto userTypeDto) throws Exception {
    final UserDto userDto =
        UserDto.builder()
            .setUsername("user-name")
            .setUsertype(userTypeDto)
            .andAllOptionals()
            .build();
    final AdminOrUserDto dto = AdminOrUserDto.builder().setUserDto(userDto).build();

    assertEquals(
        "{\"username\":\"user-name\",\"usertype\":\"user\"}", MAPPER.writeValueAsString(dto));
  }
}
