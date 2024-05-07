package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterGenerator.memberSetterMethods;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@SnapshotTest
class SetterGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @MethodSource("allNecessityAndNullabilityVariants")
  @SnapshotName("allNecessityAndNullabilityVariants")
  void memberSetterMethods_when_calledWithNullabilityAndNecessityVariants_then_matchSnapshot(
      JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator = memberSetterMethods();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect
        .scenario(member.getName().asString())
        .toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }

  public static Stream<Arguments> allNecessityAndNullabilityVariants() {
    return JavaPojos.allNecessityAndNullabilityVariants()
        .getMembers()
        .map(Arguments::of)
        .toStream();
  }

  @ParameterizedTest
  @MethodSource("allNecessityAndNullabilityVariantsTypeMapped")
  @SnapshotName("allNecessityAndNullabilityVariantsTypeMapped")
  void
      memberSetterMethods_when_calledWithNecessityAndNullabilityVariantsTypeMapped_then_matchSnapshot(
          JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator = memberSetterMethods();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect
        .scenario(member.getName().asString())
        .toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }

  public static Stream<Arguments> allNecessityAndNullabilityVariantsTypeMapped() {
    return JavaPojos.allNecessityAndNullabilityVariantsTypeMapped()
        .getMembers()
        .map(Arguments::of)
        .toStream();
  }

  @ParameterizedTest
  @MethodSource("allListNecessityAndNullabilityVariantsFullyTypeMapped")
  @SnapshotName("allListNecessityAndNullabilityVariantsFullyTypeMapped")
  void
      memberSetterMethods_when_allListNecessityAndNullabilityVariantsFullyTypeMapped_then_matchSnapshot(
          JavaPojoMember member) {
    final Generator<JavaPojoMember, PojoSettings> generator = memberSetterMethods();

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect
        .scenario(member.getName().asString())
        .toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }

  public static Stream<Arguments> allListNecessityAndNullabilityVariantsFullyTypeMapped() {
    return JavaPojos.allNecessityAndNullabilityVariantsTypeMapped(
            TypeMappings.ofClassTypeMappings(
                ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION,
                ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION))
        .getMembers()
        .filter(member -> member.getJavaType().isArrayType())
        .map(Arguments::of)
        .toStream();
  }
}
