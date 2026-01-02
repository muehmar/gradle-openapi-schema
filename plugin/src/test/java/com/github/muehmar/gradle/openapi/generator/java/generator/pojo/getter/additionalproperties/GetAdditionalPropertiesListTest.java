package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.GetAdditionalPropertiesList.getAdditionalPropertiesListGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringListType;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class GetAdditionalPropertiesListTest {
  private Expect expect;

  @Test
  void generate_when_additionalPropertiesNotAllowed_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        getAdditionalPropertiesListGenerator();

    final JavaAdditionalProperties additionalProperties = JavaAdditionalProperties.notAllowed();
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("notNullableAnyType")
  void generate_when_notNullableAnyType_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        getAdditionalPropertiesListGenerator();

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
        getAdditionalPropertiesListGenerator();

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
        getAdditionalPropertiesListGenerator();

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
        getAdditionalPropertiesListGenerator();

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
        getAdditionalPropertiesListGenerator();

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
        getAdditionalPropertiesListGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(stringListType().withNullability(NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("notNullableStringTypeWithMapping")
  void generate_when_notNullableStringTypeWithMapping_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        getAdditionalPropertiesListGenerator();

    final TypeMappings stringTypeMapping =
        TypeMappings.ofClassTypeMappings(
            TaskIdentifier.fromString("test"), STRING_MAPPING_WITH_CONVERSION);
    final JavaStringType stringType = JavaStringType.wrap(StringType.noFormat(), stringTypeMapping);

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(stringType.withNullability(NOT_NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nullableStringTypeWithMapping")
  void generate_when_nullableStringTypeWithMapping_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        getAdditionalPropertiesListGenerator();

    final TypeMappings stringTypeMapping =
        TypeMappings.ofClassTypeMappings(
            TaskIdentifier.fromString("test"), STRING_MAPPING_WITH_CONVERSION);
    final JavaStringType stringType = JavaStringType.wrap(StringType.noFormat(), stringTypeMapping);

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(stringType.withNullability(NULLABLE));
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo().withAdditionalProperties(additionalProperties);

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
