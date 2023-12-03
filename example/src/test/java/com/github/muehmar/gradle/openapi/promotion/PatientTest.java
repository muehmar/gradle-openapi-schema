package com.github.muehmar.gradle.openapi.promotion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PatientTest {

  @Test
  void build_when_namedPatientDto_then_correctProperties() {
    final NamedPatientDto namedPatientDto =
        NamedPatientDto.namedPatientDtoBuilder()
            .setFirstName(Optional.of("FirstName"))
            .setLastName("LastName")
            .andAllOptionals()
            .build();

    assertEquals(Optional.of("FirstName"), namedPatientDto.getFirstNameOpt());
    assertEquals("LastName", namedPatientDto.getLastName());

    assertEquals(
        Optional.of("FirstName"), namedPatientDto.getNamedPatientPatientDto().getFirstNameOpt());
    assertEquals("LastName", namedPatientDto.getNamedPatientPatientDto().getLastName());
  }

  @ParameterizedTest
  @MethodSource("propertyGettersAndReturnTypes")
  void getReturnType_when_root2Dto_then_isPublicMethodAndMatchReturnType(
      Class<?> dtoClass, String methodName, Class<?> returnType) throws NoSuchMethodException {
    final Method getProp1Method = dtoClass.getMethod(methodName);

    assertEquals(returnType, getProp1Method.getReturnType());
    assertTrue(Modifier.isPublic(getProp1Method.getModifiers()));
  }

  public static Stream<Arguments> propertyGettersAndReturnTypes() {
    return Stream.of(
        arguments(NamedPatientDto.class, "getLastName", String.class),
        arguments(PatientDto.class, "getLastNameOpt", Optional.class));
  }

  @ParameterizedTest
  @MethodSource("nonPresentMethodNames")
  void getMethod_when_nonPresentMethodNames_then_noSuchMethodException(
      Class<?> dtoClass, String methodName) {
    assertThrows(NoSuchMethodException.class, () -> dtoClass.getMethod(methodName));
  }

  public static Stream<Arguments> nonPresentMethodNames() {
    return Stream.of(arguments(PatientDto.class, "getLastName"));
  }
}
