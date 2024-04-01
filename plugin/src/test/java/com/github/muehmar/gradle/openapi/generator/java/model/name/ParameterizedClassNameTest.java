package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import org.junit.jupiter.api.Test;

class ParameterizedClassNameTest {

  @Test
  void asString_when_nonGenericClassName_then_correctString() {
    final ParameterizedClassName parameterizedClassName =
        ParameterizedClassName.fromNonGenericClass(QualifiedClassNames.STRING);

    final String classNameString = parameterizedClassName.asString();

    assertEquals("String", classNameString);
  }

  @Test
  void
      asStringWithValueTypeAnnotations_when_nonGenericClassName_then_functionNotUsedAndCorrectString() {
    final ParameterizedClassName parameterizedClassName =
        ParameterizedClassName.fromNonGenericClass(QualifiedClassNames.STRING);

    final String classNameString =
        parameterizedClassName.asStringWithValueTypeAnnotations(ignore -> "@Test");

    assertEquals("String", classNameString);
  }

  @Test
  void asString_when_genericClassName_then_correctString() {
    final ParameterizedClassName parameterizedClassName =
        ParameterizedClassName.fromGenericClass(
            QualifiedClassNames.MAP, PList.of(JavaTypes.stringType(), JavaTypes.integerType()));

    final String classNameString = parameterizedClassName.asString();

    assertEquals("Map<String, Integer>", classNameString);
  }

  @Test
  void
      asStringWithValueTypeAnnotations_when_genericClassNameAndCreateValueTypeAnnotations_then_correctAnnotatedParameterType() {
    final ParameterizedClassName parameterizedClassName =
        ParameterizedClassName.fromGenericClass(
            QualifiedClassNames.MAP, PList.of(JavaTypes.stringType(), JavaTypes.integerType()));

    final String classNameString =
        parameterizedClassName.asStringWithValueTypeAnnotations(ignore -> "@Test");

    assertEquals("Map<String, @Test Integer>", classNameString);
  }

  @Test
  void asStringWrappingNullableValueType_when_notNullableValueType_then_notWrapped() {
    final ParameterizedClassName parameterizedClassName =
        ParameterizedClassName.fromGenericClass(
            QualifiedClassNames.LIST, PList.of(JavaTypes.stringType()));

    final String classNameString = parameterizedClassName.asStringWrappingNullableValueType();

    assertEquals("List<String>", classNameString);
  }

  @Test
  void asStringWrappingNullableValueType_when_nullableValueType_then_wrappedIntoOptional() {
    final ParameterizedClassName parameterizedClassName =
        ParameterizedClassName.fromGenericClass(
            QualifiedClassNames.LIST, PList.of(JavaTypes.stringType().withNullability(NULLABLE)));

    final String classNameString = parameterizedClassName.asStringWrappingNullableValueType();

    assertEquals("List<Optional<String>>", classNameString);
  }
}
