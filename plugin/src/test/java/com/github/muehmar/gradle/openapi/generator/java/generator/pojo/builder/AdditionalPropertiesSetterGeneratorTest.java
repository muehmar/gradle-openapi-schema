package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.AdditionalPropertiesSetterGenerator.additionalPropertiesSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.objectType;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class AdditionalPropertiesSetterGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("objectPojoAnyTypeAdditionalProperties")
  void generate_when_objectPojoAnyTypeAdditionalProperties_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = additionalPropertiesSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(requiredBirthdate()), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("allNecessityAndNullabilityVariantsPojo")
  void generate_when_allNecessityAndNullabilityVariantsPojo_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = additionalPropertiesSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("objectPojoSpecificTypeAdditionalProperties")
  void generate_when_objectPojoSpecificTypeAdditionalProperties_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = additionalPropertiesSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.single(requiredBirthdate()),
                JavaAdditionalProperties.allowedFor(objectType())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("stringTypeWithMappingAdditionalProperties")
  void generate_when_stringTypeWithMappingAdditionalProperties_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = additionalPropertiesSetterGenerator();

    final TypeMappings stringTypeMapping =
        TypeMappings.ofClassTypeMappings(STRING_MAPPING_WITH_CONVERSION);
    final JavaStringType stringType = JavaStringType.wrap(StringType.noFormat(), stringTypeMapping);

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.single(requiredBirthdate()), JavaAdditionalProperties.allowedFor(stringType)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nullableStringTypeWithMappingAdditionalProperties")
  void generate_when_nullableStringTypeWithMappingAdditionalProperties_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = additionalPropertiesSetterGenerator();

    final TypeMappings stringTypeMapping =
        TypeMappings.ofClassTypeMappings(STRING_MAPPING_WITH_CONVERSION);
    final JavaStringType stringType =
        JavaStringType.wrap(
            StringType.noFormat().withNullability(Nullability.NULLABLE), stringTypeMapping);

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.single(requiredBirthdate()), JavaAdditionalProperties.allowedFor(stringType)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
