package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldMethodGenerator.foldMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.illegalIdentifierPojo;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

@SnapshotTest
class FoldMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("OneOfNoDiscriminator")
  void generate_when_calledWithoutDiscriminator_then_correctContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("OneOfDiscriminatorWithoutMapping")
  void generate_when_calledWithDiscriminator_then_correctContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();
    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());
    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(javaOneOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("IllegalIdentifierPojoDiscriminatorWithoutMapping")
  void generate_when_illegalIdentifierPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();

    final UntypedDiscriminator noMappingDiscriminator =
        UntypedDiscriminator.fromPropertyName(
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
  @SnapshotName("OneOfDiscriminatorWithMapping")
  void generate_when_calledWithDiscriminatorAndMapping_then_correctContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();
    final JavaObjectPojo sampleObjectPojo1 = sampleObjectPojo1();
    final JavaObjectPojo sampleObjectPojo2 = sampleObjectPojo2();

    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("obj1", sampleObjectPojo1.getSchemaName().getOriginalName());
    mapping.put("obj2", sampleObjectPojo2.getSchemaName().getOriginalName());
    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyNameAndMapping(
            TestJavaPojoMembers.requiredString().getName().getOriginalName(), mapping);

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1, sampleObjectPojo2), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(javaOneOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("OneOfPojoWithEnumDiscriminator")
  void generate_when_oneOfPojoWithEnumDiscriminator_then_correctContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();

    final JavaObjectPojo pojo = JavaPojos.oneOfPojoWithEnumDiscriminator();
    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("AnyOf")
  void generate_when_anyOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("AnyOfDiscriminatorWithoutMapping")
  void generate_when_anyOfDiscriminatorWithoutMapping_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();

    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());
    final JavaAnyOfComposition javaAnyOfComposition =
        JavaAnyOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(javaAnyOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("AnyOfDiscriminatorWithMapping")
  void generate_when_anyOfDiscriminatorWithMapping_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = foldMethodGenerator();
    final JavaObjectPojo sampleObjectPojo1 = sampleObjectPojo1();
    final JavaObjectPojo sampleObjectPojo2 = sampleObjectPojo2();

    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("obj1", sampleObjectPojo1.getSchemaName().getOriginalName());
    mapping.put("obj2", sampleObjectPojo2.getSchemaName().getOriginalName());

    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyNameAndMapping(
            TestJavaPojoMembers.requiredString().getName().getOriginalName(), mapping);
    final JavaAnyOfComposition javaAnyOfComposition =
        JavaAnyOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1, sampleObjectPojo2), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(javaAnyOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
