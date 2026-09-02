package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.StandardGetter.standardGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.list;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberXml;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class StandardGetterTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("visibilities")
  @SnapshotName("requiredString")
  void generate_when_requiredString_then_matchSnapshot(Visibility visibility) {
    final Generator<JavaPojoMember, PojoSettings> generator = standardGetterGenerator(visibility);

    final Writer writer;
    writer =
        generator.generate(
            requiredString()
                .withMemberXml(
                    new JavaPojoMemberXml(
                        Optional.of("xml-name"), Optional.of(true), Optional.empty())),
            defaultTestSettings().withXmlSupport(XmlSupport.JACKSON_3),
            javaWriter());

    expect.scenario(visibility.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("visibilities")
  @SnapshotName("arrayWithXmlDefinitions")
  void generate_when_arrayWithXmlDefinitions_then_matchSnapshot(Visibility visibility) {
    final Generator<JavaPojoMember, PojoSettings> generator = standardGetterGenerator(visibility);

    final JavaPojoMember member =
        list(StringType.noFormat(), Necessity.REQUIRED, Nullability.NOT_NULLABLE)
            .withMemberXml(
                new JavaPojoMemberXml(
                    Optional.empty(),
                    Optional.empty(),
                    Optional.of(
                        new JavaPojoMemberXml.JavaArrayXml(
                            Optional.of("array-name"),
                            Optional.of(true),
                            Optional.of("item-name")))));

    final Writer writer;
    writer =
        generator.generate(
            member, defaultTestSettings().withXmlSupport(XmlSupport.JACKSON_3), javaWriter());

    expect.scenario(visibility.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mappedString")
  void generate_when_mappedString_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        standardGetterGenerator(Visibility.PUBLIC);

    final Writer writer;
    writer =
        generator.generate(
            requiredString(TypeMappings.ofClassTypeMappings(STRING_MAPPING_WITH_CONVERSION)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("visibilities")
  @SnapshotName("genericType")
  void generate_when_genericType_then_matchSnapshot(Visibility visibility) {
    final Generator<JavaPojoMember, PojoSettings> generator = standardGetterGenerator(visibility);

    final JavaPojoMember genericType =
        list(
            StringType.noFormat().withConstraints(Constraints.ofEmail()),
            Necessity.REQUIRED,
            Nullability.NOT_NULLABLE);

    final Writer writer;
    writer = generator.generate(genericType, defaultTestSettings(), javaWriter());

    expect.scenario(visibility.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  private static Stream<Arguments> visibilities() {
    return Stream.of(Visibility.values()).map(Arguments::arguments);
  }
}
