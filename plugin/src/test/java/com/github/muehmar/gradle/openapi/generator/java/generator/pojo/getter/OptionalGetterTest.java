package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalGetter.optionalGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JAVA_DOC;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredStringList;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class OptionalGetterTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("generatorSettings")
  @SnapshotName("generatorSettings")
  void generate_when_generatorSettings_then_matchSnapshot(
      GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalGetterGenerator(generatorSettings);

    final Writer writer = generator.generate(optionalString(), defaultTestSettings(), javaWriter());

    expect
        .scenario(generatorSettings.getSettings().mkString("|"))
        .toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> generatorSettings() {
    return Stream.<PList<GetterGeneratorSetting>>of(PList.single(NO_JAVA_DOC), PList.empty())
        .map(GetterGeneratorSettings::new)
        .map(Arguments::arguments);
  }

  @Test
  @SnapshotName("genericType")
  void generate_when_genericType_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        optionalGetterGenerator(GetterGeneratorSettings.empty());

    final Writer writer =
        generator.generate(requiredStringList(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
