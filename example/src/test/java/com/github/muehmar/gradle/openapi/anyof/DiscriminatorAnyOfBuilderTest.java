package com.github.muehmar.gradle.openapi.anyof;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class DiscriminatorAnyOfBuilderTest {
  @ParameterizedTest
  @MethodSource("builderStages")
  void getDeclaredMethods_when_calledForBuilderStageAfterSettingFirstAnyOfDto_then_notAnyOfStage(
      Object builderStage) {
    final Set<String> stageMethodNames =
        Arrays.stream(builderStage.getClass().getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());

    assertEquals(
        new HashSet<>(Arrays.asList("build", "andAllOptionals", "andOptionals")), stageMethodNames);
  }

  public static Stream<Arguments> builderStages() {
    final AdminDto adminDto =
        AdminDto.builder().setId("id").setAdminname("adminname").setType("type").build();
    final UserDto userDto =
        UserDto.builder().setId("id").setUsername("username").setType("type").build();
    final AdminOrUserDiscriminatorAnyOfContainerDto containerDto =
        AdminOrUserDiscriminatorAnyOfContainerDto.fromUser(userDto);
    return Stream.of(
        arguments(AdminOrUserDiscriminatorDto.builder().setAdminDto(adminDto)),
        arguments(AdminOrUserDiscriminatorDto.builder().setUserDto(userDto)),
        arguments(AdminOrUserDiscriminatorDto.builder().setAnyOfContainer(containerDto)));
  }
}
