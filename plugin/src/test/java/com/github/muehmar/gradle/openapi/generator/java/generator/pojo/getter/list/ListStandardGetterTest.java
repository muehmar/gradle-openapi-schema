package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list.ListStandardGetter.listStandardGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.stringList;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
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
class ListStandardGetterTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("listMembers")
  @SnapshotName("listMembers")
  void generate_when_listMembers_then_matchSnapshot(JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        listStandardGetterGenerator(GetterGeneratorSettings.empty());

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> listMembers() {
    final TypeMappings fullTypeMappings =
        TypeMappings.ofClassTypeMappings(
            STRING_MAPPING_WITH_CONVERSION, LIST_MAPPING_WITH_CONVERSION);
    return Stream.of(
            stringList(),
            requiredListWithNullableItems(),
            requiredListWithNullableItems(fullTypeMappings)
                .withName(JavaName.fromString("requiredListWithNullableItemsFullMapping")))
        .map(Arguments::arguments);
  }
}
