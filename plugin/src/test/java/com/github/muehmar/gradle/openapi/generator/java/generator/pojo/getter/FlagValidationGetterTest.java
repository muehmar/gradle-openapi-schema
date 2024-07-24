package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FlagValidationGetter.flagValidationGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class FlagValidationGetterTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("members")
  @SnapshotName("members")
  void generate_when_members_then_matchSnapshot(JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        flagValidationGetterGenerator(GetterGeneratorSettings.empty());

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> members() {
    return Stream.of(requiredNullableString(), optionalString()).map(Arguments::arguments);
  }
}
