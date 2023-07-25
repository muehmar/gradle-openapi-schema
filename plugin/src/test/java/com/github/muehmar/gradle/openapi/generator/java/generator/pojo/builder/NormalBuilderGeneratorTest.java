package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class NormalBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        NormalBuilderGenerator.normalBuilderGenerator();

    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop1"), javaAnyType()));

    final Writer writer =
        generator.generate(
            JavaPojos.withRequiredAdditionalProperties(
                allNecessityAndNullabilityVariants(), requiredAdditionalProperties),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_POJO_BUILDER::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generate_when_additionalPropertyTypeIsList_then_containsListInRefs() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        NormalBuilderGenerator.normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                PList.empty(), JavaAdditionalProperties.allowedFor(JavaTypes.STRING_LIST)),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));
  }

  @Test
  @SnapshotName("allNecessityAndNullabilityVariantsDisabledJackson")
  void generate_when_allNecessityAndNullabilityVariantsDisabledJackson_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        NormalBuilderGenerator.normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("allNecessityAndNullabilityVariantsDisabledSafeBuilder")
  void generate_when_allNecessityAndNullabilityVariantsDisabledSafeBuilder_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        NormalBuilderGenerator.normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings().withEnableSafeBuilder(false),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("oneOfPojo")
  void generate_when_oneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        NormalBuilderGenerator.normalBuilderGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));

    expect.toMatchSnapshot(writer.asString());
  }
}
