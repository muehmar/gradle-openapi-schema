package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property.FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class FinalOptionalMemberBuilderGeneratorTest {
  private Expect expect;

  @SnapshotName("allNecessityAndNullabilityVariants")
  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        finalOptionalMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @SnapshotName("notNullableAdditionalProperties")
  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  void generate_when_notNullableAdditionalProperties_then_correctOutput(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        finalOptionalMemberBuilderGenerator(variant);

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType().withNullability(NOT_NULLABLE));
    final Writer writer =
        gen.generate(
            JavaPojos.objectPojo().withAdditionalProperties(additionalProperties),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(StagedBuilderVariant.class)
  @SnapshotName("noAdditionalPropertiesAllowed")
  void generate_when_noAdditionalPropertiesAllowed_then_noAdditionalPropertiesSetter(
      StagedBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        finalOptionalMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.objectPojo(PList.empty(), JavaAdditionalProperties.notAllowed()),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    assertEquals(0, writer.getRefs().size());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("stringTypeWithMappingAdditionalProperties")
  void generate_when_stringTypeWithMappingAdditionalProperties_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        finalOptionalMemberBuilderGenerator(StagedBuilderVariant.FULL);

    final TypeMappings stringTypeMapping =
        TypeMappings.ofClassTypeMappings(
            TaskIdentifier.fromString("test"), STRING_MAPPING_WITH_CONVERSION);
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
    final Generator<JavaObjectPojo, PojoSettings> generator =
        finalOptionalMemberBuilderGenerator(StagedBuilderVariant.FULL);

    final TypeMappings stringTypeMapping =
        TypeMappings.ofClassTypeMappings(
            TaskIdentifier.fromString("test"), STRING_MAPPING_WITH_CONVERSION);
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
