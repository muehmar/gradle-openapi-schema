package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.parameter.JavaParameter;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import java.util.Optional;
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

  @Test
  void fromSettingsAndParameter_when_calledForParameter_then_correctPath() {
    final Parameter param =
        new Parameter(Name.ofString("limitParam"), IntegerType.formatInteger(), Optional.of(15));

    final JavaFileName javaFileName =
        JavaFileName.fromSettingsAndParameter(defaultTestSettings(), JavaParameter.wrap(param));

    assertEquals("com/github/muehmar/parameter/LimitParam.java", javaFileName.asPath().toString());
  }
}
