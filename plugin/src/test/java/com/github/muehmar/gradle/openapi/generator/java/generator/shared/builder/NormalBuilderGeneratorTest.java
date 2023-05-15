package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

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
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class NormalBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput() {
    final NormalBuilderGenerator generator = new NormalBuilderGenerator();

    final Writer writer =
        generator.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants())
                .getNormalBuilderContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_POJO_BUILDER::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  void generate_when_additionalPropertyTypeIsList_then_containsListInRefs() {
    final NormalBuilderGenerator generator = new NormalBuilderGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(
                    PList.empty(), JavaAdditionalProperties.allowedFor(JavaTypes.STRING_LIST))
                .getNormalBuilderContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_LIST::equals));
  }

  @Test
  @SnapshotName("allNecessityAndNullabilityVariantsDisabledJackson")
  void generate_when_allNecessityAndNullabilityVariantsDisabledJackson_then_correctOutput() {
    final NormalBuilderGenerator generator = new NormalBuilderGenerator();

    final Writer writer =
        generator.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants())
                .getNormalBuilderContent(),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("allNecessityAndNullabilityVariantsDisabledSafeBuilder")
  void generate_when_allNecessityAndNullabilityVariantsDisabledSafeBuilder_then_correctOutput() {
    final NormalBuilderGenerator generator = new NormalBuilderGenerator();

    final Writer writer =
        generator.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants())
                .getNormalBuilderContent(),
            TestPojoSettings.defaultSettings().withEnableSafeBuilder(false),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));

    expect.toMatchSnapshot(writer.asString());
  }
}
