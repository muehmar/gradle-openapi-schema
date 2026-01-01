package com.github.muehmar.gradle.openapi.issues.issue229;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

class DiscriminatorDeserialisationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void fold_when_matchesAdmin_then_adminDtoReturned() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"usertype\":\"admin\",\"adminname\":\"admin-name\"}", AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final AdminDto adminDto =
        AdminDto.builder()
            .setAdminname("admin-name")
            .setUsertype(UserTypeDto.ADMIN)
            .andAllOptionals()
            .build();
    assertEquals(adminDto, obj);
  }

  @Test
  void fold_when_matchesUser_then_userDtoReturned() throws Exception {
    final AdminOrUserDto adminOrUserDto =
        MAPPER.readValue(
            "{\"usertype\":\"user\",\"username\":\"user-name\"}", AdminOrUserDto.class);

    final Object obj = adminOrUserDto.foldOneOf(admin -> admin, user -> user);

    final UserDto userDto =
        UserDto.builder()
            .setUsername("user-name")
            .setUsertype(UserTypeDto.USER)
            .andAllOptionals()
            .build();
    assertEquals(userDto, obj);
  }
}
