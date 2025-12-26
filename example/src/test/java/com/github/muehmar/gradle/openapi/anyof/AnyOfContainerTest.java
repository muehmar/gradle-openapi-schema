package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AnyOfContainerTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void writeValueAsString_when_adminDto_then_correctJson() throws Exception {
    final AdminDto adminDto =
        AdminDto.builder()
            .setId("admin-id")
            .setAdminname("admin-name")
            .setType("admin")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .build();

    final AdminOrUserAnyOfContainerDto container = AdminOrUserAnyOfContainerDto.fromAdmin(adminDto);

    final AdminOrUserDto dto = AdminOrUserDto.builder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"adminname\":\"admin-name\",\"id\":\"admin-id\",\"level\":5,\"type\":\"admin\"}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_userDto_then_correctJson() throws Exception {
    final UserDto userDto =
        UserDto.builder()
            .setId("user-id")
            .setUsername("user-name")
            .setType("user")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();

    final AdminOrUserAnyOfContainerDto container = AdminOrUserAnyOfContainerDto.fromUser(userDto);

    final AdminOrUserDto dto = AdminOrUserDto.builder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"age\":25,\"email\":null,\"id\":\"user-id\",\"type\":\"user\",\"username\":\"user-name\"}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void writeValueAsString_when_adminAndUserOfInlinedDto_then_correctJson() throws Exception {
    final UserDto userDto =
        UserDto.builder()
            .setId("id")
            .setUsername("user-name")
            .setType("user")
            .andAllOptionals()
            .setAge(25)
            .setEmail(Tristate.ofNull())
            .build();
    final AdminDto adminDto =
        AdminDto.builder()
            .setId("id")
            .setAdminname("admin-name")
            .setType("admin")
            .andAllOptionals()
            .setLevel(5L)
            .setColor(Optional.empty())
            .build();

    final AdminOrUserAnyOfContainerDto c1 = AdminOrUserAnyOfContainerDto.fromUser(userDto);
    final AdminOrUserAnyOfContainerDto c2 = AdminOrUserAnyOfContainerDto.fromAdmin(adminDto);

    final AdminOrUserAnyOfContainerDto container = c1.merge(c2);

    final AdminOrUserDto dto = AdminOrUserDto.builder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"adminname\":\"admin-name\",\"age\":25,\"email\":null,\"id\":\"id\",\"level\":5,\"type\":\"user\",\"username\":\"user-name\"}",
        MAPPER.writeValueAsString(dto));
  }
}
