package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerTristateGetter.containerTristateGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableMap;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableStringList;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredStringList;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION;
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
class ContainerTristateGetterTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("containerMembers")
  @SnapshotName("containerMembers")
  void generate_when_listMembers_then_matchSnapshot(JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        containerTristateGetterGenerator(Visibility.PUBLIC);

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  private static Stream<Arguments> containerMembers() {
    final TypeMappings fullTypeMappings =
        TypeMappings.ofClassTypeMappings(
            STRING_MAPPING_WITH_CONVERSION, LIST_MAPPING_WITH_CONVERSION);
    return Stream.of(
            optionalNullableStringList(),
            optionalNullableListWithNullableItems(),
            optionalNullableListWithNullableItems(fullTypeMappings)
                .withName(JavaName.fromString("optionalNullableListWithNullableItemsFullMapping")),
            optionalNullableMap())
        .map(Arguments::arguments);
  }

  @ParameterizedTest
  @MethodSource("visibilities")
  @SnapshotName("visibility")
  void generate_when_visibility_then_matchSnapshot(Visibility visibility) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        containerTristateGetterGenerator(visibility);

    final Writer writer;
    writer = generator.generate(requiredStringList(), defaultTestSettings(), javaWriter());

    expect.scenario(visibility.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  private static Stream<Arguments> visibilities() {
    return Stream.of(Visibility.values()).map(Arguments::arguments);
  }
}
