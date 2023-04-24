package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaMemberName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
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
            PojoName.ofNameAndSuffix(Name.ofString("Posology"), "Dto"),
            "Posology",
            NumericType.formatDouble(),
            Constraints.ofSize(Size.ofMax(4)));
    final JavaArrayPojo javaArrayPojo = JavaArrayPojo.wrap(arrayPojo, TypeMappings.empty());

    final JavaPojoMember arrayPojoMember = javaArrayPojo.getArrayPojoMember();

    assertEquals(JavaMemberName.wrap(Name.ofString("value")), arrayPojoMember.getName());
    assertEquals(arrayPojo.getDescription(), arrayPojoMember.getDescription());
    assertEquals(arrayPojo.getConstraints(), arrayPojoMember.getJavaType().getConstraints());
    assertEquals(Name.ofString("List<Double>"), arrayPojoMember.getJavaType().getFullClassName());
    assertEquals(Necessity.REQUIRED, arrayPojoMember.getNecessity());
    assertEquals(Nullability.NOT_NULLABLE, arrayPojoMember.getNullability());
  }
}
