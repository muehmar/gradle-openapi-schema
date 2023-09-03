package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
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
        PojoPropertyCountMethod.pojoPropertyCountMethoGenerator();

    final Writer writer =
        gen.generate(
            allNecessityAndNullabilityVariants(constraints), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("pojoPropertyCountMethodWithValidationDisabled")
  void generate_when_validationDisabled_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        PojoPropertyCountMethod.pojoPropertyCountMethoGenerator();

    final Constraints constraints =
        Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(1, 2));

    final Writer writer =
        gen.generate(
            allNecessityAndNullabilityVariants(constraints),
            defaultTestSettings().withEnableValidation(false),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("pojoPropertyCountMethodWithNoJsonSupport")
  void generate_when_jsonSupportDisabled_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        PojoPropertyCountMethod.pojoPropertyCountMethoGenerator();

    final Constraints constraints =
        Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(1, 2));

    final Writer writer =
        gen.generate(
            allNecessityAndNullabilityVariants(constraints),
            defaultTestSettings().withJsonSupport(JsonSupport.NONE),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojo")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        PojoPropertyCountMethod.pojoPropertyCountMethoGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.oneOfPojo(allNecessityAndNullabilityVariants(), sampleObjectPojo1()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
