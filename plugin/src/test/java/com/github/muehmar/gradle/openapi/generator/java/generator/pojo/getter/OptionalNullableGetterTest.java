package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalNullableGetter.optionalNullableGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationMethods;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class OptionalNullableGetterTest {
  private Expect expect;

  private static final JavaPojoMember POJO_MEMBER =
      TestJavaPojoMembers.requiredString().withNecessity(OPTIONAL).withNullability(NULLABLE);

  @Test
  void generator_when_enabledJacksonAndDisabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNullableGetterGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            POJO_MEMBER, defaultTestSettings().withEnableValidation(false), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_disabledJacksonAndEnabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNullableGetterGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            POJO_MEMBER, defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_optionalNullableSuffix_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNullableGetterGenerator(STANDARD);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("")
            .optionalSuffix("")
            .optionalNullableSuffix("OptNull")
            .build();

    final Writer writer =
        generator.generate(
            POJO_MEMBER,
            defaultTestSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableValidation(false)
                .withGetterSuffixes(getterSuffixes),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_deprecatedAnnotation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNullableGetterGenerator(STANDARD);

    final ValidationMethods validationMethods =
        TestPojoSettings.defaultValidationMethods()
            .withDeprecatedAnnotation(true)
            .withModifier(JavaModifier.PUBLIC);

    final Writer writer =
        generator.generate(
            POJO_MEMBER,
            defaultTestSettings()
                .withJsonSupport(JsonSupport.JACKSON)
                .withValidationMethods(validationMethods),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("valueTypeOfArrayHasConstraints")
  void generator_when_valueTypeOfArrayHasConstraints_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNullableGetterGenerator(STANDARD);

    final IntegerType itemType =
        IntegerType.formatInteger()
            .withConstraints(Constraints.ofMinAndMax(new Min(5), new Max(10)));

    final JavaPojoMember member =
        TestJavaPojoMembers.list(itemType, OPTIONAL, NULLABLE, Constraints.ofSize(Size.ofMin(5)));

    final Writer writer =
        generator.generate(member, defaultTestSettings().withEnableValidation(true), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
