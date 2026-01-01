package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class DiscriminatorAnyOfContainerTest {
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

    final AdminOrUserDiscriminatorAnyOfContainerDto container =
        AdminOrUserDiscriminatorAnyOfContainerDto.fromAdmin(adminDto);

    final AdminOrUserDiscriminatorDto dto =
        AdminOrUserDiscriminatorDto.builder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"adminname\":\"admin-name\",\"id\":\"admin-id\",\"level\":5,\"type\":\"Admin\"}",
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

    final AdminOrUserDiscriminatorAnyOfContainerDto container =
        AdminOrUserDiscriminatorAnyOfContainerDto.fromUser(userDto);

    final AdminOrUserDiscriminatorDto dto =
        AdminOrUserDiscriminatorDto.builder().setAnyOfContainer(container).build();

    assertEquals(
        "{\"age\":25,\"email\":null,\"id\":\"user-id\",\"type\":\"User\",\"username\":\"user-name\"}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void getDeclaredMethods_when_calledForContainer_then_containsNotWithAndMergeMethods() {
    final Set<String> publicMethodNames =
        Arrays.stream(AdminOrUserDiscriminatorAnyOfContainerDto.class.getDeclaredMethods())
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .map(Method::getName)
            .collect(Collectors.toSet());

    assertEquals(
        new HashSet<>(Arrays.asList("fromAdmin", "fromUser", "hashCode", "equals", "toString")),
        publicMethodNames);
  }
}
