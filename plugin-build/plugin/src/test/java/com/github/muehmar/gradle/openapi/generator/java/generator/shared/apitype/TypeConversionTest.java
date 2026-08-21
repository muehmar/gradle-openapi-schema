package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConstructorConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FactoryMethodConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.InstanceMethodConversion;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class TypeConversionTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("singleFactoryMethodConversion")
  void typeConversion_when_singleFactoryMethodConversion_then_correctOutputAndRefs(
      ConversionGenerationMode mode) {
    final Generator<PList<ConversionMethod>, Void> generator =
        TypeConversion.typeConversion("value", mode);

    final PList<ConversionMethod> methods =
        PList.single(
            ConversionMethod.ofFactoryMethod(
                new FactoryMethodConversion(
                    QualifiedClassName.ofQualifiedClassName("com.example.UserId"),
                    Name.ofString("of"))));

    final Writer writer = generator.generate(methods, noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("singleInstanceMethodConversion")
  void typeConversion_when_singleInstanceMethodConversion_then_correctOutputAndRefs(
      ConversionGenerationMode mode) {
    final Generator<PList<ConversionMethod>, Void> generator =
        TypeConversion.typeConversion("value", mode);

    final PList<ConversionMethod> methods =
        PList.single(
            ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString")));

    final Writer writer = generator.generate(methods, noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("singleConstructorConversion")
  void typeConversion_when_singleConstructorConversion_then_correctOutputAndRefs(
      ConversionGenerationMode mode) {
    final Generator<PList<ConversionMethod>, Void> generator =
        TypeConversion.typeConversion("value", mode);

    final PList<ConversionMethod> methods =
        PList.single(
            ConversionMethod.ofConstructor(
                new ConstructorConversion(
                    QualifiedClassName.ofQualifiedClassName("com.example.UserId"),
                    Optional.empty(),
                    false)));

    final Writer writer = generator.generate(methods, noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("constructorConversionWithGenericClass")
  void typeConversion_when_constructorConversionWithGenericClass_then_correctOutputAndRefs(
      ConversionGenerationMode mode) {
    final Generator<PList<ConversionMethod>, Void> generator =
        TypeConversion.typeConversion("value", mode);

    final PList<ConversionMethod> methods =
        PList.single(ConversionMethod.ofConstructor(ConstructorConversion.conversionForList()));

    final Writer writer = generator.generate(methods, noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("multipleConversions")
  void typeConversion_when_multipleConversions_then_correctOutputAndRefs(
      ConversionGenerationMode mode) {
    final Generator<PList<ConversionMethod>, Void> generator =
        TypeConversion.typeConversion("field", mode);

    final PList<ConversionMethod> methods =
        PList.of(
            ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("asString")),
            ConversionMethod.ofConstructor(ConstructorConversion.conversionForList()),
            ConversionMethod.ofFactoryMethod(
                new FactoryMethodConversion(
                    QualifiedClassName.ofQualifiedClassName("com.example.Result"),
                    Name.ofString("fromString"))));

    final Writer writer = generator.generate(methods, noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("emptyConversionMethods")
  void typeConversion_when_emptyConversionMethods_then_correctOutputAndRefs(
      ConversionGenerationMode mode) {
    final Generator<PList<ConversionMethod>, Void> generator =
        TypeConversion.typeConversion("value", mode);

    final PList<ConversionMethod> methods = PList.empty();

    final Writer writer = generator.generate(methods, noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(ConversionGenerationMode.class)
  @SnapshotName("nonImportableClassesUsed")
  void typeConversion_when_nonImportableClassesUsed_then_correctOutputAndRefs(
      ConversionGenerationMode mode) {
    final Generator<PList<ConversionMethod>, Void> generator =
        TypeConversion.typeConversion("value", mode);

    final PList<ConversionMethod> methods =
        PList.of(
            ConversionMethod.ofFactoryMethod(
                new FactoryMethodConversion(
                    QualifiedClassName.ofQualifiedClassName("java.lang.String"),
                    Name.ofString("fromOtherString"))),
            ConversionMethod.ofFactoryMethod(
                new FactoryMethodConversion(
                    QualifiedClassName.ofQualifiedClassName("SomeEnum"),
                    Name.ofString("fromString"))));

    final Writer writer = generator.generate(methods, noSettings(), javaWriter());

    expect.scenario(mode.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
