package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class PojoPropertyCountMethodTest {

  private Expect expect;

  @Test
  @SnapshotName("pojoPropertyCountMethodDefaultSettings")
  void generate_when_defaultSettings_then_correctOutput() {
    final Constraints constraints =
        Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(1, 2));
    final Generator<JavaObjectPojo, PojoSettings> gen =
        PojoPropertyCountMethod.propertyCountMethod();

    final Writer writer =
        gen.generate(
            (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(constraints),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaValidationRefs.MIN::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.MAX::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
  }

  @Test
  @SnapshotName("pojoPropertyCountMethodWithValidationDisabled")
  void generate_when_validationDisabled_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        PojoPropertyCountMethod.propertyCountMethod();

    final Constraints constraints =
        Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(1, 2));

    final Writer writer =
        gen.generate(
            (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(constraints),
            TestPojoSettings.defaultSettings().withEnableValidation(false),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertFalse(writer.getRefs().exists(JavaValidationRefs.MIN::equals));
    assertFalse(writer.getRefs().exists(JavaValidationRefs.MAX::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
  }

  @Test
  @SnapshotName("pojoPropertyCountMethodWithNoJsonSupport")
  void generate_when_jsonSupportDisabled_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        PojoPropertyCountMethod.propertyCountMethod();

    final Constraints constraints =
        Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(1, 2));

    final Writer writer =
        gen.generate(
            (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(constraints),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(JavaValidationRefs.MIN::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.MAX::equals));
    assertFalse(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
  }
}
