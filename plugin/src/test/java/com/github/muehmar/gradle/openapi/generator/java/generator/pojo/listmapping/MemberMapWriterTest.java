package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.MemberMapWriterBuilder.fullMemberMapWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class MemberMapWriterTest {
  private Expect expect;

  private static final JavaPojoMember MEMBER =
      JavaPojoMember.wrap(
          PojoMembers.optionalListWithNullableItems(),
          invoiceName(),
          TypeMappings.ofClassTypeMappings(
              ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION,
              ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION));

  private static final JavaArrayType JAVA_ARRAY_TYPE = MEMBER.getJavaType().onArrayType().get();

  @Test
  @SnapshotName("allUnnecessary")
  void fullMemberMapWriterBuilder_when_allUnnecessary_then_matchSnapshot() {
    final Writer writer =
        fullMemberMapWriterBuilder()
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
  void fullMemberMapWriterBuilder_when_wrapOptionalListAndListItem_then_matchSnapshot() {
    final Writer writer =
        fullMemberMapWriterBuilder()
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
  void fullMemberMapWriterBuilder_when_wrapTristateList_then_matchSnapshot() {
    final Writer writer =
        fullMemberMapWriterBuilder()
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
  void fullMemberMapWriterBuilder_when_mapListAndListItemType_then_matchSnapshot() {
    final Writer writer =
        fullMemberMapWriterBuilder()
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
  void fullMemberMapWriterBuilder_when_prefixAndTrailingSemicolon_then_matchSnapshot() {
    final Writer writer =
        fullMemberMapWriterBuilder()
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
}
