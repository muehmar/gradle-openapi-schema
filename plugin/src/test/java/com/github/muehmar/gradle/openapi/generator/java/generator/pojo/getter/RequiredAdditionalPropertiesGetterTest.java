package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.RequiredAdditionalPropertiesGetter.requiredAdditionalPropertiesGetter;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class RequiredAdditionalPropertiesGetterTest {
  private Expect expect;

  @Test
  @SnapshotName("requiredAdditionalObjectProperties")
  void generate_when_requiredAdditionalObjectProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        JavaPojos.withRequiredAdditionalProperties(
            sampleObjectPojo1(),
            PList.single(
                new JavaRequiredAdditionalProperty(
                    Name.ofString("prop1"),
                    JavaObjectType.wrap(
                        ObjectType.ofName(PojoName.ofName(Name.ofString("AdminDto")))))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalObjectPropertiesWithDeprecatedValidationMethod")
  void
      generate_when_requiredAdditionalObjectPropertiesWithDeprecatedValidationMethods_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        JavaPojos.withRequiredAdditionalProperties(
            sampleObjectPojo1(),
            PList.single(
                new JavaRequiredAdditionalProperty(
                    Name.ofString("prop1"),
                    JavaObjectType.wrap(
                        ObjectType.ofName(PojoName.ofName(Name.ofString("AdminDto")))))));

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
        JavaPojos.withRequiredAdditionalProperties(
            sampleObjectPojo1(),
            PList.single(
                new JavaRequiredAdditionalProperty(
                    Name.ofString("prop1"),
                    JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty()))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("requiredAdditionalAnyTypeProperties")
  void generate_when_requiredAdditionalAnyTypeProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = requiredAdditionalPropertiesGetter();

    final JavaObjectPojo pojo =
        JavaPojos.withRequiredAdditionalProperties(
            sampleObjectPojo1(),
            PList.single(
                new JavaRequiredAdditionalProperty(Name.ofString("prop1"), javaAnyType())));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
