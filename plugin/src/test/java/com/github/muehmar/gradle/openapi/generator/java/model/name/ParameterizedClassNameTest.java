package com.github.muehmar.gradle.openapi.generator.java.model.name;

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
}
