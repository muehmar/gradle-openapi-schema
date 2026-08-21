package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.DtoSetterGenerator.dtoSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberBuilder.javaPojoMemberBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberXml;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.composition.DiscriminatorType;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumTypeBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

@SnapshotTest
class DtoSetterGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allOfPojo")
  void generator_when_allOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(
                sampleObjectPojo1(), JavaPojos.allNecessityAndNullabilityVariants()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojo")
  void generator_when_calledWithComposedPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("Opt1")
            .optionalSuffix("Opt2")
            .optionalNullableSuffix("Tristate")
            .build();
    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(
                sampleObjectPojo1(), JavaPojos.allNecessityAndNullabilityVariants()),
            defaultTestSettings().withGetterSuffixes(getterSuffixes),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojoWithDiscriminator")
  void generator_when_calledWithOneOfPojoWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());
    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(javaOneOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("oneOfPojoWithEnumDiscriminator")
  void generator_when_calledWithOneOfPojoWithEnumDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo oneOfPojo = JavaPojos.oneOfPojoWithEnumDiscriminator();

    final Writer writer = generator.generate(oneOfPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  /**
   * Regression test: the discriminator value emitted into the dto setter must have the same type as
   * the (builder) setter of the discriminator member.
   *
   * <p>The enum discriminator member of this oneOf composition is format-mapped to the custom class
   * {@code com.custom.CustomColor} WITH a type conversion, so the generated builder setter {@code
   * setColor} accepts a {@code CustomColor}. The member's full to-api conversion chain must
   * therefore be applied to the discriminator value, e.g. {@code
   * setColor(CustomColor.fromColor(Color.fromValue("yellow")));} — emitting the bare generated enum
   * expression did not compile against the custom-typed setter.
   */
  @Test
  @SnapshotName("oneOfPojoWithEnumDiscriminatorMappedToCustomType")
  void
      generator_when_enumDiscriminatorMemberMappedToCustomTypeWithConversion_then_discriminatorValueConvertedToApiType() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo oneOfPojo =
        oneOfPojoWithCustomTypedEnumDiscriminator(colorEnumMemberMappedToCustomColor());

    final Writer writer = generator.generate(oneOfPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  /**
   * Regression test: the classes used by the conversion of the discriminator value must be added as
   * refs, otherwise the generated code misses the import.
   *
   * <p>The conversion towards the api type is a static factory method of {@code
   * com.custom.ColorConverters}, a class which differs from the mapped type {@code
   * com.custom.CustomColor}. As the conversion of a discriminator member is rendered nowhere else
   * in the parent dto, the ref of the conversion class is only added by this generator.
   */
  @Test
  @SnapshotName("oneOfPojoWithEnumDiscriminatorMappedToCustomTypeWithSeparateConversionClass")
  void
      generator_when_enumDiscriminatorMemberMappedWithSeparateConversionClass_then_conversionClassAddedAsRef() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo oneOfPojo =
        oneOfPojoWithCustomTypedEnumDiscriminator(
            colorEnumMemberMappedToCustomColor(
                new TypeConversion(
                    "com.custom.ColorConverters#toColor", "com.custom.ColorConverters#fromColor")));

    final Writer writer = generator.generate(oneOfPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  private static JavaPojoMember colorEnumMemberMappedToCustomColor() {
    return colorEnumMemberMappedToCustomColor(
        new TypeConversion("com.custom.CustomColor#toColor", "com.custom.CustomColor#fromColor"));
  }

  /**
   * A required enum member 'color' whose enum type (format 'Color') is format-mapped to {@code
   * com.custom.CustomColor} WITH the given type conversion (analogous to {@code
   * ClassTypeMappings#STRING_MAPPING_WITH_CONVERSION}).
   */
  private static JavaPojoMember colorEnumMemberMappedToCustomColor(TypeConversion typeConversion) {
    final TypeMappings typeMappings =
        TypeMappings.ofSingleFormatTypeMapping(
            new FormatTypeMapping("Color", "com.custom.CustomColor", Optional.of(typeConversion)));
    return javaPojoMemberBuilder()
        .pojoName(JavaPojoNames.invoiceName())
        .name(JavaName.fromString("color"))
        .description("Color")
        .javaType(JavaEnumType.wrap(colorEnumType(), typeMappings))
        .necessity(REQUIRED)
        .type(OBJECT_MEMBER)
        .memberXml(JavaPojoMemberXml.noDefinition())
        .build();
  }

  private static EnumType colorEnumType() {
    return EnumTypeBuilder.createFull()
        .name(Name.ofString("Color"))
        .members(PList.of("yellow", "orange", "red"))
        .nullability(NOT_NULLABLE)
        .legacyNullability(NOT_NULLABLE)
        .format("Color")
        .build();
  }

  /**
   * Same structure as {@link JavaPojos#oneOfPojoWithEnumDiscriminator()} but with the given
   * (custom-type-mapped) member as enum discriminator member.
   */
  private static JavaObjectPojo oneOfPojoWithCustomTypedEnumDiscriminator(
      JavaPojoMember colorMember) {
    final Map<String, Name> mapping = new HashMap<>();
    mapping.put("yellow", Name.ofString("Yellow"));
    mapping.put("orange", Name.ofString("Orange"));
    final UntypedDiscriminator untypedDiscriminator =
        UntypedDiscriminator.fromPropertyName(colorMember.getName().getOriginalName())
            .withMapping(Optional.of(mapping));

    final JavaObjectPojo basePojo =
        JavaPojos.objectPojo(colorMember).withName(JavaPojoNames.fromNameAndSuffix("Base", "Dto"));
    final Optional<JavaAllOfComposition> allOfComposition =
        Optional.of(JavaAllOfComposition.fromPojos(NonEmptyList.of(basePojo)));

    final JavaEnumType javaEnumType = (JavaEnumType) colorMember.getJavaType();
    final EnumType discriminatorEnumType =
        EnumType.ofNameAndMembers(
            javaEnumType.getEnumClassName().getClassName(),
            javaEnumType.getMembers().map(EnumConstantName::getOriginalConstant));

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(
                JavaPojos.objectPojo(TestJavaPojoMembers.requiredString())
                    .withAllOfComposition(allOfComposition)
                    .withSchemaName(SchemaName.ofString("Yellow"))
                    .withName(JavaPojoNames.fromNameAndSuffix("Yellow", "Dto")),
                JavaPojos.objectPojo(TestJavaPojoMembers.requiredBirthdate())
                    .withAllOfComposition(allOfComposition)
                    .withSchemaName(SchemaName.ofString("Orange"))
                    .withName(JavaPojoNames.fromNameAndSuffix("Orange", "Dto"))),
            untypedDiscriminator,
            DiscriminatorType.fromEnumType(discriminatorEnumType));

    return JavaPojos.oneOfPojo(javaOneOfComposition)
        .withName(JavaPojoNames.fromNameAndSuffix("OneOf", "Dto"));
  }

  @Test
  @SnapshotName("anyOfPojo")
  void generator_when_anyOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(
                sampleObjectPojo1(), JavaPojos.allNecessityAndNullabilityVariants()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojoWithDiscriminator")
  void generator_when_anyOfPojoWithDiscriminator_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());
    final JavaAnyOfComposition javaAnyOfComposition =
        JavaAnyOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);

    final Writer writer =
        generator.generate(
            JavaPojos.anyOfPojo(javaAnyOfComposition), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("noBuilderSetMethodPrefix")
  void generator_when_noBuilderSetMethodPrefix_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings().withBuilderMethodPrefix(""),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("composedPojoHasNoAdditionalPropertiesAllowed")
  void generator_when_composedPojoHasNoAdditionalPropertiesAllowed_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo samplePojo1 =
        sampleObjectPojo1().withAdditionalProperties(JavaAdditionalProperties.notAllowed());

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(samplePojo1, sampleObjectPojo2()),
            defaultTestSettings().withBuilderMethodPrefix(""),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("composedPojoHasNotNullableAdditionalProperties")
  void generator_when_composedPojoHasNotNullableAdditionalProperties_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaAdditionalProperties additionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType().withNullability(NOT_NULLABLE));
    final JavaObjectPojo samplePojo1 =
        sampleObjectPojo1().withAdditionalProperties(additionalProperties);

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(samplePojo1, sampleObjectPojo2()),
            defaultTestSettings().withBuilderMethodPrefix(""),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nestedOneOfPojo")
  void generator_when_nestedOneOfPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    final JavaObjectPojo oneOfPojo =
        JavaPojos.oneOfPojo(JavaPojos.allNecessityAndNullabilityVariants(), sampleObjectPojo2());

    final Writer writer =
        generator.generate(
            JavaPojos.allOfPojo(sampleObjectPojo1(), oneOfPojo),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
