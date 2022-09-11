package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultSettings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NewValidationGeneratorTest {
  @Test
  void validationAnnotations_when_validationDisabled_then_noOutput() {
    final JavaPojoMember member = JavaPojoMembers.requiredBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer =
        generator.generate(
            member, defaultSettings().withEnableConstraints(false), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForRequiredField_then_notNullWithRef() {
    final JavaPojoMember member = JavaPojoMembers.requiredBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.NOT_NULL), writer.getRefs());
    assertEquals("@NotNull", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForRequiredButNullableField_then_noAnnotation() {
    final JavaPojoMember member = JavaPojoMembers.requiredNullableBirthdate();
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalReferenceField_then_validWithRef() {
    final JavaPojoMember member = JavaPojoMembers.reference(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForStringList_then_noValidAnnotation() {
    final JavaPojoMember member =
        JavaPojoMembers.list(StringType.noFormat(), Constraints.empty(), OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForObjectList_then_validAnnotation() {
    final JavaPojoMember member =
        JavaPojoMembers.list(
            ObjectType.ofName(PojoName.ofName(Name.ofString("UserDto"))),
            Constraints.empty(),
            OPTIONAL,
            NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.VALID), writer.getRefs());
    assertEquals("@Valid", writer.asString());
  }

  @ParameterizedTest
  @MethodSource("mapObjectTypes")
  void validationAnnotations_when_calledForMapWithObjectTypes_then_validAnnotation(
      NewType keyType, NewType valueType) {
    final JavaPojoMember member = JavaPojoMembers.map(keyType, valueType, OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.VALID), writer.getRefs());
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
      NewType keyType, NewType valueType) {
    final JavaPojoMember member = JavaPojoMembers.map(keyType, valueType, OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());
    assertEquals("", writer.asString());
  }

  public static Stream<Arguments> mapNonObjectTypes() {
    final ArrayType stringList = ArrayType.ofItemType(StringType.noFormat());
    final MapType stringLongMap =
        MapType.ofKeyAndValueType(StringType.noFormat(), NumericType.formatLong());
    return Stream.of(
        Arguments.arguments(NumericType.formatInteger(), StringType.noFormat()),
        Arguments.arguments(StringType.noFormat(), stringList),
        Arguments.arguments(StringType.noFormat(), stringLongMap));
  }

  @Test
  void validationAnnotations_when_calledForOptionalEmailField_then_emailWithRef() {
    final JavaPojoMember member = JavaPojoMembers.email(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.EMAIL), writer.getRefs());
    assertEquals("@Email", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalIntegerField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = JavaPojoMembers.integer(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaValidationRefs.MIN::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.MAX::equals));
    assertEquals("@Min(value = 10)\n" + "@Max(value = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalDoubleField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = JavaPojoMembers.doubleMember(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

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
    final JavaPojoMember member =
        JavaPojoMembers.list(
            StringType.noFormat(), Constraints.ofSize(Size.of(1, 50)), OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.SIZE), writer.getRefs());
    assertEquals("@Size(min = 1, max = 50)", writer.asString());
  }

  @Test
  void validationAnnotations_when_calledForOptionalStringField_then_minAndMaxWithRefs() {
    final JavaPojoMember member = JavaPojoMembers.string(OPTIONAL, NOT_NULLABLE);
    final Generator<JavaPojoMember, PojoSettings> generator =
        NewValidationGenerator.validationAnnotations();

    final Writer writer = generator.generate(member, defaultSettings(), Writer.createDefault());

    assertEquals(PList.single(JavaValidationRefs.PATTERN), writer.getRefs());
    assertEquals("@Pattern(regexp=\"Hello\")", writer.asString());
  }
}
