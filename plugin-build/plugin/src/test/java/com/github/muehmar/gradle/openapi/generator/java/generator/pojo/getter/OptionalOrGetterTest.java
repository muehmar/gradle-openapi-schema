package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalOrGetter.optionalOrGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredStringList;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class OptionalOrGetterTest {

  private Expect expect;

  @Test
  @SnapshotName("mappedString")
  void generate_when_mappedString_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = optionalOrGetterGenerator();

    final Writer writer =
        generator.generate(
            optionalString(TypeMappings.ofClassTypeMappings(STRING_MAPPING_WITH_CONVERSION)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("optionalString")
  void generate_when_optionalString_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = optionalOrGetterGenerator();

    final Writer writer = generator.generate(optionalString(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("genericType")
  void generate_when_genericType_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator = optionalOrGetterGenerator();

    final Writer writer =
        generator.generate(requiredStringList(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
