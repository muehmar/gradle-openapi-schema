package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.TristateGetter.tristateGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JAVA_DOC;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.PACKAGE_PRIVATE;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredStringList;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
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
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class TristateGetterTest {
  private Expect expect;

  @Test
  @SnapshotName("mappedString")
  void generate_when_mappedString_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        tristateGetterGenerator(GetterGeneratorSettings.empty());

    final Writer writer =
        generator.generate(
            optionalNullableString(
                TypeMappings.ofClassTypeMappings(
                    TaskIdentifier.fromString("test"), STRING_MAPPING_WITH_CONVERSION)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("generatorSettings")
  @SnapshotName("generatorSettings")
  void generate_when_generatorSettings_then_matchSnapshot(
      GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        tristateGetterGenerator(generatorSettings);

    final Writer writer =
        generator.generate(optionalNullableString(), defaultTestSettings(), javaWriter());

    expect
        .scenario(generatorSettings.getSettings().mkString("|"))
        .toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> generatorSettings() {
    return Stream.<PList<GetterGeneratorSetting>>of(
            PList.single(NO_JAVA_DOC), PList.single(PACKAGE_PRIVATE), PList.empty())
        .map(GetterGeneratorSettings::new)
        .map(Arguments::arguments);
  }

  @Test
  @SnapshotName("genericType")
  void generate_when_genericType_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        tristateGetterGenerator(GetterGeneratorSettings.empty());

    final Writer writer =
        generator.generate(requiredStringList(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
