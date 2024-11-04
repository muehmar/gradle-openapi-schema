package com.github.muehmar.gradle.openapi.generator.java.generator.shared.map;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.map.MapAssignmentWriterBuilder.fullMapAssignmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class MapAssignmentWriterTest {
  private Expect expect;

  private static final JavaPojoMember MEMBER =
      JavaPojoMember.wrap(
          PojoMembers.optionalMap(),
          invoiceName(),
          TypeMappings.ofClassTypeMappings(
              ClassTypeMappings.MAP_MAPPING_WITH_CONVERSION,
              ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION));

  private static final JavaMapType JAVA_MAP_TYPE = MEMBER.getJavaType().onMapType().get();

  @Test
  @SnapshotName("allUnnecessary")
  void fullMapAssignmentWriterBuilder_when_allUnnecessary_then_matchSnapshot() {
    final Writer writer =
        fullMapAssignmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapMapNotNecessary()
            .unmapMapTypeNotNecessary()
            .unwrapMapItemNotNecessary()
            .unmapMapItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("expressionOnly")
  void fullMapAssigmentWriterBuilder_when_expressionOnly_then_matchSnapshot() {
    final Writer writer =
        fullMapAssignmentWriterBuilder()
            .member(MEMBER)
            .expressionOnly()
            .unwrapMapNotNecessary()
            .unmapMapTypeNotNecessary()
            .unwrapMapItemNotNecessary()
            .unmapMapItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("unwrapTristateMap")
  void fullMapAssigmentWriterBuilder_when_unwrapTristateMap_then_matchSnapshot() {
    final Writer writer =
        fullMapAssignmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapTristateMap()
            .unmapMapTypeNotNecessary()
            .unwrapMapItemNotNecessary()
            .unmapMapItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("unmapMapAndMapItemType")
  void fullMapAssigmentWriterBuilder_when_unmapMapAndMapItemType_then_matchSnapshot() {
    final Writer writer =
        fullMapAssignmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapMapNotNecessary()
            .unmapMapType(JAVA_MAP_TYPE)
            .unwrapMapItemNotNecessary()
            .unmapMapItemType(JAVA_MAP_TYPE)
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("autoMapping")
  void fullMapAssigmentWriterBuilder_when_autoMapping_then_matchSnapshot() {
    final Writer writer =
        fullMapAssignmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .autoUnwrapMap(MEMBER)
            .autoUnmapMapType(MEMBER)
            .autoUnwrapMapItem(MEMBER)
            .autoUnmapMapItemType(MEMBER)
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("onlyUnwrapOptionalMap")
  void fullMapAssigmentWriterBuilder_when_onlyUnwrapOptionalMap_then_matchSnapshot() {
    final Writer writer =
        fullMapAssignmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapOptionalMap()
            .unmapMapTypeNotNecessary()
            .unwrapMapItemNotNecessary()
            .unmapMapItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("onlyUnwrapTristateMap")
  void fullMapAssigmentWriterBuilder_when_onlyUnwrapTristateMap_then_matchSnapshot() {
    final Writer writer =
        fullMapAssignmentWriterBuilder()
            .member(MEMBER)
            .fieldAssigment()
            .unwrapTristateMap()
            .unmapMapTypeNotNecessary()
            .unwrapMapItemNotNecessary()
            .unmapMapItemTypeNotNecessary()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
