package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.BasicValidationMethodGenerator.basicValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class BasicValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfPojoWithMembers")
  void generate_when_oneOfPojoWithMembers_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.withMembers(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            PList.of(JavaPojoMembers.requiredColorEnum()));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("oneOfPojoWithDiscriminator")
  void generate_when_oneOfPojoWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfComposition.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()),
            Discriminator.fromPropertyName(Name.ofString("type")));

    final JavaObjectPojo pojo = JavaPojos.oneOfPojo(javaOneOfComposition);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("anyOfPojoWithMembers")
  void generate_when_anyOfPojoWithMembers_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.withMembers(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            PList.of(JavaPojoMembers.requiredColorEnum()));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("emptyPojo")
  void generate_when_emptyPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaObjectPojo pojo = JavaPojos.objectPojo();

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithMinPropertyCountConstraint")
  void generate_when_objectPojoWithMinPropertyCountConstraint_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.allNecessityAndNullabilityVariants(
            Constraints.ofPropertiesCount(PropertyCount.ofMinProperties(5)));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithMaxPropertyCountConstraint")
  void generate_when_objectPojoWithMaxPropertyCountConstraint_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.allNecessityAndNullabilityVariants(
            Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(8)));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithNotAnyValueTypeForAdditionalProperties")
  void generate_when_objectPojoWithNotAnyValueTypeForAdditionalProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.withAdditionalProperties(
            sampleObjectPojo1(), JavaAdditionalProperties.allowedFor(JavaTypes.stringType()));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithNotAllowedAdditionalProperties")
  void generate_when_objectPojoWithNotAllowedAdditionalProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = basicValidationMethodGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.withAdditionalProperties(
            sampleObjectPojo1(), JavaAdditionalProperties.notAllowed());

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
