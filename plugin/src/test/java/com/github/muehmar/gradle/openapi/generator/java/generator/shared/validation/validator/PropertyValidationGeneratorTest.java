package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValidationGenerator.memberValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.list;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.map;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredDouble;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredInteger;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaNumericType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
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
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class PropertyValidationGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("requiredStringWithoutCondition")
  void generate_when_requiredStringWithoutCondition_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember stringType =
        requiredString()
            .withNecessity(REQUIRED)
            .withNullability(NOT_NULLABLE)
            .withJavaType(
                JavaStringType.wrap(
                    StringType.noFormat().withConstraints(Constraints.empty()),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(stringType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredStringWithCondition")
  void generate_when_requiredStringWithCondition_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember stringType =
        requiredString()
            .withNecessity(REQUIRED)
            .withNullability(NOT_NULLABLE)
            .withJavaType(
                JavaStringType.wrap(
                    StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(5))),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(stringType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredNullableStringWithoutCondition")
  void generate_when_requiredNullableStringWithoutCondition_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember stringType =
        requiredString()
            .withNecessity(REQUIRED)
            .withNullability(NULLABLE)
            .withJavaType(
                JavaStringType.wrap(
                    StringType.noFormat().withConstraints(Constraints.empty()),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(stringType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredNullableStringWithCondition")
  void generate_when_requiredNullableStringWithCondition_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember stringType =
        requiredString()
            .withNecessity(REQUIRED)
            .withNullability(NULLABLE)
            .withJavaType(
                JavaStringType.wrap(
                    StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(5))),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(stringType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("integerWithMinAndMax")
  void generate_when_integerWithMinAndMax_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final Writer writer =
        generator.generate(requiredInteger(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("stringWithSize")
  void generate_when_stringWithMinAndMaxLength_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember stringType =
        requiredString()
            .withJavaType(
                JavaStringType.wrap(
                    StringType.noFormat().withConstraints(Constraints.ofSize(Size.of(10, 50))),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(stringType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("listWithSize")
  void generate_when_listWithMinAndMaxLength_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember listType =
        list(StringType.noFormat(), REQUIRED, NOT_NULLABLE, Constraints.ofSize(Size.of(10, 50)));

    final Writer writer = generator.generate(listType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("listUniqueItems")
  void generate_when_listUniqueItems_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember listType =
        list(StringType.noFormat(), REQUIRED, NOT_NULLABLE, Constraints.ofUniqueItems(true));

    final Writer writer = generator.generate(listType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mapWithSize")
  void generate_when_mapWithMinAndMaxLength_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember listType =
        map(
            StringType.noFormat(),
            StringType.noFormat(),
            REQUIRED,
            NOT_NULLABLE,
            Constraints.ofSize(Size.of(10, 50)));

    final Writer writer = generator.generate(listType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mapWithPropertyCount")
  void generate_when_mapWithPropertyCount_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember listType =
        map(
            StringType.noFormat(),
            StringType.noFormat(),
            REQUIRED,
            NOT_NULLABLE,
            Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(5, 10)));

    final Writer writer = generator.generate(listType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("byteArraySize")
  void generate_when_byteArrayWithMinAndMaxLength_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember byteArrayType =
        requiredString()
            .withJavaType(
                JavaStringType.wrap(
                    StringType.ofFormat(StringType.Format.BINARY)
                        .withConstraints(Constraints.ofSize(Size.of(10, 50))),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(byteArrayType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("doubleWithDecimalMinMaxExclusive")
  void generate_when_doubleWithDecimalMinMaxExclusive_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember doubleMember =
        requiredDouble()
            .withJavaType(
                JavaNumericType.wrap(
                    NumericType.formatDouble()
                        .withConstraints(
                            Constraints.ofDecimalMinAndMax(
                                new DecimalMin("50.1", false), new DecimalMax("100.1", false))),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(doubleMember, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("doubleWithDecimalMinMaxInclusive")
  void generate_when_doubleWithDecimalMinMaxInclusive_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember doubleMember =
        requiredDouble()
            .withJavaType(
                JavaNumericType.wrap(
                    NumericType.formatDouble()
                        .withConstraints(
                            Constraints.ofDecimalMinAndMax(
                                new DecimalMin("50.1", true), new DecimalMax("100.1", true))),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(doubleMember, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("stringWithPattern")
  void generate_when_stringWithPattern_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final Writer writer = generator.generate(requiredString(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("stringWithEmail")
  void generate_when_stringWithEmail_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final Writer writer =
        generator.generate(
            requiredString()
                .withJavaType(
                    JavaStringType.wrap(
                        StringType.noFormat().withConstraints(Constraints.ofEmail()),
                        TypeMappings.empty())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mapWithListValueType")
  void generate_when_mapWithListValueType_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final StringType stringType =
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMax(50)));
    final ArrayType listType =
        ArrayType.ofItemType(stringType, NOT_NULLABLE)
            .withConstraints(Constraints.ofSize(Size.ofMin(8)));
    final JavaPojoMember mapType =
        map(
            StringType.noFormat(),
            listType,
            REQUIRED,
            NOT_NULLABLE,
            Constraints.ofSize(Size.of(10, 50)));

    final Writer writer = generator.generate(mapType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("customObjectType")
  void generate_when_customObjectType_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember objectMember =
        TestJavaPojoMembers.object(QualifiedClassName.ofName("CustomObject"));

    final Writer writer = generator.generate(objectMember, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("openapiObjectType")
  void generate_when_openapiObjectType_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember objectMember =
        TestJavaPojoMembers.object(
            ObjectType.ofName(PojoName.ofNameAndSuffix("OpenapiObject", "Dto")));

    final Writer writer = generator.generate(objectMember, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("doubleMultipleOf")
  void generate_when_doubleMultipleOf_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember doubleMember =
        requiredDouble()
            .withJavaType(
                JavaNumericType.wrap(
                    NumericType.formatDouble()
                        .withConstraints(
                            Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("129")))),
                    TypeMappings.empty()));

    final Writer writer = generator.generate(doubleMember, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("unsupportedConstraintsForType")
  @SnapshotName("unsupportedConstraintsForType")
  void generate_when_unsupportedTypeAndConstraintsCombination_then_noConstraintValidation(
      JavaType javaType) {
    final Generator<JavaPojoMember, PojoSettings> generator = memberValidationGenerator();

    final JavaPojoMember javaPojoMember =
        requiredString().withName(JavaName.fromString("unsupported")).withJavaType(javaType);
    final TaskIdentifier taskIdentifier = TaskIdentifier.fromString(UUID.randomUUID().toString());

    final Writer writer =
        generator.generate(
            javaPojoMember, defaultTestSettings().withTaskIdentifier(taskIdentifier), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
    assertEquals(1, WarningsContext.getWarnings(taskIdentifier).getWarnings().size());
  }

  public static Stream<Arguments> unsupportedConstraintsForType() {
    final Constraints minConstraint = Constraints.ofMin(new Min(5));
    final Constraints maxConstraint = Constraints.ofMax(new Max(5));
    final Constraints decimalMinConstraint =
        Constraints.ofDecimalMin(DecimalMin.exclusive("20.25"));
    final Constraints decimalMaxConstraint =
        Constraints.ofDecimalMax(DecimalMax.exclusive("20.25"));
    final Constraints sizeConstraint = Constraints.ofSize(Size.ofMin(5));
    final Constraints patternConstraint =
        Constraints.ofPattern(Pattern.ofUnescapedString("pattern"));
    final Constraints emailConstraint = Constraints.ofEmail();
    final Constraints multipleOfConstraint =
        Constraints.ofMultipleOf(new MultipleOf(BigDecimal.ONE));

    return Stream.of(
            createStringType(minConstraint),
            createStringType(maxConstraint),
            createStringType(decimalMinConstraint),
            createStringType(decimalMaxConstraint),
            createStringType(multipleOfConstraint),
            createIntegerType(sizeConstraint),
            createIntegerType(emailConstraint),
            createIntegerType(patternConstraint))
        .map(Arguments::of);
  }

  private static JavaStringType createStringType(Constraints constraints) {
    return JavaStringType.wrap(
        StringType.noFormat().withConstraints(constraints), TypeMappings.empty());
  }

  private static JavaIntegerType createIntegerType(Constraints constraints) {
    return JavaIntegerType.wrap(
        IntegerType.formatInteger().withConstraints(constraints), TypeMappings.empty());
  }
}
