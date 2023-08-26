package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class DiscriminatorValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("NoDiscriminator")
  void generate_when_calledWithoutDiscriminator_then_noContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            TestPojoSettings.defaultSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("DiscriminatorWithoutMapping")
  void generate_when_calledWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator();

    final Discriminator noMappingDiscriminator =
        Discriminator.fromPropertyName(JavaPojoMembers.requiredString().getName().asName());

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
            JavaOneOfComposition.fromPojosAndDiscriminator(
                NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), noMappingDiscriminator));

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("DiscriminatorWithMapping")
  void generate_when_calledWithDiscriminatorAndMapping_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator();

    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("obj1", sampleObjectPojo1().getSchemaName().asName());
    mapping.put("obj2", sampleObjectPojo2().getSchemaName().asName());

    final Name propertyName = JavaPojoMembers.requiredString().getName().asName();
    final Discriminator discriminator =
        Discriminator.fromPropertyNameAndMapping(propertyName, mapping);

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
            JavaOneOfComposition.fromPojosAndDiscriminator(
                NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator));

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("DiscriminatorWithoutMappingWithProtectedAndDeprecatedValidationSettings")
  void generate_when_protectedAndDeprecatedValidationSettings_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator();

    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("obj1", sampleObjectPojo1().getSchemaName().asName());
    mapping.put("obj2", sampleObjectPojo2().getSchemaName().asName());

    final Name propertyName = JavaPojoMembers.requiredString().getName().asName();
    final Discriminator discriminator =
        Discriminator.fromPropertyNameAndMapping(propertyName, mapping);

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
            JavaOneOfComposition.fromPojosAndDiscriminator(
                NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator));

    final Writer writer =
        generator.generate(
            pojo,
            TestPojoSettings.defaultSettings()
                .withValidationMethods(
                    TestPojoSettings.defaultValidationMethods()
                        .withModifier(JavaModifier.PROTECTED)
                        .withDeprecatedAnnotation(true)),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
