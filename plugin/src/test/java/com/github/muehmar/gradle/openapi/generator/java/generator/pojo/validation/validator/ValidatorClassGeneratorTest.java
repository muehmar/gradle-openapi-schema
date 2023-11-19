package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.validator.ValidatorClassGenerator.validationClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.illegalIdentifierPojo;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ValidatorClassGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("sampleObjectPojo2")
  void generate_when_sampleObjectPojo2_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final Writer writer =
        generator.generate(sampleObjectPojo2(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }

  @Test
  @SnapshotName("allOfPojoWithMembers")
  void generate_when_allOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.allOfPojo(sampleObjectPojo1(), sampleObjectPojo2())
            .withMembers(
                JavaPojoMembers.leastRestrictive(
                    PList.of(TestJavaPojoMembers.requiredColorEnum())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("oneOfPojoWithMembers")
  void generate_when_oneOfPojoWithMembers_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2())
            .withMembers(
                JavaPojoMembers.leastRestrictive(
                    PList.of(TestJavaPojoMembers.requiredColorEnum())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("oneOfPojoWithDiscriminator")
  void generate_when_oneOfPojoWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()),
            Discriminator.fromPropertyName(Name.ofString("type")));

    final JavaObjectPojo pojo = JavaPojos.oneOfPojo(javaOneOfComposition);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("anyOfPojoWithMembers")
  void generate_when_anyOfPojoWithMembers_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2())
            .withMembers(
                JavaPojoMembers.leastRestrictive(
                    PList.of(TestJavaPojoMembers.requiredColorEnum())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("emptyPojo")
  void generate_when_emptyPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo = JavaPojos.objectPojo();

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithMinPropertyCountConstraint")
  void generate_when_objectPojoWithMinPropertyCountConstraint_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.allNecessityAndNullabilityVariants(
            Constraints.ofPropertiesCount(PropertyCount.ofMinProperties(5)));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithMaxPropertyCountConstraint")
  void generate_when_objectPojoWithMaxPropertyCountConstraint_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo =
        JavaPojos.allNecessityAndNullabilityVariants(
            Constraints.ofPropertiesCount(PropertyCount.ofMaxProperties(8)));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithNotAnyValueTypeForAdditionalProperties")
  void generate_when_objectPojoWithNotAnyValueTypeForAdditionalProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withAdditionalProperties(JavaAdditionalProperties.allowedFor(JavaTypes.stringType()));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithNotAllowedAdditionalProperties")
  void generate_when_objectPojoWithNotAllowedAdditionalProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final JavaObjectPojo pojo =
        sampleObjectPojo1().withAdditionalProperties(JavaAdditionalProperties.notAllowed());

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generate_when_illegalIdentifierPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final Writer writer =
        generator.generate(illegalIdentifierPojo(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
