package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class CommonGetterTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(GetterGenerator.GeneratorOption.class)
  @SnapshotName("rawGetterMethodDefaultSettings")
  void rawGetterMethod_when_defaultSettings_then_correctOutput(
      GetterGenerator.GeneratorOption option) {
    final Generator<JavaPojoMember, PojoSettings> generator = CommonGetter.rawGetterMethod(option);
    final Writer writer =
        generator.generate(optionalListWithNullableItems(), defaultTestSettings(), javaWriter());

    expect.scenario(option.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(GetterGenerator.GeneratorOption.class)
  @SnapshotName("rawGetterMethodCustomModifierAndSuffix")
  void rawGetterMethod_when_customModifierAndSuffix_then_correctOutput(
      GetterGenerator.GeneratorOption option) {
    final Generator<JavaPojoMember, PojoSettings> generator = CommonGetter.rawGetterMethod(option);

    final PojoSettings settings =
        defaultTestSettings()
            .withValidationMethods(
                TestPojoSettings.defaultValidationMethods()
                    .withModifier(JavaModifier.PUBLIC)
                    .withGetterSuffix("CustomSuffix"));
    final Writer writer =
        generator.generate(optionalListWithNullableItems(), settings, javaWriter());

    expect.scenario(option.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
