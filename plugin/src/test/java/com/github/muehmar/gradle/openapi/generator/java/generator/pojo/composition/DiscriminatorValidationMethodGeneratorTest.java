package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.illegalIdentifierPojo;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

@SnapshotTest
class DiscriminatorValidationMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("NoDiscriminator")
  void generate_when_calledWithoutDiscriminator_then_noContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        discriminatorValidationMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("DiscriminatorWithoutMapping")
  void generate_when_calledWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        discriminatorValidationMethodGenerator();

    final Discriminator noMappingDiscriminator =
        Discriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
            JavaOneOfCompositions.fromPojosAndDiscriminator(
                NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), noMappingDiscriminator));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("IllegalIdentifierPojoDiscriminatorWithoutMapping")
  void generate_when_illegalIdentifierPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        discriminatorValidationMethodGenerator();

    final Discriminator noMappingDiscriminator =
        Discriminator.fromPropertyName(
            TestJavaPojoMembers.keywordNameString().getName().getOriginalName());

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
            JavaOneOfCompositions.fromPojosAndDiscriminator(
                NonEmptyList.of(illegalIdentifierPojo(), illegalIdentifierPojo()),
                noMappingDiscriminator));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("DiscriminatorWithMapping")
  void generate_when_calledWithDiscriminatorAndMapping_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        discriminatorValidationMethodGenerator();

    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("obj1", sampleObjectPojo1().getSchemaName().getOriginalName());
    mapping.put("obj2", sampleObjectPojo2().getSchemaName().getOriginalName());

    final Name propertyName = TestJavaPojoMembers.requiredString().getName().getOriginalName();
    final Discriminator discriminator =
        Discriminator.fromPropertyNameAndMapping(propertyName, mapping);

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
            JavaOneOfCompositions.fromPojosAndDiscriminator(
                NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator));

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("DiscriminatorWithoutMappingWithProtectedAndDeprecatedValidationSettings")
  void generate_when_protectedAndDeprecatedValidationSettings_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        discriminatorValidationMethodGenerator();

    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("obj1", sampleObjectPojo1().getSchemaName().getOriginalName());
    mapping.put("obj2", sampleObjectPojo2().getSchemaName().getOriginalName());

    final Name propertyName = TestJavaPojoMembers.requiredString().getName().getOriginalName();
    final Discriminator discriminator =
        Discriminator.fromPropertyNameAndMapping(propertyName, mapping);

    final JavaObjectPojo pojo =
        JavaPojos.oneOfPojo(
            JavaOneOfCompositions.fromPojosAndDiscriminator(
                NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator));

    final Writer writer =
        generator.generate(
            pojo,
            defaultTestSettings()
                .withValidationMethods(
                    TestPojoSettings.defaultValidationMethods()
                        .withModifier(JavaModifier.PROTECTED)
                        .withDeprecatedAnnotation(true)),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
