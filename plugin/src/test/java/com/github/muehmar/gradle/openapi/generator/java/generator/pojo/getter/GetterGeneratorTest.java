package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.getterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalNullableStringList;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalStringList;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredNullableStringList;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredStringList;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
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
    final TypeMappings fullConversion =
        TypeMappings.ofClassTypeMappings(
            ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION,
            ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION);
    final TypeMappings listConversion =
        TypeMappings.ofClassTypeMappings(ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION);
    final TypeMappings stringConversion =
        TypeMappings.ofClassTypeMappings(ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION);
    final PList<JavaPojoMember> members =
        allNecessityAndNullabilityVariants()
            .concat(
                allNecessityAndNullabilityVariants(fullConversion)
                    .map(member -> member.withName(member.getName().append("-FullyMapped"))))
            .concat(
                allNecessityAndNullabilityVariants(stringConversion)
                    .map(member -> member.withName(member.getName().append("-StringMapped"))))
            .concat(
                allNecessityAndNullabilityVariants(listConversion)
                    .map(member -> member.withName(member.getName().append("-ListMapped"))))
            .add(requiredStringList())
            .add(requiredNullableStringList())
            .add(optionalStringList())
            .add(optionalNullableStringList());

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
