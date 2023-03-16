package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ANY_OF;
import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ONE_OF;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class FoldMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("OneOfNoDiscriminator")
  void generate_when_calledWithoutDiscriminator_then_correctContent() {
    final Generator<JavaComposedPojo, PojoSettings> generator = FoldMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.composedPojo(ONE_OF),
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
    final Generator<JavaComposedPojo, PojoSettings> generator = FoldMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.composedPojoWithDiscriminator(ONE_OF),
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
    final Generator<JavaComposedPojo, PojoSettings> generator = FoldMethodGenerator.generator();
    final Writer writer =
        generator.generate(
            JavaPojos.composedPojoWithDiscriminatorMapping(ONE_OF),
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
    final Generator<JavaComposedPojo, PojoSettings> generator = FoldMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.composedPojo(ANY_OF),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
    assertEquals(3, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_LIST::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_ARRAY_LIST::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_FUNCTION::equals));
  }
}
