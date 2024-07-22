package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType.STANDARD_NO_VALIDATION;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class RequiredNotNullableGetterTest {
  private Expect expect;

  private static final JavaPojoMember POJO_MEMBER =
      TestJavaPojoMembers.string(REQUIRED, NOT_NULLABLE);

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
        requiredNotNullableGetterGenerator(STANDARD_NO_VALIDATION);

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

  @ParameterizedTest
  @MethodSource(
      "com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers#allNecessityAndNullabilityVariantsTestSource")
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_matchSnapshot(JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        requiredNotNullableGetterGenerator(STANDARD);

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect
        .scenario(member.getNullability().name().concat("_").concat(member.getNecessity().name()))
        .toMatchSnapshot(writerSnapshot(writer));
  }
}
