package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.RequiredAdditionalPropertiesSetterGenerator.requiredAdditionalPropertiesSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.anyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.nullableAnyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.nullableStringType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringType;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class RequiredAdditionalPropertiesSetterGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("pojoWithRequiredProperties")
  void generate_when_pojoWithRequiredProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        requiredAdditionalPropertiesSetterGenerator();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop1"), anyType()));

    final Writer writer =
        generator.generate(
            sampleObjectPojo1().withRequiredAdditionalProperties(requiredAdditionalProperties),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("pojoWithRequiredMappedStringProperty")
  void generate_when_pojoWithRequiredMappedStringProperty_then_apiTypedSetter() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        requiredAdditionalPropertiesSetterGenerator();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(
                Name.ofString("prop1"),
                JavaStringType.wrap(
                    StringType.noFormat(),
                    TypeMappings.ofSingleClassTypeMapping(
                        ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION))));

    final Writer writer =
        generator.generate(
            sampleObjectPojo1().withRequiredAdditionalProperties(requiredAdditionalProperties),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("pojoWithRequiredNullableMappedStringProperty")
  void generate_when_pojoWithRequiredNullableMappedStringProperty_then_apiTypedSetters() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        requiredAdditionalPropertiesSetterGenerator();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(
                Name.ofString("prop1"),
                JavaStringType.wrap(
                        StringType.noFormat(),
                        TypeMappings.ofSingleClassTypeMapping(
                            ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION))
                    .withNullability(Nullability.NULLABLE)));

    final Writer writer =
        generator.generate(
            sampleObjectPojo1().withRequiredAdditionalProperties(requiredAdditionalProperties),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("pojoWithRequiredNullableSpecificTypeProperty")
  void generate_when_pojoWithRequiredNullableSpecificTypeProperty_then_optionalSetterGenerated() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        requiredAdditionalPropertiesSetterGenerator();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(
                Name.ofString("prop1"), nullableStringType()));

    final Writer writer =
        generator.generate(
            sampleObjectPojo1().withRequiredAdditionalProperties(requiredAdditionalProperties),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("pojoWithRequiredNullableAnyTypeProperty")
  void generate_when_pojoWithRequiredNullableAnyTypeProperty_then_optionalSetterGenerated() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        requiredAdditionalPropertiesSetterGenerator();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(
                Name.ofString("prop1"), nullableAnyType()));

    final Writer writer =
        generator.generate(
            sampleObjectPojo1().withRequiredAdditionalProperties(requiredAdditionalProperties),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("pojoWithRequiredNotNullableSpecificTypeProperty")
  void generate_when_pojoWithRequiredNotNullableSpecificTypeProperty_then_onlyRawSetter() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        requiredAdditionalPropertiesSetterGenerator();
    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop1"), stringType()));

    final Writer writer =
        generator.generate(
            sampleObjectPojo1().withRequiredAdditionalProperties(requiredAdditionalProperties),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
