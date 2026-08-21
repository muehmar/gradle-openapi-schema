package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberBuilder.javaPojoMemberBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberXml;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaNumericType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import com.github.muehmar.gradle.openapi.warnings.WarningsContext;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

@SnapshotTest
class MultipleOfValidationMethodGeneratorTest {
  private Expect expect;

  private static final JavaPojoMember INTEGER_MEMBER =
      javaPojoMemberBuilder()
          .pojoName(invoiceName())
          .name(JavaName.fromString("intVal"))
          .description("Description")
          .javaType(
              JavaIntegerType.wrap(
                  IntegerType.formatInteger()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("50")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .memberXml(JavaPojoMemberXml.noDefinition())
          .build();

  private static final JavaPojoMember LONG_MEMBER =
      javaPojoMemberBuilder()
          .pojoName(invoiceName())
          .name(JavaName.fromString("longVal"))
          .description("Description")
          .javaType(
              JavaIntegerType.wrap(
                  IntegerType.formatLong()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("12")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .memberXml(JavaPojoMemberXml.noDefinition())
          .build();

  private static final JavaPojoMember FLOAT_MEMBER =
      javaPojoMemberBuilder()
          .pojoName(invoiceName())
          .name(JavaName.fromString("floatVal"))
          .description("Description")
          .javaType(
              JavaNumericType.wrap(
                  NumericType.formatFloat()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("50.5")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .memberXml(JavaPojoMemberXml.noDefinition())
          .build();

  private static final JavaPojoMember DOUBLE_MEMBER =
      javaPojoMemberBuilder()
          .pojoName(invoiceName())
          .name(JavaName.fromString("doubleVal"))
          .description("Description")
          .javaType(
              JavaNumericType.wrap(
                  NumericType.formatDouble()
                      .withConstraints(
                          Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("12.25")))),
                  TypeMappings.empty()))
          .necessity(Necessity.REQUIRED)
          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
          .memberXml(JavaPojoMemberXml.noDefinition())
          .build();

  private static final JavaObjectPojo POJO =
      JavaPojos.objectPojo(PList.of(INTEGER_MEMBER, LONG_MEMBER, FLOAT_MEMBER, DOUBLE_MEMBER));

  @Test
  void generate_when_integerAndDoubleWithoutMultipleOfConstraint_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.of(
                    TestJavaPojoMembers.requiredDouble(), TestJavaPojoMembers.requiredInteger())),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("validationDisabled")
  void generate_when_validationDisabled_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(POJO, defaultTestSettings().withEnableValidation(false), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("integerMember")
  void generate_when_integer_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(INTEGER_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("longMember")
  void generate_when_long_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(LONG_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("floatMember")
  void generate_when_float_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(FLOAT_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("doubleMember")
  void generate_when_double_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = multipleOfValidationMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(PList.of(DOUBLE_MEMBER)),
            defaultTestSettings().withEnableValidation(true),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_stringWithUnsupportedMultipleOfConstraint_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = multipleOfValidationMethodGenerator();

    final JavaStringType stringType =
        JavaStringType.wrap(
            StringType.noFormat()
                .withConstraints(Constraints.ofMultipleOf(new MultipleOf(BigDecimal.ONE))),
            TypeMappings.empty());
    final TaskIdentifier taskIdentifier = TaskIdentifier.fromString(UUID.randomUUID().toString());

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.of(TestJavaPojoMembers.requiredString().withJavaType(stringType))),
            defaultTestSettings().withEnableValidation(true).withTaskIdentifier(taskIdentifier),
            javaWriter());

    assertEquals("", writer.asString());
    assertEquals(1, WarningsContext.getWarnings(taskIdentifier).getWarnings().size());
  }
}
