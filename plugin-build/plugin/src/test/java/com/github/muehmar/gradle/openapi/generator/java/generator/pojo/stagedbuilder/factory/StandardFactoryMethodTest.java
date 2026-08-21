package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory;

import static ch.bluecare.commons.data.PList.single;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class StandardFactoryMethodTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("members")
  @SnapshotName("membersAndStandardFactoryMethod")
  void factoryMethodsForVariant_when_membersAndStandardFactoryMethod_then_matchSnapshot(
      PList<JavaPojoMember> members) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        StandardFactoryMethod.factoryMethodsForVariant(StagedBuilderVariant.STANDARD);

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(members),
            TestPojoSettings.defaultTestSettings(),
            Writer.javaWriter());

    final String scenario = members.map(JavaPojoMember::getName).mkString(", ");

    expect.scenario(scenario).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @MethodSource("members")
  @SnapshotName("membersAndFullFactoryMethod")
  void factoryMethodsForVariant_when_membersAndFullFactoryMethod_then_matchSnapshot(
      PList<JavaPojoMember> members) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        StandardFactoryMethod.factoryMethodsForVariant(StagedBuilderVariant.FULL);

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(members),
            TestPojoSettings.defaultTestSettings(),
            Writer.javaWriter());

    final String scenario = members.map(JavaPojoMember::getName).mkString(", ");

    expect.scenario(scenario).toMatchSnapshot(writerSnapshot(writer));
  }

  static Stream<Arguments> members() {
    return Stream.of(
            single(TestJavaPojoMembers.requiredBirthdate()),
            single(TestJavaPojoMembers.optionalString()),
            single(TestJavaPojoMembers.optionalNullableString()),
            PList.of(TestJavaPojoMembers.requiredString(), TestJavaPojoMembers.optionalBirthdate()))
        .map(Arguments::of);
  }
}
