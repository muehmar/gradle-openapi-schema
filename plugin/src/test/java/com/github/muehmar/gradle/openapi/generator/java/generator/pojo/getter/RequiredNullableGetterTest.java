package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.Jakarta2ValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
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
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class RequiredNullableGetterTest {

  private Expect expect;

  @Test
  void generator_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(5, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));
    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.ASSERT_TRUE::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generator_when_disabledJackson_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(
            Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern")),
            Necessity.REQUIRED,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertEquals(4, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.ASSERT_TRUE::equals));
    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.PATTERN::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generator_when_disabledJacksonAndValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableValidation(false),
            Writer.createDefault());

    assertEquals(2, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generator_when_requiredNullableSuffix_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NULLABLE);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("ReqNull")
            .optionalSuffix("")
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

    assertEquals(2, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generator_when_deprecatedAnnotation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NULLABLE);

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

    expect.toMatchSnapshot(writer.asString());
  }
}
