package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.ref.Jakarta2ValidationRefs;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
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
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class ValidationGeneratorTest {

  private Expect expect;

  private static Stream<Arguments> settingsVariants() {
    return TestPojoSettings.validationVariants().map(Arguments::arguments).toStream();
  }

  @ParameterizedTest
  @SnapshotName("assertTrueAnnotation")
  @MethodSource("settingsVariants")
  void assertTrue_when_settings_then_correctOutput(PojoSettings settings) {
    final Generator<String, PojoSettings> generator =
        ValidationGenerator.assertTrue(Function.identity());
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
        ValidationGenerator.assertFalse(Function.identity());
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
    final JavaPojoMember member = JavaPojoMembers.requiredBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer =
        generator.generate(member, defaultTestSettings().withEnableValidation(false), javaWriter());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForRequiredField_then_notNullWithRef() {
    final JavaPojoMember member = JavaPojoMembers.requiredBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.NOT_NULL), writer.getRefs());
    assertEquals("@NotNull", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForRequiredButNullableField_then_noAnnotation() {
    final JavaPojoMember member = JavaPojoMembers.requiredNullableBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalReferenceField_then_validWithRef() {
    final JavaPojoMember member = JavaPojoMembers.reference(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForStringList_then_noValidAnnotation() {
    final JavaPojoMember member =
        JavaPojoMembers.list(StringType.noFormat(), OPTIONAL, NOT_NULLABLE, Constraints.empty());
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForObjectList_then_validAnnotation() {
    final JavaPojoMember member =
        JavaPojoMembers.list(
            ObjectType.ofName(PojoName.ofName(Name.ofString("UserDto"))),
            OPTIONAL,
            NOT_NULLABLE,
            Constraints.empty());
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  @ParameterizedTest
  @MethodSource("mapObjectTypes")
  void validationAnnotations_when_calledForMapWithObjectTypes_then_validAnnotation(
      Type keyType, Type valueType) {
    final JavaPojoMember member = JavaPojoMembers.map(keyType, valueType, OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

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
    final JavaPojoMember member = JavaPojoMembers.map(keyType, valueType, OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

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
    final JavaPojoMember member = JavaPojoMembers.email(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.EMAIL), writer.getRefs());
    assertEquals("@Email", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalIntegerField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = JavaPojoMembers.integer(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.MIN::equals));
    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.MAX::equals));
    assertEquals("@Min(value = 10)\n" + "@Max(value = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalDoubleField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = JavaPojoMembers.doubleMember(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

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
        JavaPojoMembers.list(
            StringType.noFormat(), OPTIONAL, NOT_NULLABLE, Constraints.ofSize(Size.of(1, 50)));
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.SIZE), writer.getRefs());
    assertEquals("@Size(min = 1, max = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalStringField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = JavaPojoMembers.string(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        ValidationGenerator.validationAnnotationsForMember();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    assertEquals(PList.single(Jakarta2ValidationRefs.PATTERN), writer.getRefs());
    assertEquals("@Pattern(regexp=\"Hello\")", writer.asString());
  }
}
