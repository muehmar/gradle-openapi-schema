package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
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
class ListAssigmentWriterTest {
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
  void fullListAssigmentWriterBuilder_when_allUnnecessary_then_matchSnapshot() {
    final Writer writer =
        fullListAssigmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapListNotNecessary()
            .unmapListTypeNotNecessary()
            .unwrapListItemNotNecessary()
            .unmapListItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("expressionOnly")
  void fullListAssigmentWriterBuilder_when_expressionOnly_then_matchSnapshot() {
    final Writer writer =
        fullListAssigmentWriterBuilder()
            .member(MEMBER)
            .expressionOnly()
            .unwrapListNotNecessary()
            .unmapListTypeNotNecessary()
            .unwrapListItemNotNecessary()
            .unmapListItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("unwrapOptionalListAndListItem")
  void fullListAssigmentWriterBuilder_when_unwrapOptionalListAndListItem_then_matchSnapshot() {
    final Writer writer =
        fullListAssigmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapOptionalList()
            .unmapListTypeNotNecessary()
            .unwrapOptionalListItem()
            .unmapListItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("unwrapTristateList")
  void fullListAssigmentWriterBuilder_when_unwrapTristateList_then_matchSnapshot() {
    final Writer writer =
        fullListAssigmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapTristateList()
            .unmapListTypeNotNecessary()
            .unwrapListItemNotNecessary()
            .unmapListItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("unmapListAndListItemType")
  void fullListAssigmentWriterBuilder_when_unmapListAndListItemType_then_matchSnapshot() {
    final Writer writer =
        fullListAssigmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapListNotNecessary()
            .unmapListType(JAVA_ARRAY_TYPE)
            .unwrapListItemNotNecessary()
            .unmapListItemType(JAVA_ARRAY_TYPE)
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
