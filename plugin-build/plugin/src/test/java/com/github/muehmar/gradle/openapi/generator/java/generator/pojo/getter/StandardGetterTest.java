package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.StandardGetter.standardGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JAVA_DOC;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_JSON;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSetting.PACKAGE_PRIVATE;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings.getterGeneratorSettings;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.list;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
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
  @MethodSource("generatorSettings")
  @SnapshotName("requiredString")
  void generate_when_requiredString_then_matchSnapshot(GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        standardGetterGenerator(generatorSettings);

    final Writer writer;
    writer =
        generator.generate(
            requiredString()
                .withMemberXml(
                    new JavaPojoMemberXml(
                        Optional.of("xml-name"), Optional.of(true), Optional.empty())),
            defaultTestSettings().withXmlSupport(XmlSupport.JACKSON_3),
            javaWriter());

    expect
        .scenario(generatorSettings.getSettings().mkString("|"))
        .toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("generatorSettings")
  @SnapshotName("arrayWithXmlDefinitions")
  void generate_when_arrayWithXmlDefinitions_then_matchSnapshot(
      GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        standardGetterGenerator(generatorSettings);

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

    expect
        .scenario(generatorSettings.getSettings().mkString("|"))
        .toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mappedString")
  void generate_when_mappedString_then_matchSnapshot() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        standardGetterGenerator(getterGeneratorSettings(NO_VALIDATION, NO_JSON));

    final Writer writer;
    writer =
        generator.generate(
            requiredString(TypeMappings.ofClassTypeMappings(STRING_MAPPING_WITH_CONVERSION)),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("generatorSettings")
  @SnapshotName("genericType")
  void generate_when_genericType_then_matchSnapshot(GetterGeneratorSettings generatorSettings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        standardGetterGenerator(generatorSettings);

    final JavaPojoMember genericType =
        list(
            StringType.noFormat().withConstraints(Constraints.ofEmail()),
            Necessity.REQUIRED,
            Nullability.NOT_NULLABLE);

    final Writer writer;
    writer = generator.generate(genericType, defaultTestSettings(), javaWriter());

    expect
        .scenario(generatorSettings.getSettings().mkString("|"))
        .toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> generatorSettings() {
    return Stream.of(
        arguments(GetterGeneratorSettings.empty()),
        arguments(new GetterGeneratorSettings(PList.single(NO_VALIDATION))),
        arguments(new GetterGeneratorSettings(PList.single(NO_JSON))),
        arguments(new GetterGeneratorSettings(PList.single(NO_JAVA_DOC))),
        arguments(new GetterGeneratorSettings(PList.single(PACKAGE_PRIVATE))));
  }
}
