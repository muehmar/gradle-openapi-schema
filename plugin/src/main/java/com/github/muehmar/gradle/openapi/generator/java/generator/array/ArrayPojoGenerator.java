package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static com.github.muehmar.gradle.openapi.generator.java.generator.array.FactoryMethodGenerator.factoryMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.array.ValidatorClassGenerator.validationClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.getterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither.WitherGenerator.witherGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.IsValidMethodGenerator.isValidMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.UniqueItemsValidationMethodGenerator.uniqueItemsValidationMethodGenerator;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.writer.Writer;

public class ArrayPojoGenerator implements Generator<JavaArrayPojo, PojoSettings> {

  private final Generator<JavaArrayPojo, PojoSettings> delegate;

  public ArrayPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaArrayPojo, PojoSettings>create()
            .clazz()
            .declaration(TOP_LEVEL)
            .packageGen(new PackageGenerator<>())
            .javaDoc(JavaDocGenerator.javaDoc((pojo, settings) -> pojo.getDescription()))
            .noAnnotations()
            .modifiers(PUBLIC)
            .className(pojo -> pojo.getClassName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
  }

  @Override
  public Writer generate(JavaArrayPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaArrayPojo, PojoSettings> content() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen()
        .append(memberGenerator(), JavaArrayPojo::getMemberContent)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), JavaArrayPojo::getConstructorContent)
        .appendSingleBlankLine()
        .append(factoryMethodGenerator())
        .appendSingleBlankLine()
        .appendOptional(EnumGenerator.nested(), pojo -> pojo.getArrayPojoMember().asEnumContent())
        .appendSingleBlankLine()
        .append(getterGenerator(), JavaArrayPojo::getArrayPojoMember)
        .appendSingleBlankLine()
        .append(witherGenerator(), JavaArrayPojo::getWitherContent)
        .appendSingleBlankLine()
        .append(validationContent())
        .appendSingleBlankLine()
        .append(equalsHashCodeAndToString());
  }

  private static Generator<JavaArrayPojo, PojoSettings> validationContent() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen()
        .append(uniqueItemsValidationMethodGenerator(), JavaArrayPojo::getArrayPojoMember)
        .appendSingleBlankLine()
        .append(isValidMethodGenerator())
        .appendSingleBlankLine()
        .append(validationClassGenerator());
  }

  private static Generator<JavaArrayPojo, PojoSettings> equalsHashCodeAndToString() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen()
        .append(equalsMethod(), JavaArrayPojo::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), JavaArrayPojo::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), JavaArrayPojo::getToStringContent);
  }
}
