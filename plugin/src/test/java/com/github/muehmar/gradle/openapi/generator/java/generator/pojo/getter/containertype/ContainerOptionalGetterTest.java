package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerOptionalGetter.containerOptionalGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.PACKAGE_PRIVATE;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableMap;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableStringList;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.MAP_MAPPING_WITH_CONVERSION;
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
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class ContainerOptionalGetterTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("containerMembers")
  @SnapshotName("containerMembers")
  void generate_when_listMembers_then_matchSnapshot(JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        containerOptionalGetterGenerator(GetterGeneratorSettings.empty());

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  private static Stream<Arguments> containerMembers() {
    final TypeMappings fullTypeMappings =
        TypeMappings.ofClassTypeMappings(
            TaskIdentifier.fromString("test"),
            STRING_MAPPING_WITH_CONVERSION,
            LIST_MAPPING_WITH_CONVERSION,
            MAP_MAPPING_WITH_CONVERSION);

    return Stream.of(
            requiredNullableStringList(),
            requiredNullableListWithNullableItems(),
            optionalListWithNullableItems(fullTypeMappings)
                .withName(JavaName.fromString("optionalListWithNullableItemsFullMapping")),
            requiredNullableMap(),
            requiredNullableMap(fullTypeMappings)
                .withName(JavaName.fromString("optionalMapFullMapping")))
        .map(Arguments::arguments);
  }

  @ParameterizedTest
  @MethodSource("generatorSettings")
  @SnapshotName("generatorSettings")
  void generate_when_generatorSettings_then_matchSnapshot(
      GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        containerOptionalGetterGenerator(generatorSettings);

    final Writer writer;
    writer = generator.generate(requiredString(), defaultTestSettings(), javaWriter());

    expect
        .scenario(generatorSettings.getSettings().mkString("|"))
        .toMatchSnapshot(writerSnapshot(writer));
  }

  private static Stream<Arguments> generatorSettings() {
    return Stream.<PList<GetterGeneratorSetting>>of(PList.empty(), PList.of(PACKAGE_PRIVATE))
        .map(GetterGeneratorSettings::new)
        .map(Arguments::arguments);
  }
}
