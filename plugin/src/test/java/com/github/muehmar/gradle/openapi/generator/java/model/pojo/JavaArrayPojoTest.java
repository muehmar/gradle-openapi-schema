package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import org.junit.jupiter.api.Test;

class JavaArrayPojoTest {
  @Test
  void wrap_when_arrayPojo_then_correctJavaPojoMemberCreated() {
    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            componentName("Posology", "Dto"),
            "Posology",
            NumericType.formatDouble(),
            Constraints.ofSize(Size.ofMax(4)));
    final JavaArrayPojo javaArrayPojo = JavaArrayPojo.wrap(arrayPojo, TypeMappings.empty());

    final JavaPojoMember arrayPojoMember = javaArrayPojo.getArrayPojoMember();

    assertEquals(JavaName.fromString("value"), arrayPojoMember.getName());
    assertEquals(arrayPojo.getDescription(), arrayPojoMember.getDescription());
    assertEquals(arrayPojo.getConstraints(), arrayPojoMember.getJavaType().getConstraints());
    assertEquals(
        "List<Double>", arrayPojoMember.getJavaType().getParameterizedClassName().asString());
    assertEquals(Necessity.REQUIRED, arrayPojoMember.getNecessity());
    assertEquals(Nullability.NOT_NULLABLE, arrayPojoMember.getNullability());
  }
}
