package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
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

  @Test
  void generator_when_enabledJacksonAndDisabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNullableGetter.optionalNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.OPTIONAL, Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember, defaultTestSettings().withEnableValidation(false), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_disabledJacksonAndEnabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNullableGetter.optionalNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(
            Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern")),
            Necessity.OPTIONAL,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember, defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_optionalNullableSuffix_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        OptionalNullableGetter.optionalNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(
            Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern")),
            Necessity.OPTIONAL,
            Nullability.NULLABLE);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("")
            .optionalSuffix("")
            .optionalNullableSuffix("OptNull")
            .build();

    final Writer writer =
        generator.generate(
            pojoMember,
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
        OptionalNullableGetter.optionalNullableGetterGenerator(STANDARD);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.OPTIONAL, Nullability.NULLABLE);

    final ValidationMethods validationMethods =
        TestPojoSettings.defaultValidationMethods()
            .withDeprecatedAnnotation(true)
            .withModifier(JavaModifier.PUBLIC);

    final Writer writer =
        generator.generate(
            pojoMember,
            defaultTestSettings()
                .withJsonSupport(JsonSupport.JACKSON)
                .withValidationMethods(validationMethods),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
