package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalNotNullableGetter.optionalNotNullableGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
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
class OptionalNotNullableGetterTest {
  private Expect expect;

  private static final JavaPojoMember POJO_MEMBER =
      JavaPojoMembers.requiredString().withNecessity(OPTIONAL).withNullability(NOT_NULLABLE);

  @Test
  void generator_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNotNullableGetterGenerator(STANDARD);

    final Writer writer = generator.generate(POJO_MEMBER, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_disabledJacksonAndEnabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNotNullableGetterGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            POJO_MEMBER, defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_disabledJacksonAndValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNotNullableGetterGenerator(STANDARD);

    final Writer writer =
        generator.generate(
            POJO_MEMBER,
            defaultTestSettings().withJsonSupport(JsonSupport.NONE).withEnableValidation(false),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generator_when_suffixForOptionalNotNullable_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalNotNullableGetterGenerator(STANDARD);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("")
            .optionalSuffix("Opt")
            .optionalNullableSuffix("")
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
        optionalNotNullableGetterGenerator(STANDARD);

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
}
