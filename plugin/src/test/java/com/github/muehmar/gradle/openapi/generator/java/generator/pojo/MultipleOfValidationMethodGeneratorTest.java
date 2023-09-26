package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaMemberName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMemberBuilder;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaNumericType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

@SnapshotTest
class MultipleOfValidationMethodGeneratorTest {
  private Expect expect;

  private static final JavaPojoMember INTEGER_MEMBER =
      JavaPojoMemberBuilder.create()
          .name(JavaMemberName.wrap(Name.ofString("intVal")))
          .description("Description")
          .javaType(
              JavaIntegerType.wrap(
                  IntegerType.formatInteger()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("50")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .nullability(Nullability.NOT_NULLABLE)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .build();

  private static final JavaPojoMember LONG_MEMBER =
      JavaPojoMemberBuilder.create()
          .name(JavaMemberName.wrap(Name.ofString("longVal")))
          .description("Description")
          .javaType(
              JavaIntegerType.wrap(
                  IntegerType.formatLong()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("12")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .nullability(Nullability.NOT_NULLABLE)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .build();

  private static final JavaPojoMember FLOAT_MEMBER =
      JavaPojoMemberBuilder.create()
          .name(JavaMemberName.wrap(Name.ofString("floatVal")))
          .description("Description")
          .javaType(
              JavaNumericType.wrap(
                  NumericType.formatFloat()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("50.5")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .nullability(Nullability.NOT_NULLABLE)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .build();

  private static final JavaPojoMember DOUBLE_MEMBER =
      JavaPojoMemberBuilder.create()
          .name(JavaMemberName.wrap(Name.ofString("doubleVal")))
          .description("Description")
          .javaType(
              JavaNumericType.wrap(
                  NumericType.formatDouble()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("12.25")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .nullability(Nullability.NOT_NULLABLE)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .build();

  private static final JavaObjectPojo POJO =
      JavaPojos.objectPojo(PList.of(INTEGER_MEMBER, LONG_MEMBER, FLOAT_MEMBER, DOUBLE_MEMBER));

  @Test
  void generate_when_integerAndDoubleWithoutMultipleOfConstraint_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.of(JavaPojoMembers.requiredDouble(), JavaPojoMembers.requiredInteger())),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_validationDisabled_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(POJO, defaultTestSettings().withEnableValidation(false), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("integerMember")
  void generate_when_integer_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(INTEGER_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("longMember")
  void generate_when_long_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(LONG_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("floatMember")
  void generate_when_float_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(FLOAT_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_MATH_BIG_DECIMAL::equals));
    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("doubleMember")
  void generate_when_double_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(DOUBLE_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_MATH_BIG_DECIMAL::equals));
    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
