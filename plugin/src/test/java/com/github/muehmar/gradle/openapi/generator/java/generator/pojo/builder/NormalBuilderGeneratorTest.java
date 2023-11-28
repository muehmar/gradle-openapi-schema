package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.NormalBuilderGenerator.normalBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringListType;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@SnapshotTest
class NormalBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = normalBuilderGenerator();

    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop1"), javaAnyType()));

    final Writer writer =
        generator.generate(
            allNecessityAndNullabilityVariants()
                .withRequiredAdditionalProperties(requiredAdditionalProperties),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_additionalPropertyTypeIsList_then_containsListInRefs() {
    final Generator<JavaObjectPojo, PojoSettings> generator = normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.empty(), JavaAdditionalProperties.allowedFor(stringListType())),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("allNecessityAndNullabilityVariantsDisabledJackson")
  void generate_when_allNecessityAndNullabilityVariantsDisabledJackson_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            allNecessityAndNullabilityVariants(),
            defaultTestSettings().withJsonSupport(JsonSupport.NONE),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("allNecessityAndNullabilityVariantsDisabledSafeBuilder")
  void generate_when_allNecessityAndNullabilityVariantsDisabledSafeBuilder_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            allNecessityAndNullabilityVariants(),
            defaultTestSettings().withEnableSafeBuilder(false),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojo")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nestedRequiredAdditionalPropertiesInAllOfSubPojos")
  void generate_when_nestedRequiredAdditionalPropertiesInAllOfSubPojos_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = normalBuilderGenerator();

    final JavaRequiredAdditionalProperty prop1 =
        JavaRequiredAdditionalProperty.fromNameAndType(
            Name.ofString("prop1"), JavaTypes.stringType());
    final JavaRequiredAdditionalProperty prop2 =
        JavaRequiredAdditionalProperty.fromNameAndType(
            Name.ofString("prop2"), JavaTypes.integerType());

    final JavaObjectPojo pojo =
        sampleObjectPojo1()
            .withRequiredAdditionalProperties(PList.single(prop1))
            .withAllOfComposition(
                Optional.of(
                    JavaAllOfComposition.fromPojos(
                        NonEmptyList.single(
                            sampleObjectPojo2()
                                .withRequiredAdditionalProperties(PList.single(prop2))))));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
