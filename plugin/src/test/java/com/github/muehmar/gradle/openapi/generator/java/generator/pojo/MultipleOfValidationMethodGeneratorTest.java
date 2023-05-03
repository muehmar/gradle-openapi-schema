package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaNumericType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class MultipleOfValidationMethodGeneratorTest {
  private Expect expect;

  private static final JavaPojoMember INTEGER_MEMBER =
      JavaPojoMember.of(
          Name.ofString("intVal"),
          "Description",
          JavaIntegerType.wrap(
              IntegerType.formatInteger()
                  .withConstraints(Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("50")))),
              TypeMappings.empty()),
          Necessity.REQUIRED,
          Nullability.NOT_NULLABLE);

  private static final JavaPojoMember LONG_MEMBER =
      JavaPojoMember.of(
          Name.ofString("longVal"),
          "Description",
          JavaIntegerType.wrap(
              IntegerType.formatLong()
                  .withConstraints(Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("12")))),
              TypeMappings.empty()),
          Necessity.REQUIRED,
          Nullability.NOT_NULLABLE);

  private static final JavaPojoMember FLOAT_MEMBER =
      JavaPojoMember.of(
          Name.ofString("floatVal"),
          "Description",
          JavaNumericType.wrap(
              NumericType.formatFloat()
                  .withConstraints(
                      Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("50.5")))),
              TypeMappings.empty()),
          Necessity.REQUIRED,
          Nullability.NOT_NULLABLE);

  private static final JavaPojoMember DOUBLE_MEMBER =
      JavaPojoMember.of(
          Name.ofString("doubleVal"),
          "Description",
          JavaNumericType.wrap(
              NumericType.formatDouble()
                  .withConstraints(
                      Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("12.25")))),
              TypeMappings.empty()),
          Necessity.REQUIRED,
          Nullability.NOT_NULLABLE);

  private static final JavaObjectPojo POJO =
      JavaPojos.objectPojo(PList.of(INTEGER_MEMBER, LONG_MEMBER, FLOAT_MEMBER, DOUBLE_MEMBER));

  @Test
  void generate_when_integerAndDoubleWithoutMultipleOfConstraint_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.of(JavaPojoMembers.requiredDouble(), JavaPojoMembers.requiredInteger())),
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_validationDisabled_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            POJO,
            TestPojoSettings.defaultSettings().withEnableValidation(false),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("integerMember")
  void generate_when_integer_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(INTEGER_MEMBER)),
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("longMember")
  void generate_when_long_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(LONG_MEMBER)),
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("floatMember")
  void generate_when_float_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(FLOAT_MEMBER)),
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_MATH_BIG_DECIMAL::equals));
    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("doubleMember")
  void generate_when_double_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(DOUBLE_MEMBER)),
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_MATH_BIG_DECIMAL::equals));
    expect.toMatchSnapshot(writer.asString());
  }
}
