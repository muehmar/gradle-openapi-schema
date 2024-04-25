package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.RequiredAdditionalPropertiesGetter.requiredAdditionalPropertiesGetter;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.anyType;
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
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
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
                            StandardObjectType.ofName(
                                PojoName.ofName(Name.ofString("AdminDto")))))));

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
                            StandardObjectType.ofName(
                                PojoName.ofName(Name.ofString("AdminDto")))))));

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
}
