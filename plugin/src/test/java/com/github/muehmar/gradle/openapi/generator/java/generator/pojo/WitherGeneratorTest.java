package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static org.junit.jupiter.api.Assertions.*;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class WitherGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNullabilityAndNecessityVariants")
  void generate_when_calledWithNullabilityAndNecessityVariants_then_correctOutput() {
    final Generator<WitherGenerator.WitherContent, PojoSettings> generator =
        WitherGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants().getWitherContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("noAdditionalProperties")
  void generate_when_noAdditionalProperties_then_correctOutput() {
    final Generator<WitherGenerator.WitherContent, PojoSettings> generator =
        WitherGenerator.generator();
    final Writer writer =
        generator.generate(
            WitherContentBuilder.create()
                .className(JavaIdentifier.fromString("ObjectDto"))
                .members(PList.single(JavaPojoMembers.requiredString()))
                .build(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("propertyNameMatchesSubstringOfOtherProperty")
  void
      generate_when_pojoContainsPropertyNameWhichMatchesSubstringOfOtherProperty_then_correctOutput() {
    final JavaPojoMember surnameMember =
        JavaPojoMember.of(
            Name.ofString("surname"),
            "desc",
            JavaTypes.STRING,
            Necessity.REQUIRED,
            Nullability.NULLABLE);

    final JavaPojoMember nameMember =
        JavaPojoMember.of(
            Name.ofString("name"),
            "desc",
            JavaTypes.STRING,
            Necessity.OPTIONAL,
            Nullability.NULLABLE);

    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.of(surnameMember, nameMember));

    final Generator<WitherGenerator.WitherContent, PojoSettings> generator =
        WitherGenerator.generator();
    final Writer writer =
        generator.generate(
            pojo.getWitherContent(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }
}
