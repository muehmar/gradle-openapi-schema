package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory;

import static ch.bluecare.commons.data.PList.single;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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
class SinglePropertyFactoryMethodTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("members")
  @SnapshotName("members")
  void singlePropertyFactoryMethod_when_members_then_matchSnapshot(PList<JavaPojoMember> members) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        SinglePropertyFactoryMethod.singlePropertyFactoryMethod();

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

  @ParameterizedTest
  @MethodSource("noSinglePropertyObjectPojos")
  void singlePropertyFactoryMethod_when_noSingleMemberPojos_then_noOutput(JavaObjectPojo pojo) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        SinglePropertyFactoryMethod.singlePropertyFactoryMethod();

    final Writer writer =
        generator.generate(pojo, TestPojoSettings.defaultTestSettings(), Writer.javaWriter());

    assertEquals("", writer.asString());
  }

  public static Stream<Arguments> noSinglePropertyObjectPojos() {
    final JavaPojoMembers singleMember =
        JavaPojoMembers.fromMembers(single(TestJavaPojoMembers.requiredBirthdate()));
    return Stream.of(
            JavaPojos.anyOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2())
                .withMembers(singleMember),
            JavaPojos.oneOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2())
                .withMembers(singleMember),
            JavaPojos.allOfPojo(JavaPojos.sampleObjectPojo1(), JavaPojos.sampleObjectPojo2())
                .withMembers(singleMember),
            JavaPojos.sampleObjectPojo1(),
            JavaPojos.objectPojo(singleMember.asList())
                .withRequiredAdditionalProperties(
                    PList.single(
                        JavaRequiredAdditionalProperty.fromNameAndType(
                            Name.ofString("prop"), JavaTypes.stringType()))))
        .map(Arguments::of);
  }
}
