package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.FrameworkAdditionalPropertiesGetter.frameworkAdditionalPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringListType;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationMethods;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class FrameworkAdditionalPropertiesGetterTest {
  private Expect expect;

  @Test
  void generate_when_additionalPropertiesNotAllowed_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties = JavaAdditionalProperties.notAllowed();
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("notNullableStringType")
  void generate_when_notNullableStringType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType().withNullability(NOT_NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nullableStringType")
  void generate_when_nullableStringType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType().withNullability(NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("notNullableStringTypeWithConstraints")
  void generate_when_notNullableStringTypeWithConstraints_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(
            JavaStringType.wrap(
                StringType.noFormat()
                    .withNullability(NOT_NULLABLE)
                    .withConstraints(
                        Constraints.ofSize(Size.ofMin(5))
                            .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern")))),
                TypeMappings.empty()));
    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.empty(), additionalProperties);

    final Writer writer =
        generator.generate(pojo, defaultTestSettings().withEnableValidation(true), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nullableStringTypeWithConstraints")
  void generate_when_nullableStringTypeWithConstraints_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(
            JavaStringType.wrap(
                StringType.noFormat()
                    .withNullability(NULLABLE)
                    .withConstraints(
                        Constraints.ofSize(Size.ofMin(5))
                            .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern")))),
                TypeMappings.empty()));
    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.empty(), additionalProperties);

    final Writer writer =
        generator.generate(pojo, defaultTestSettings().withEnableValidation(true), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("notNullableAnyType")
  void generate_when_notNullableAnyType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.anyType().withNullability(NOT_NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nullableAnyType")
  void generate_when_nullableAnyType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.anyType().withNullability(NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("notNullableObjectType")
  void generate_when_notNullableObjectType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(
            JavaObjectType.wrap(
                    StandardObjectType.ofName(pojoName("Object", "Dto")), TypeMappings.empty())
                .withNullability(NOT_NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nullableObjectType")
  void generate_when_nullableObjectType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(
            JavaObjectType.wrap(
                    StandardObjectType.ofName(pojoName("Object", "Dto")), TypeMappings.empty())
                .withNullability(NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("notNullableListType")
  void generate_when_notNullableListType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(stringListType().withNullability(NOT_NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nullableListType")
  void generate_when_nullableListType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(stringListType().withNullability(NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("deprecationAnnotation")
  void generate_when_deprecationAnnotation_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        frameworkAdditionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.anyType());
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer =
        generator.generate(
            pojo,
            defaultTestSettings()
                .withValidationMethods(new ValidationMethods(JavaModifier.PUBLIC, "Raw", true)),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
