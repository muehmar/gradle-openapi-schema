package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.getterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class GetterGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("pojoMembers")
  @SnapshotName("pojoMembers")
  void generate_when_pojoMembers_then_matchSnapshot(JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator = getterGenerator();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(member.getName().asString()).toMatchSnapshot(writerSnapshot(writer));
  }

  public static Stream<Arguments> pojoMembers() {
    final PList<JavaPojoMember> members =
        JavaPojos.allNecessityAndNullabilityVariants()
            .getMembers()
            .add(TestJavaPojoMembers.requiredStringList());

    final PList<JavaPojoMember> anyOfMembers =
        members.map(
            member ->
                member
                    .withType(ANY_OF_MEMBER)
                    .withName(JavaName.fromString(member.getName().asString() + "AnyOf")));

    final PList<JavaPojoMember> allOfMembers =
        members.map(
            member ->
                member
                    .withType(ALL_OF_MEMBER)
                    .withName(JavaName.fromString(member.getName().asString() + "AllOf")));

    return members
        .concat(anyOfMembers)
        .concat(allOfMembers)
        .add(requiredListWithNullableItems())
        .add(requiredNullableListWithNullableItems())
        .add(optionalListWithNullableItems())
        .add(optionalNullableListWithNullableItems())
        .concat(JavaPojos.illegalIdentifierPojo().getMembers())
        .toStream()
        .map(Arguments::arguments);
  }
}
