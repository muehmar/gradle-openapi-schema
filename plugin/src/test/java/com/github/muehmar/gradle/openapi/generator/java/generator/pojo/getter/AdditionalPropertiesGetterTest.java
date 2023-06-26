package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class AdditionalPropertiesGetterTest {
  private Expect expect;

  @Test
  @SnapshotName("additionalPropertiesTypeIsObject")
  void generate_when_additionalPropertiesTypeIsObject_then_validAnnotation() {
    final Generator<JavaPojo, PojoSettings> generator =
        AdditionalPropertiesGetter.additionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(
            JavaObjectType.wrap(ObjectType.ofName(PojoName.ofNameAndSuffix("Object", "Dto"))));
    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.empty(), additionalProperties);

    final Writer writer =
        generator.generate(
            pojo,
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("additionalPropertiesTypeIsStringWithConstraints")
  void generate_when_additionalPropertiesTypeIsStringWithConstraints_then_correctOutput() {
    final Generator<JavaPojo, PojoSettings> generator =
        AdditionalPropertiesGetter.additionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(
            JavaStringType.wrap(
                StringType.noFormat()
                    .withConstraints(Constraints.ofMinAndMax(new Min(3), new Max(5))),
                TypeMappings.empty()));
    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.empty(), additionalProperties);

    final Writer writer =
        generator.generate(
            pojo,
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("additionalPropertiesTypeIsList")
  void generate_when_additionalPropertiesTypeIsList_then_correctOutputAndRefs() {
    final Generator<JavaPojo, PojoSettings> generator =
        AdditionalPropertiesGetter.additionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.STRING_LIST);
    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.empty(), additionalProperties);

    final Writer writer =
        generator.generate(
            pojo,
            TestPojoSettings.defaultSettings().withEnableValidation(true),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_LIST::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generate_when_noAdditionalPropertiesAllowed_then_noOutput() {
    final Generator<JavaPojo, PojoSettings> generator =
        AdditionalPropertiesGetter.additionalPropertiesGetterGenerator();

    final JavaAdditionalProperties additionalProperties = JavaAdditionalProperties.notAllowed();
    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.empty(), additionalProperties);

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_standardObject_then_correctRefs() {
    final Generator<JavaPojo, PojoSettings> generator =
        AdditionalPropertiesGetter.additionalPropertiesGetterGenerator();

    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.empty());

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
  }
}
