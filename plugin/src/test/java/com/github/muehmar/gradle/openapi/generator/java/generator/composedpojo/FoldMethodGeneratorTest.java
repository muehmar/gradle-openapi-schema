package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class FoldMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("OneOfNoDiscriminator")
  void generate_when_calledWithoutDiscriminator_then_correctContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        FoldMethodGenerator.foldMethodGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_FUNCTION::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_SUPPLIER::equals));
    assertEquals(2, writer.getRefs().distinct(Function.identity()).size());
  }

  @Test
  @SnapshotName("OneOfDiscriminatorWithoutMapping")
  void generate_when_calledWithDiscriminator_then_correctContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        FoldMethodGenerator.foldMethodGenerator();
    final Discriminator discriminator =
        Discriminator.fromPropertyName(JavaPojoMembers.requiredString().getName().asName());
    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfComposition.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(javaOneOfComposition),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_FUNCTION::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_SUPPLIER::equals));
    assertEquals(2, writer.getRefs().distinct(Function.identity()).size());
  }

  @Test
  @SnapshotName("OneOfDiscriminatorWithMapping")
  void generate_when_calledWithDiscriminatorAndMapping_then_correctContent() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        FoldMethodGenerator.foldMethodGenerator();
    final JavaObjectPojo sampleObjectPojo1 = sampleObjectPojo1();
    final JavaObjectPojo sampleObjectPojo2 = sampleObjectPojo2();

    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("obj1", sampleObjectPojo1.getSchemaName().asName());
    mapping.put("obj2", sampleObjectPojo2.getSchemaName().asName());
    final Discriminator discriminator =
        Discriminator.fromPropertyNameAndMapping(
            JavaPojoMembers.requiredString().getName().asName(), mapping);

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfComposition.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1, sampleObjectPojo2), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(javaOneOfComposition),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_FUNCTION::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_SUPPLIER::equals));
    assertEquals(2, writer.getRefs().distinct(Function.identity()).size());
  }

  @Test
  @SnapshotName("AnyOf")
  void generate_when_anyOf_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        FoldMethodGenerator.foldMethodGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_ARRAY_LIST::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_FUNCTION::equals));
    assertEquals(3, writer.getRefs().distinct(Function.identity()).size());
  }
}
