package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.PojoPropertyCountMethod.pojoPropertyCountMethoGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.NormalBuilderGenerator.normalBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ConversionMethodGenerator.conversionMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldMethodGenerator.foldMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldValidationGenerator.foldValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.InvalidCompositionDtoGetterGenerator.invalidCompositionDtoGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.OneOfAnyOfDtoGetterGenerator.oneOfAnyOfDtoGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ValidCountMethodGenerator.validCountMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ValidCountValidationMethod.validCountValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.ValidationMethodGenerator.validationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.AllOfDtoGetterGenerator.allOfDtoGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.getterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.RequiredAdditionalPropertiesGetter.requiredAdditionalPropertiesGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.AdditionalPropertiesGetter.additionalPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.ListMappingMethods.unmapListMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.map.MapFactoryMethodeGenerator.mapFactoryMethodeGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.MapMappingMethods.mapMappingMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.AdditionalPropertiesTypeValidationGenerator.additionalPropertiesTypeValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.NoAdditionalPropertiesValidationMethodGenerator.noAdditionalPropertiesValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.validator.ValidatorClassGenerator.validationClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither.WitherGenerator.witherGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.IsValidMethodGenerator.isValidMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.UniqueItemsValidationMethodGenerator.uniqueItemsValidationMethodGenerator;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
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
            .annotations(
                PList.of(
                    JacksonAnnotationGenerator.jsonDeserialize(),
                    JacksonAnnotationGenerator.jacksonXmlRootElement()
                        .contraMap(JavaObjectPojo::getPojoXml)))
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
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), JavaObjectPojo::getConstructorContent)
        .appendSingleBlankLine()
        .append(innerEnums())
        .appendSingleBlankLine()
        .append(mapFactoryMethodeGenerator())
        .appendSingleBlankLine()
        .append(getters())
        .appendSingleBlankLine()
        .append(witherGenerator(), JavaObjectPojo::getWitherContent)
        .appendSingleBlankLine()
        .append(unmapListMethods())
        .appendSingleBlankLine()
        .append(mapMappingMethods())
        .appendSingleBlankLine()
        .append(foldMethodGenerator())
        .appendSingleBlankLine()
        .append(conversionMethodGenerator())
        .appendSingleBlankLine()
        .append(customValidationMethods())
        .appendSingleBlankLine()
        .append(equalsHashCodeToString())
        .appendSingleBlankLine()
        .append(builders());
  }

  private static Generator<JavaObjectPojo, PojoSettings> getters() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(getterGenerator(), JavaObjectPojo::getAllMembers, newLine())
        .appendSingleBlankLine()
        .append(allOfDtoGetterGenerator())
        .appendSingleBlankLine()
        .append(requiredAdditionalPropertiesGetter())
        .appendSingleBlankLine()
        .append(additionalPropertiesGetterGenerator())
        .appendSingleBlankLine()
        .append(oneOfAnyOfDtoGetterGenerator())
        .appendSingleBlankLine()
        .append(pojoPropertyCountMethoGenerator());
  }

  private static Generator<JavaObjectPojo, PojoSettings> innerEnums() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            EnumGenerator.nested(),
            pojo -> pojo.getMembers().flatMapOptional(JavaPojoMember::asEnumContent),
            newLine())
        .appendSingleBlankLine()
        .appendOptional(
            EnumGenerator.nested(), pojo -> pojo.getAdditionalProperties().asEnumContent());
  }

  private static Generator<JavaObjectPojo, PojoSettings> customValidationMethods() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(invalidCompositionDtoGetterGenerator())
        .appendSingleBlankLine()
        .append(validationMethodGenerator())
        .appendSingleBlankLine()
        .append(validCountMethodGenerator())
        .appendSingleBlankLine()
        .append(validCountValidationMethodGenerator())
        .appendSingleBlankLine()
        .append(foldValidationGenerator())
        .appendSingleBlankLine()
        .append(discriminatorValidationMethodGenerator())
        .appendSingleBlankLine()
        .append(isValidMethodGenerator())
        .appendSingleBlankLine()
        .append(validationClassGenerator())
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
            JavaObjectPojo::getMembers);
  }

  private static Generator<JavaObjectPojo, PojoSettings> equalsHashCodeToString() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(equalsMethod(), JavaObjectPojo::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), JavaObjectPojo::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), JavaObjectPojo::getToStringContent);
  }

  private static Generator<JavaObjectPojo, PojoSettings> builders() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(normalBuilderGenerator())
        .appendSingleBlankLine()
        .append(new StagedBuilderGenerator());
  }
}
