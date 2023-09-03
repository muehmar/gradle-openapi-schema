package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.DtoSetterGenerator.dtoSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.withAdditionalProperties;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class DtoSetterGeneratorTest {
  private Expect expect;

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

    final Discriminator discriminator =
        Discriminator.fromPropertyName(JavaPojoMembers.requiredString().getName().asName());
    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfComposition.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(javaOneOfComposition), defaultTestSettings(), javaWriter());

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
        withAdditionalProperties(sampleObjectPojo1(), JavaAdditionalProperties.notAllowed());

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(samplePojo1, sampleObjectPojo2()),
            defaultTestSettings().withBuilderMethodPrefix(""),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
