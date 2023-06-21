package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.*;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.AdditionalPropertiesGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.UniqueItemsValidationMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator;
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
        .append(MemberGenerator.generator(), JavaObjectPojo::getMemberContent)
        .<JavaObjectPojo>contraMap(pojo -> pojo)
        .appendSingleBlankLine()
        .append(PojoConstructorGenerator.generator(), JavaObjectPojo::getConstructorContent)
        .appendSingleBlankLine()
        .appendList(
            EnumGenerator.nested(),
            pojo -> pojo.getMembers().flatMapOptional(JavaPojoMember::asEnumContent),
            newLine())
        .appendSingleBlankLine()
        .appendOptional(
            EnumGenerator.nested(), pojo -> pojo.getAdditionalProperties().asEnumContent())
        .appendSingleBlankLine()
        .appendList(GetterGenerator.generator(), JavaObjectPojo::getAllMembers, newLine())
        .appendSingleBlankLine()
        .append(AdditionalPropertiesGetter.getter())
        .appendSingleBlankLine()
        .append(WitherGenerator.generator(), JavaObjectPojo::getWitherContent)
        .appendSingleBlankLine()
        .append(PojoPropertyCountMethod.propertyCountMethod())
        .appendSingleBlankLine()
        .append(FoldMethodGenerator.generator())
        .appendSingleBlankLine()
        .append(ConversionMethodGenerator.composedAsDtoMethods())
        .appendSingleBlankLine()
        .append(ValidationMethodGenerator.isValidAgainstMethods())
        .appendSingleBlankLine()
        .append(ValidCountMethodGenerator.validCountMethod())
        .appendSingleBlankLine()
        .append(ValidCountValidationMethod.generator())
        .appendSingleBlankLine()
        .append(OneOfFoldValidationGenerator.generator())
        .appendSingleBlankLine()
        .append(AnyOfFoldValidationGenerator.generator())
        .appendSingleBlankLine()
        .append(EqualsGenerator.equalsMethod(), JavaObjectPojo::getEqualsContent)
        .appendSingleBlankLine()
        .append(HashCodeGenerator.hashCodeMethod(), JavaObjectPojo::getHashCodeContent)
        .appendSingleBlankLine()
        .append(ToStringGenerator.toStringMethod(), JavaObjectPojo::getToStringContent)
        .appendSingleBlankLine()
        .append(MultipleOfValidationMethodGenerator.generator())
        .appendSingleBlankLine()
        .append(
            NoAdditionalPropertiesValidationMethodGenerator.generator(),
            JavaObjectPojo::getAdditionalProperties)
        .appendSingleBlankLine()
        .appendList(
            UniqueItemsValidationMethodGenerator.generator().appendSingleBlankLine(),
            JavaObjectPojo::getMembers)
        .append(NormalBuilderGenerator.generator(), JavaObjectPojo::getNormalBuilderContent)
        .appendSingleBlankLine()
        .append(new SafeBuilderGenerator());
  }
}
