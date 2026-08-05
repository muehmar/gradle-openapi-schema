package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.RequiredAdditionalPropertiesGetter.requiredAdditionalPropertiesGetter;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.anyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.nullableAnyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.nullableObjectType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.nullableStringListType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.nullableStringType;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@SnapshotTest
class RequiredAdditionalPropertiesGetterTest {
  private Expect expect;

  @Test
  @SnapshotName("requiredAdditionalObjectProperties")
  void generate_when_requiredAdditionalObjectProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"),
                        JavaObjectType.wrap(
                            StandardObjectType.ofName(PojoName.ofName(Name.ofString("AdminDto"))),
                            TypeMappings.empty()))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalObjectPropertiesWithDeprecatedValidationMethod")
  void
      generate_when_requiredAdditionalObjectPropertiesWithDeprecatedValidationMethods_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"),
                        JavaObjectType.wrap(
                            StandardObjectType.ofName(PojoName.ofName(Name.ofString("AdminDto"))),
                            TypeMappings.empty()))));

    final Writer writer =
        generator.generate(
            pojo,
            defaultTestSettings()
                .withValidationMethods(
                    ValidationMethodsBuilder.create()
                        .modifier(JavaModifier.PACKAGE_PRIVATE)
                        .getterSuffix("suffix")
                        .deprecatedAnnotation(true)
                        .build()),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalStringProperties")
  void generate_when_requiredAdditionalStringProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"),
                        JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty()))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalMappedStringProperties")
  void generate_when_requiredAdditionalMappedStringProperties_then_apiTypedGetterWithConversion() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"),
                        JavaStringType.wrap(
                            StringType.noFormat(),
                            TypeMappings.ofSingleClassTypeMapping(
                                ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION)))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalNullableMappedStringProperties")
  void
      generate_when_requiredAdditionalNullableMappedStringProperties_then_apiTypedGetterWithConversion() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"),
                        JavaStringType.wrap(
                                StringType.noFormat(),
                                TypeMappings.ofSingleClassTypeMapping(
                                    ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION))
                            .withNullability(Nullability.NULLABLE))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalMappedObjectProperties")
  void
      generate_when_requiredAdditionalMappedObjectProperties_then_validAnnotationOnInternalGetter() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final DtoMapping dtoMapping =
        new DtoMapping(
            "AdminDto",
            "com.custom.CustomAdmin",
            Optional.of(
                new TypeConversion(
                    "com.custom.CustomAdmin#toDto", "com.custom.CustomAdmin#fromDto")));
    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"),
                        JavaObjectType.wrap(
                            StandardObjectType.ofName(PojoName.ofName(Name.ofString("AdminDto"))),
                            TypeMappings.ofSingleDtoMapping(dtoMapping)))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalAnyTypeProperties")
  void generate_when_requiredAdditionalAnyTypeProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(JavaName.fromString("prop1"), anyType())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalNullableStringProperties")
  void generate_when_requiredAdditionalNullableStringProperties_then_noNotNullAsObjectMethod() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"), nullableStringType())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalNullableObjectProperties")
  void generate_when_requiredAdditionalNullableObjectProperties_then_validAnnotatedRawGetter() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"), nullableObjectType())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalNullableStringListProperties")
  void generate_when_requiredAdditionalNullableStringListProperties_then_compilableCast() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"), nullableStringListType())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalNullableAnyTypeProperties")
  void generate_when_requiredAdditionalNullableAnyTypeProperties_then_noNotNullAnnotation() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(
                        JavaName.fromString("prop1"), nullableAnyType())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
