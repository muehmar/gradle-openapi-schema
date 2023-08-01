package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationMethods;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class OptionalNotNullableGetterTest {

  private Expect expect;

  @Test
  void generator_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNotNullableGetter.optionalNotNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_disabledJacksonAndEnabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNotNullableGetter.optionalNotNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(
            Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern")),
            OPTIONAL,
            NOT_NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_disabledJacksonAndValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNotNullableGetter.optionalNotNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableValidation(false),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_suffixForOptionalNotNullable_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNotNullableGetter.optionalNotNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("")
            .optionalSuffix("Opt")
            .optionalNullableSuffix("")
            .build();

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableValidation(false)
                .withGetterSuffixes(getterSuffixes),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_deprecatedAnnotation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNotNullableGetter.optionalNotNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final ValidationMethods validationMethods =
        TestPojoSettings.defaultValidationMethods()
            .withDeprecatedAnnotation(true)
            .withModifier(JavaModifier.PUBLIC);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.JACKSON)
                .withValidationMethods(validationMethods),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
