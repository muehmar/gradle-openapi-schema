package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AdditionalPropertiesTypeValidationGenerator.additionalPropertiesTypeValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NoAdditionalPropertiesValidationMethodGenerator.noAdditionalPropertiesValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.PojoPropertyCountMethod.pojoPropertyCountMethoGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherGenerator.witherGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.NormalBuilderGenerator.normalBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.AnyOfFoldValidationGenerator.anyOfFoldValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.BasicValidationMethodGenerator.basicValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.CompositionGetterGenerator.compositionGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ConversionMethodGenerator.conversionMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldMethodGenerator.foldMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.InvalidCompositionDtoGetterGenerator.invalidCompositionDtoGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.OneOfFoldValidationGenerator.oneOfFoldValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ValidCountMethodGenerator.validCountMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ValidCountValidationMethod.validCountValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ValidationMethodGenerator.validationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.AdditionalPropertiesGetter.additionalPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ComposedDtoGetterGenerator.composedDtoGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.getterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.RequiredAdditionalPropertiesGetter.requiredAdditionalPropertiesGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.map.MapFactoryMethodeGenerator.mapFactoryMethodeGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator.ValidatorClassGenerator.validationClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.UniqueItemsValidationMethodGenerator.uniqueItemsValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.writer.Writer;

public class ObjectPojoGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public ObjectPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .clazz()
            .declaration(TOP_LEVEL)
            .packageGen(new PackageGenerator<>())
            .javaDoc(JavaDocGenerator.javaDoc((pojo, settings) -> pojo.getDescription()))
            .singleAnnotation(JacksonAnnotationGenerator.jsonDeserialize())
            .modifiers(PUBLIC)
            .className(pojo -> pojo.getClassName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaObjectPojo, PojoSettings> content() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(memberGenerator(), JavaObjectPojo::getMemberContent)
        .<JavaObjectPojo>contraMap(pojo -> pojo)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), JavaObjectPojo::getConstructorContent)
        .appendSingleBlankLine()
        .appendList(
            EnumGenerator.nested(),
            pojo -> pojo.getMembers().flatMapOptional(JavaPojoMember::asEnumContent),
            newLine())
        .appendSingleBlankLine()
        .append(mapFactoryMethodeGenerator())
        .appendSingleBlankLine()
        .appendOptional(
            EnumGenerator.nested(), pojo -> pojo.getAdditionalProperties().asEnumContent())
        .appendSingleBlankLine()
        .appendList(getterGenerator(), JavaObjectPojo::getAllMembers, newLine())
        .appendSingleBlankLine()
        .append(composedDtoGetterGenerator())
        .appendSingleBlankLine()
        .append(requiredAdditionalPropertiesGetter())
        .appendSingleBlankLine()
        .append(additionalPropertiesGetterGenerator())
        .appendSingleBlankLine()
        .append(witherGenerator(), JavaObjectPojo::getWitherContent)
        .appendSingleBlankLine()
        .append(pojoPropertyCountMethoGenerator())
        .appendSingleBlankLine()
        .append(foldMethodGenerator())
        .appendSingleBlankLine()
        .append(compositionGetterGenerator())
        .appendSingleBlankLine()
        .append(conversionMethodGenerator())
        .appendSingleBlankLine()
        .append(invalidCompositionDtoGetterGenerator())
        .appendSingleBlankLine()
        .append(validationMethodGenerator())
        .appendSingleBlankLine()
        .append(validCountMethodGenerator())
        .appendSingleBlankLine()
        .append(validCountValidationMethodGenerator())
        .appendSingleBlankLine()
        .append(oneOfFoldValidationGenerator())
        .appendSingleBlankLine()
        .append(anyOfFoldValidationGenerator())
        .appendSingleBlankLine()
        .append(discriminatorValidationMethodGenerator())
        .appendSingleBlankLine()
        .append(basicValidationMethodGenerator())
        .appendSingleBlankLine()
        .append(validationClassGenerator())
        .appendSingleBlankLine()
        .appendSingleBlankLine()
        .append(equalsMethod(), JavaObjectPojo::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), JavaObjectPojo::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), JavaObjectPojo::getToStringContent)
        .appendSingleBlankLine()
        .append(multipleOfValidationMethodGenerator())
        .appendSingleBlankLine()
        .append(
            noAdditionalPropertiesValidationMethodGenerator(),
            JavaObjectPojo::getAdditionalProperties)
        .appendSingleBlankLine()
        .append(additionalPropertiesTypeValidationGenerator())
        .appendSingleBlankLine()
        .appendList(
            uniqueItemsValidationMethodGenerator().appendSingleBlankLine(),
            JavaObjectPojo::getMembers)
        .append(normalBuilderGenerator())
        .appendSingleBlankLine()
        .append(new SafeBuilderGenerator(SafeBuilderVariant.FULL))
        .appendSingleBlankLine()
        .append(new SafeBuilderGenerator(SafeBuilderVariant.STANDARD));
  }
}
