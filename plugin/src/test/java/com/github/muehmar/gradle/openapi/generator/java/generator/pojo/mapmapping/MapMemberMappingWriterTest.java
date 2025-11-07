package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.MapMemberMappingWriterBuilder.fullMapMemberMappingWriterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalMap;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableMap;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredMap;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableMap;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.MAP_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class MapMemberMappingWriterTest {
  private Expect expect;

  private static final JavaPojoMember MEMBER =
      JavaPojoMember.wrap(
          PojoMembers.optionalMap(),
          invoiceName(),
          TypeMappings.ofClassTypeMappings(
              MAP_MAPPING_WITH_CONVERSION, STRING_MAPPING_WITH_CONVERSION));

  private static final JavaPojoMember NULLABLE_MEMBER =
      JavaPojoMember.wrap(
          PojoMembers.optionalNullableMapWithNullableValues(),
          invoiceName(),
          TypeMappings.ofClassTypeMappings(
              MAP_MAPPING_WITH_CONVERSION, STRING_MAPPING_WITH_CONVERSION));

  private static final JavaMapType JAVA_MAP_TYPE = MEMBER.getJavaType().onMapType().get();

  @Test
  @SnapshotName("allUnnecessary")
  void fullMapMemberMappingWriterBuilder_when_allUnnecessary_then_matchSnapshot() {
    final Writer writer =
        fullMapMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapMapItemTypeNotNecessary()
            .wrapMapItemNotNecessary()
            .mapMapTypeNotNecessary()
            .wrapMapNotNecessary()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("wrapOptionalMapAndMapItem")
  void fullMapMemberMappingWriterBuilder_when_wrapOptionalMapAndMapItem_then_matchSnapshot() {
    final Writer writer =
        fullMapMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapMapItemTypeNotNecessary()
            .wrapOptionalMapItem()
            .mapMapTypeNotNecessary()
            .wrapOptionalMap()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("wrapTristateMap")
  void fullMapMemberMappingWriterBuilder_when_wrapTristateMap_then_matchSnapshot() {
    final Writer writer =
        fullMapMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapMapItemTypeNotNecessary()
            .wrapMapItemNotNecessary()
            .mapMapTypeNotNecessary()
            .wrapTristateMap()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mapMapAndMapItemType")
  void fullMapMemberMappingWriterBuilder_when_mapMapAndMapItemType_then_matchSnapshot() {
    final Writer writer =
        fullMapMemberMappingWriterBuilder()
            .member(MEMBER)
            .noPrefix()
            .mapMapItemType(JAVA_MAP_TYPE)
            .wrapMapItemNotNecessary()
            .mapMapType(JAVA_MAP_TYPE)
            .wrapMapNotNecessary()
            .noTrailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("prefixAndTrailingSemicolon")
  void fullMapMemberMappingWriterBuilder_when_prefixAndTrailingSemicolon_then_matchSnapshot() {
    final Writer writer =
        fullMapMemberMappingWriterBuilder()
            .member(MEMBER)
            .prefix("prefix")
            .mapMapItemTypeNotNecessary()
            .wrapMapItemNotNecessary()
            .mapMapTypeNotNecessary()
            .wrapMapNotNecessary()
            .trailingSemicolon()
            .build();

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @SnapshotName("autoMapped")
  @MethodSource("mapVariants")
  void fullMapMemberMappingWriterBuilder_when_autoMapped_then_matchSnapshot(JavaPojoMember member) {
    final Writer writer =
        fullMapMemberMappingWriterBuilder()
            .member(member)
            .noPrefix()
            .autoMapMapItemType()
            .autoWrapMapItem()
            .autoMapMapType()
            .autoWrapMap()
            .noTrailingSemicolon()
            .build();

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> mapVariants() {
    final TypeMappings fullMapping =
        TypeMappings.ofClassTypeMappings(
            STRING_MAPPING_WITH_CONVERSION, MAP_MAPPING_WITH_CONVERSION);
    return Stream.of(
            requiredMap(),
            requiredNullableMap(),
            optionalMap(),
            optionalNullableMap(),
            requiredMap(fullMapping)
                .withName(JavaName.fromName(Name.ofString("requiredMapWithFullMapping"))),
            requiredNullableMap(fullMapping)
                .withName(JavaName.fromName(Name.ofString("requiredNullableMapWithFullMapping"))),
            optionalMap(fullMapping)
                .withName(JavaName.fromName(Name.ofString("optionalMapWithFullMapping"))),
            optionalNullableMap(fullMapping)
                .withName(JavaName.fromName(Name.ofString("optionalNullableMapWithFullMapping"))))
        .map(Arguments::arguments);
  }
}
