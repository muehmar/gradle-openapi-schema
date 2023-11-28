package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForPropertyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.ref.Jakarta2ValidationRefs;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import com.github.muehmar.gradle.openapi.warnings.WarningsContext;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class ValidationAnnotationGeneratorTest {

  private Expect expect;

  private static Stream<Arguments> settingsVariants() {
    return TestPojoSettings.validationVariants().map(Arguments::arguments).toStream();
  }

  @ParameterizedTest
  @SnapshotName("assertTrueAnnotation")
  @MethodSource("settingsVariants")
  void assertTrue_when_settings_then_correctOutput(PojoSettings settings) {
    final Generator<String, PojoSettings> generator =
        ValidationAnnotationGenerator.assertTrue(Function.identity());
    final Writer writer = generator.generate("Message", settings, javaWriter());

    final String scenario =
        PList.of(
                Boolean.toString(settings.isEnableValidation()),
                settings.getValidationApi().getValue())
            .mkString(",");

    expect
        .scenario(scenario)
        .toMatchSnapshot(writer.getRefs().mkString("\n") + "\n\n" + writer.asString());
  }

  @ParameterizedTest
  @SnapshotName("assertFalseAnnotation")
  @MethodSource("settingsVariants")
  void assertFalse_when_settings_then_correctOutput(PojoSettings settings) {
    final Generator<String, PojoSettings> generator =
        ValidationAnnotationGenerator.assertFalse(Function.identity());
    final Writer writer = generator.generate("Message", settings, javaWriter());

    final String scenario =
        PList.of(
                Boolean.toString(settings.isEnableValidation()),
                settings.getValidationApi().getValue())
            .mkString(",");

    expect
        .scenario(scenario)
        .toMatchSnapshot(writer.getRefs().mkString("\n") + "\n\n" + writer.asString());
  }

  @Test
  void validationAnnotations_when_validationDisabled_then_noOutput() {
    final JavaPojoMember member = TestJavaPojoMembers.requiredBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer =
        generator.generate(member, defaultTestSettings().withEnableValidation(false), javaWriter());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForRequiredField_then_notNullWithRef() {
    final JavaPojoMember member = TestJavaPojoMembers.requiredBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.NOT_NULL), writer.getRefs());
    assertEquals("@NotNull", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForRequiredButNullableField_then_noAnnotation() {
    final JavaPojoMember member = TestJavaPojoMembers.requiredNullableBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalReferenceField_then_validWithRef() {
    final JavaPojoMember member = TestJavaPojoMembers.reference(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForStringList_then_noValidAnnotation() {
    final JavaPojoMember member =
        TestJavaPojoMembers.list(
            StringType.noFormat(), OPTIONAL, NOT_NULLABLE, Constraints.empty());
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForObjectList_then_validAnnotation() {
    final JavaPojoMember member =
        TestJavaPojoMembers.list(
            ObjectType.ofName(PojoName.ofName(Name.ofString("UserDto"))),
            OPTIONAL,
            NOT_NULLABLE,
            Constraints.empty());
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  @ParameterizedTest
  @MethodSource("mapObjectTypes")
  void validationAnnotations_when_calledForMapWithObjectTypes_then_validAnnotation(
      Type keyType, Type valueType) {
    final JavaPojoMember member =
        TestJavaPojoMembers.map(keyType, valueType, OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  public static Stream<Arguments> mapObjectTypes() {
    final ObjectType objectType = ObjectType.ofName(PojoName.ofName(Name.ofString("AnyDto")));
    final ArrayType objectList = ArrayType.ofItemType(objectType);
    return Stream.of(
        Arguments.of(objectType, objectType),
        Arguments.arguments(StringType.noFormat(), objectType),
        Arguments.arguments(objectType, StringType.noFormat()),
        Arguments.arguments(objectList, StringType.noFormat()));
  }

  @ParameterizedTest
  @MethodSource("mapNonObjectTypes")
  void validationAnnotations_when_calledForMapWithNonObjectTypes_then_noValidAnnotation(
      Type keyType, Type valueType) {
    final JavaPojoMember member =
        TestJavaPojoMembers.map(keyType, valueType, OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  public static Stream<Arguments> mapNonObjectTypes() {
    final ArrayType stringList = ArrayType.ofItemType(StringType.noFormat());
    final MapType stringLongMap =
        MapType.ofKeyAndValueType(StringType.noFormat(), IntegerType.formatLong());
    return Stream.of(
        Arguments.arguments(IntegerType.formatInteger(), StringType.noFormat()),
        Arguments.arguments(StringType.noFormat(), stringList),
        Arguments.arguments(StringType.noFormat(), stringLongMap));
  }

  @Test
  void validationAnnotations_when_calledForOptionalEmailField_then_emailWithRef() {
    final JavaPojoMember member = TestJavaPojoMembers.email(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.EMAIL), writer.getRefs());
    assertEquals("@Email", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalIntegerField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = TestJavaPojoMembers.integer(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.MIN::equals));
    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.MAX::equals));
    assertEquals("@Min(value = 10)\n" + "@Max(value = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalDoubleField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = TestJavaPojoMembers.doubleMember(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.DECIMAL_MIN::equals));
    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.DECIMAL_MAX::equals));
    assertEquals(
        "@DecimalMin(value = \"12.5\", inclusive = true)\n"
            + "@DecimalMax(value = \"50.1\", inclusive = false)",
        writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalStringListField_then_minAndMaxWithRefs() {
    final JavaPojoMember member =
        TestJavaPojoMembers.list(
            StringType.noFormat(), OPTIONAL, NOT_NULLABLE, Constraints.ofSize(Size.of(1, 50)));
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.SIZE), writer.getRefs());
    assertEquals("@Size(min = 1, max = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalStringField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = TestJavaPojoMembers.string(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator = validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.PATTERN), writer.getRefs());
    assertEquals("@Pattern(regexp=\"Hello\")", writer.asString());
  }

  @ParameterizedTest
  @MethodSource("unsupportedConstraintsForType")
  void validationAnnotationsForType_when_unsupportedConstraintForType_then_notOutput(
      JavaType javaType) {
    final Generator<ValidationAnnotationGenerator.PropertyType, PojoSettings> generator =
        validationAnnotationsForPropertyType();
    final TaskIdentifier taskIdentifier = TaskIdentifier.fromString(UUID.randomUUID().toString());

    final PropertyInfoName propertyInfoName =
        PropertyInfoName.fromPojoNameAndMemberName(invoiceName(), JavaName.fromString("property"));
    final ValidationAnnotationGenerator.PropertyType propertyType =
        new ValidationAnnotationGenerator.PropertyType(propertyInfoName, javaType);

    final Writer writer =
        generator.generate(
            propertyType, defaultTestSettings().withTaskIdentifier(taskIdentifier), javaWriter());

    assertEquals("", writer.asString());
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

    return Stream.of(
            createStringType(minConstraint),
            createStringType(maxConstraint),
            createStringType(decimalMinConstraint),
            createStringType(decimalMaxConstraint),
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
