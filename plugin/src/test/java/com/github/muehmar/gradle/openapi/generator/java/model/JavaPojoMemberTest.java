package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaPojoMemberTest {

  @ParameterizedTest
  @MethodSource("primitiveMapping")
  void wrap_when_differentNecessityAndNullability_then_primitiveTypeUsedForRequiredAndNotNullable(
      Necessity necessity, Nullability nullability, String className) {
    final NewPojoMember pojoMember =
        new NewPojoMember(
            Name.ofString("flag"), "desc", BooleanType.create(), necessity, nullability);

    final JavaPojoMember javaPojoMember = JavaPojoMember.wrap(pojoMember, TypeMappings.empty());

    assertEquals(className, javaPojoMember.getJavaType().getClassName().asString());
  }

  static Stream<Arguments> primitiveMapping() {
    return Stream.of(
        Arguments.of(Necessity.REQUIRED, Nullability.NOT_NULLABLE, "boolean"),
        Arguments.of(Necessity.REQUIRED, Nullability.NULLABLE, "Boolean"),
        Arguments.of(Necessity.OPTIONAL, Nullability.NOT_NULLABLE, "Boolean"),
        Arguments.of(Necessity.OPTIONAL, Nullability.NULLABLE, "Boolean"));
  }
}
