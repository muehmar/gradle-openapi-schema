package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.RequiredNotNullableGetter.requiredNotNullableGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class RequiredNotNullableGetterTest {
  private Expect expect;

  private static final JavaPojoMember POJO_MEMBER =
      TestJavaPojoMembers.requiredString().withNecessity(REQUIRED).withNullability(NOT_NULLABLE);

  @Test
  @SnapshotName("requiredAndNotNullableField")
  void generator_when_requiredAndNotNullableField_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        requiredNotNullableGetterGenerator(STANDARD);

    final Writer writer = generator.generate(POJO_MEMBER, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAndNotNullableFieldNoValidationOption")
  void generator_when_requiredAndNotNullableFieldNoValidationOption_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        requiredNotNullableGetterGenerator(NO_VALIDATION);

    final Writer writer = generator.generate(POJO_MEMBER, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("validationDisabled")
  void generator_when_validationDisabled_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        requiredNotNullableGetterGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            POJO_MEMBER, defaultTestSettings().withEnableValidation(false), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredSuffix")
  void generator_when_requiredSuffix_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        requiredNotNullableGetterGenerator(STANDARD);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("Req")
            .requiredNullableSuffix("")
            .optionalSuffix("")
            .optionalNullableSuffix("")
            .build();

    final Writer writer =
        generator.generate(
            POJO_MEMBER,
            defaultTestSettings().withEnableValidation(false).withGetterSuffixes(getterSuffixes),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("valueTypeOfArrayHasConstraints")
  void generator_when_valueTypeOfArrayHasConstraints_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        requiredNotNullableGetterGenerator(STANDARD);

    final IntegerType itemType =
        IntegerType.formatInteger()
            .withConstraints(Constraints.ofMinAndMax(new Min(5), new Max(10)));

    final JavaPojoMember member =
        TestJavaPojoMembers.list(
            itemType, REQUIRED, Nullability.NOT_NULLABLE, Constraints.ofSize(Size.ofMin(5)));

    final Writer writer =
        generator.generate(member, defaultTestSettings().withEnableValidation(true), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
