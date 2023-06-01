package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.MemberContent;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class MemberGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_samplePojo_then_correctOutputAndRef() {
    final Generator<MemberContent, PojoSettings> gen = MemberGenerator.generator();

    final Writer writer =
        gen.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants()).getMemberContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));

    final String output = writer.asString();
    expect.toMatchSnapshot(output);
  }

  @Test
  void generate_when_arrayPojo_then_correctOutputAndRef() {
    final Generator<MemberContent, PojoSettings> gen = MemberGenerator.generator();

    final Writer writer =
        gen.generate(
            JavaPojos.arrayPojo().getMemberContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_VALUE::equals));

    final String output = writer.asString();
    assertEquals("@JsonValue\n" + "private final List<Double> value;", output);
  }
}
