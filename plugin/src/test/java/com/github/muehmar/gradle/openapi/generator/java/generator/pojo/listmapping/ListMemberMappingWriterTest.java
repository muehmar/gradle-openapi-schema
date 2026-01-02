package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.ListMemberMappingWriterBuilder.fullListMemberMappingWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredStringList;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class ListMemberMappingWriterTest {
  private Expect expect;

  private static final JavaPojoMember MEMBER =
      JavaPojoMember.wrap(
          PojoMembers.optionalListWithNullableItems(),
          invoiceName(),
          TypeMappings.ofClassTypeMappings(
              TaskIdentifier.fromString("test"),
              LIST_MAPPING_WITH_CONVERSION,
              STRING_MAPPING_WITH_CONVERSION));

  private static final JavaArrayType JAVA_ARRAY_TYPE = MEMBER.getJavaType().onArrayType().get();

  @Test
  @SnapshotName("allUnnecessary")
  void fullListMemberMappingWriterBuilder_when_allUnnecessary_then_matchSnapshot() {
    final Writer writer =
        fullListMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapListItemTypeNotNecessary()
            .wrapListItemNotNecessary()
            .mapListTypeNotNecessary()
            .wrapListNotNecessary()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("wrapOptionalListAndListItem")
  void fullListMemberMappingWriterBuilder_when_wrapOptionalListAndListItem_then_matchSnapshot() {
    final Writer writer =
        fullListMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapListItemTypeNotNecessary()
            .wrapOptionalListItem()
            .mapListTypeNotNecessary()
            .wrapOptionalList()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("wrapTristateList")
  void fullListMemberMappingWriterBuilder_when_wrapTristateList_then_matchSnapshot() {
    final Writer writer =
        fullListMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapListItemTypeNotNecessary()
            .wrapListItemNotNecessary()
            .mapListTypeNotNecessary()
            .wrapTristateList()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mapListAndListItemType")
  void fullListMemberMappingWriterBuilder_when_mapListAndListItemType_then_matchSnapshot() {
    final Writer writer =
        fullListMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapListItemType(JAVA_ARRAY_TYPE)
            .wrapListItemNotNecessary()
            .mapListType(JAVA_ARRAY_TYPE)
            .wrapListNotNecessary()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("prefixAndTrailingSemicolon")
  void fullListMemberMappingWriterBuilder_when_prefixAndTrailingSemicolon_then_matchSnapshot() {
    final Writer writer =
        fullListMemberMappingWriterBuilder()
            .member(MEMBER)
            .prefix("return ")
            .mapListItemTypeNotNecessary()
            .wrapListItemNotNecessary()
            .mapListTypeNotNecessary()
            .wrapListNotNecessary()
            .trailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("listVariants")
  @SnapshotName("autoMapped")
  void fullListMemberMappingWriterBuilder_when_everythingAutoMapped_then_matchSnapshot(
      JavaPojoMember member) {
    final Writer writer =
        fullListMemberMappingWriterBuilder()
            .member(member)
            .prefix("")
            .autoMapListItemType()
            .autoWrapListItem()
            .autoMapListType()
            .autoWrapList()
            .trailingSemicolon()
            .build();

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> listVariants() {
    final TypeMappings stringTypeMapping =
        TypeMappings.ofSingleClassTypeMapping(
            STRING_MAPPING_WITH_CONVERSION, TaskIdentifier.fromString("test"));
    final TypeMappings listTypeMapping =
        TypeMappings.ofSingleClassTypeMapping(
            LIST_MAPPING_WITH_CONVERSION, TaskIdentifier.fromString("test"));
    final TypeMappings fullMapping =
        TypeMappings.ofClassTypeMappings(
            TaskIdentifier.fromString("test"),
            STRING_MAPPING_WITH_CONVERSION,
            LIST_MAPPING_WITH_CONVERSION);
    return Stream.of(
            requiredStringList(),
            requiredListWithNullableItems(),
            requiredNullableListWithNullableItems(),
            optionalListWithNullableItems(),
            optionalNullableListWithNullableItems(),
            requiredListWithNullableItems(stringTypeMapping)
                .withName(JavaName.fromString("requiredListWithNullableItemsStringMapping")),
            requiredListWithNullableItems(listTypeMapping)
                .withName(JavaName.fromString("requiredListWithNullableItemsListMapping")),
            requiredListWithNullableItems(fullMapping)
                .withName(JavaName.fromString("requiredListWithNullableItemsFullMapping")))
        .map(Arguments::arguments);
  }
}
