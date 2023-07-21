package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.AnyOfFoldValidationGenerator.anyOfFoldValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ConversionMethodGenerator.conversionMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.DiscriminatorValidationMethodGenerator.discriminatorValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.FoldMethodGenerator.foldMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.OneOfFoldValidationGenerator.oneOfFoldValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ValidCountMethodGenerator.validCountMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ValidCountValidationMethod.validCountValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ValidationMethodGenerator.validationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.map.MapFactoryMethodeGenerator.mapFactoryMethodeGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MultipleOfValidationMethodGenerator.multipleOfValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NoAdditionalPropertiesValidationMethodGenerator.noAdditionalPropertiesValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.PojoPropertyCountMethod.pojoPropertyCountMethoGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherGenerator.witherGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.AdditionalPropertiesGetter.additionalPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ComposedDtoGetterGenerator.composedDtoGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.getterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.UniqueItemsValidationMethodGenerator.uniqueItemsValidationMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderGenerator.normalBuilderGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator.toStringMethod;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SafeBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
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
        .append(additionalPropertiesGetterGenerator())
        .appendSingleBlankLine()
        .append(witherGenerator(), JavaObjectPojo::getWitherContent)
        .appendSingleBlankLine()
        .append(pojoPropertyCountMethoGenerator())
        .appendSingleBlankLine()
        .append(foldMethodGenerator())
        .appendSingleBlankLine()
        .append(conversionMethodGenerator())
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
        .appendList(
            uniqueItemsValidationMethodGenerator().appendSingleBlankLine(),
            JavaObjectPojo::getMembers)
        .append(normalBuilderGenerator())
        .appendSingleBlankLine()
        .append(new SafeBuilderGenerator());
  }
}
