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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class AnyOfFoldMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("AnyOf")
  void generate_when_anyOf_then_correctOutput() {
    final Generator<JavaComposedPojo, PojoSettings> generator =
        AnyOfFoldMethodGenerator.generator();

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

  @Test
  void generate_when_oneOf_then_noOutput() {
    final Generator<JavaComposedPojo, PojoSettings> generator =
        AnyOfFoldMethodGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.composedPojo(ONE_OF),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertEquals(0, writer.getRefs().size());
  }
}
