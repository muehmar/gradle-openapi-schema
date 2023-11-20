package com.github.muehmar.gradle.openapi.promotion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class Root1Test {

  @ParameterizedTest
  @MethodSource("propertyGettersAndReturnTypes")
  void getReturnType_when_propertyGetters_then_isPublicMethodAndMatchReturnType(
      Class<?> dtoClass, String methodName, Class<?> returnType) throws NoSuchMethodException {
    final Method getProp1Method = dtoClass.getMethod(methodName);

    assertEquals(returnType, getProp1Method.getReturnType());
    assertTrue(Modifier.isPublic(getProp1Method.getModifiers()));
  }

  public static Stream<Arguments> propertyGettersAndReturnTypes() {
    return Stream.of(
        arguments(Root1Dto.class, "getProp2", String.class),
        arguments(Root1Dto.class, "getProp3Opt", Optional.class),
        arguments(Root1Prop2RequiredDto.class, "getProp2", String.class),
        arguments(Root1Prop3RequiredDto.class, "getProp3", String.class),
        arguments(Prop2RequiredDto.class, "getProp2", Object.class),
        arguments(Prop3RequiredDto.class, "getProp3", Object.class));
  }

  @ParameterizedTest
  @MethodSource("nonPresentMethodNames")
  void getMethod_when_nonPresentMethodNames_then_noSuchMethodException(
      Class<?> dtoClass, String methodName) {
    assertThrows(NoSuchMethodException.class, () -> dtoClass.getMethod(methodName));
  }

  public static Stream<Arguments> nonPresentMethodNames() {
    return Stream.of(arguments(Root1Dto.class, "getProp3"));
  }
}
