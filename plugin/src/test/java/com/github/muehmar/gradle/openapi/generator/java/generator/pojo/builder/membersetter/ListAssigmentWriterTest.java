package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ListAssigmentWriterTest {
  private Expect expect;

  @Test
  @SnapshotName("allUnnecessary")
  void fullListAssigmentWriterBuilder_when_allUnnecessary_then_matchSnapshot() {
    final Writer writer =
        fullListAssigmentWriterBuilder()
            .member(TestJavaPojoMembers.optionalListWithNullableItems())
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
            .member(TestJavaPojoMembers.optionalListWithNullableItems())
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
            .member(TestJavaPojoMembers.optionalListWithNullableItems())
            .unwrapTristateList()
            .unmapListTypeNotNecessary()
            .unwrapListItemNotNecessary()
            .unmapListItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("customTypeMappings")
  void fullListAssigmentWriterBuilder_when_customTypeMappings_then_matchSnapshot() {
    final Writer writer =
        fullListAssigmentWriterBuilder()
            .member(TestJavaPojoMembers.optionalListWithNullableItems())
            .unwrapListNotNecessary()
            .unmapListType("l -> l.toList()")
            .unwrapListItemNotNecessary()
            .unmapListItemType("i -> i.toString()")
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
