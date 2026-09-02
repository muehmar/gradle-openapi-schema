package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerOptionalGetter.containerOptionalGetterGenerator;
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
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
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
        containerOptionalGetterGenerator(Visibility.PUBLIC);

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  private static Stream<Arguments> containerMembers() {
    final TypeMappings fullTypeMappings =
        TypeMappings.ofClassTypeMappings(
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
  @MethodSource("visibilities")
  @SnapshotName("visibility")
  void generate_when_visibility_then_matchSnapshot(Visibility visibility) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        containerOptionalGetterGenerator(visibility);

    final Writer writer;
    writer = generator.generate(requiredString(), defaultTestSettings(), javaWriter());

    expect.scenario(visibility.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  private static Stream<Arguments> visibilities() {
    return Stream.of(Visibility.values()).map(Arguments::arguments);
  }
}
