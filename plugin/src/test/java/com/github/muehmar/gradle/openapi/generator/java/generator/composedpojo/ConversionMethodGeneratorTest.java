package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static org.junit.jupiter.api.Assertions.*;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class ConversionMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("objectPojoWithSpecificAdditionalPropertiesType")
  void generate_when_objectPojoWithSpecificAdditionalPropertiesType_then_correctOutput() {
    final Generator<JavaPojo, PojoSettings> generator = ConversionMethodGenerator.asDtoMethod();

    final JavaObjectPojo pojo =
        JavaPojos.objectPojo(
            PList.single(JavaPojoMembers.requiredBirthdate()),
            JavaAdditionalProperties.allowedFor(
                JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty())));

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_STREAM_COLLECTORS::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("objectPojoWithAnyTypeAdditionalProperties")
  void generate_when_objectPojoWithAnyTypeAdditionalProperties_then_correctOutput() {
    final Generator<JavaPojo, PojoSettings> generator = ConversionMethodGenerator.asDtoMethod();

    final JavaObjectPojo pojo =
        JavaPojos.objectPojo(
            PList.single(JavaPojoMembers.requiredBirthdate()),
            JavaAdditionalProperties.allowedFor(JavaAnyType.create()));

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("pojoWithoutAdditionalProperties")
  void generate_when_pojoWithoutAdditionalProperties_then_correctOutput() {
    final Generator<JavaPojo, PojoSettings> generator = ConversionMethodGenerator.asDtoMethod();

    final JavaArrayPojo pojo = JavaPojos.arrayPojo();

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }
}
