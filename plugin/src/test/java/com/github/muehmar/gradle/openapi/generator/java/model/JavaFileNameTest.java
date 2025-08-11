package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import org.junit.jupiter.api.Test;

class JavaFileNameTest {
  @Test
  void fromRef_when_calledForOpenApiUtilsRef_then_correctPath() {
    final JavaFileName javaFileName = JavaFileName.fromRef(OpenApiUtilRefs.TRISTATE);

    assertEquals("com/github/muehmar/openapi/util/Tristate.java", javaFileName.asPath().toString());
  }

  @Test
  void fromSettingsAndPojo_when_calledForArrayPojo_then_correctPath() {
    final JavaFileName javaFileName =
        JavaFileName.fromSettingsAndPojo(defaultTestSettings(), JavaPojos.arrayPojo());

    assertEquals("com/github/muehmar/PosologyDto.java", javaFileName.asPath().toString());
  }
}
