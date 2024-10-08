package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.DtoSetterGenerator.dtoSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class DtoSetterGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allOfPojo")
  void generator_when_allOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(
                sampleObjectPojo1(), JavaPojos.allNecessityAndNullabilityVariants()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojo")
  void generator_when_calledWithComposedPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("Opt1")
            .optionalSuffix("Opt2")
            .optionalNullableSuffix("Tristate")
            .build();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(
                sampleObjectPojo1(), JavaPojos.allNecessityAndNullabilityVariants()),
            defaultTestSettings().withGetterSuffixes(getterSuffixes),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojoWithDiscriminator")
  void generator_when_calledWithOneOfPojoWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());
    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(javaOneOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojoWithEnumDiscriminator")
  void generator_when_calledWithOneOfPojoWithEnumDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo oneOfPojo = JavaPojos.oneOfPojoWithEnumDiscriminator();

    final Writer writer = generator.generate(oneOfPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojo")
  void generator_when_anyOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(
                sampleObjectPojo1(), JavaPojos.allNecessityAndNullabilityVariants()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojoWithDiscriminator")
  void generator_when_anyOfPojoWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());
    final JavaAnyOfComposition javaAnyOfComposition =
        JavaAnyOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(javaAnyOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("noBuilderSetMethodPrefix")
  void generator_when_noBuilderSetMethodPrefix_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings().withBuilderMethodPrefix(""),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("composedPojoHasNoAdditionalPropertiesAllowed")
  void generator_when_composedPojoHasNoAdditionalPropertiesAllowed_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo samplePojo1 =
        sampleObjectPojo1().withAdditionalProperties(JavaAdditionalProperties.notAllowed());

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(samplePojo1, sampleObjectPojo2()),
            defaultTestSettings().withBuilderMethodPrefix(""),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("composedPojoHasNotNullableAdditionalProperties")
  void generator_when_composedPojoHasNotNullableAdditionalProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType().withNullability(NOT_NULLABLE));
    final JavaObjectPojo samplePojo1 =
        sampleObjectPojo1().withAdditionalProperties(additionalProperties);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(samplePojo1, sampleObjectPojo2()),
            defaultTestSettings().withBuilderMethodPrefix(""),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nestedOneOfPojo")
  void generator_when_nestedOneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo oneOfPojo =
        JavaPojos.oneOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), sampleObjectPojo2());

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(sampleObjectPojo1(), oneOfPojo),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
