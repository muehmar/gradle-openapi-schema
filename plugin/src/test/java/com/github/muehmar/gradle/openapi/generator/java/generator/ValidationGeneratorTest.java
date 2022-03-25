package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.generator.data.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ValidationGeneratorTest {
  @Test
  void validationAnnotations_when_validationDisabled_then_noOutput() {
    final PojoMember member = PojoMembers.requiredBirthdate();
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer =
        generator.generate(
            member, defaultSettings().withEnableConstraints(false), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForRequiredField_then_notNullWithRef() {
    final PojoMember member = PojoMembers.requiredBirthdate();
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.NOT_NULL), writer.getRefs());
    assertEquals("@NotNull", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalReferenceField_then_validWithRef() {
    final PojoMember member = PojoMembers.requiredReference().withNecessity(OPTIONAL);
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalEmailField_then_emailWithRef() {
    final PojoMember member = PojoMembers.requiredEmail().withNecessity(OPTIONAL);
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.EMAIL), writer.getRefs());
    assertEquals("@Email", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalIntegerField_then_minAndMaxWithRefs() {
    final PojoMember member = PojoMembers.requiredInteger().withNecessity(OPTIONAL);
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaValidationRefs.MIN::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.MAX::equals));
    assertEquals("@Min(value = 10)\n" + "@Max(value = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalDoubleField_then_minAndMaxWithRefs() {
    final PojoMember member = PojoMembers.requiredDouble().withNecessity(OPTIONAL);
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaValidationRefs.DECIMAL_MIN::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.DECIMAL_MAX::equals));
    assertEquals(
        "@DecimalMin(value = \"12.5\", inclusive = true)\n"
            + "@DecimalMax(value = \"50.1\", inclusive = false)",
        writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalStringListField_then_minAndMaxWithRefs() {
    final PojoMember member = PojoMembers.requiredStringList().withNecessity(OPTIONAL);
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.SIZE), writer.getRefs());
    assertEquals("@Size(min = 1, max = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalStringField_then_minAndMaxWithRefs() {
    final PojoMember member = PojoMembers.requiredString().withNecessity(OPTIONAL);
    final Generator<PojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.PATTERN), writer.getRefs());
    assertEquals("@Pattern(regexp=\"Hello\")", writer.asString());
  }
}
