package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.FactoryMethodGenerator.anyOFromFactoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;

public class AnyOfContainerGenerator {
  private AnyOfContainerGenerator() {}

  public static Generator<AnyOfContainer, PojoSettings> anyOfContainerGenerator() {
    return ClassGenBuilder.<AnyOfContainer, PojoSettings>create()
        .clazz()
        .topLevel()
        .packageGen(new PackageGenerator<>())
        .javaDoc(JavaDocGenerator.javaDoc((container, settings) -> "TODO"))
        .noAnnotations()
        .modifiers(PUBLIC)
        .className(container -> container.getContainerName().asString())
        .noSuperClass()
        .noInterfaces()
        .content(content())
        .build();
  }

  private static Generator<AnyOfContainer, PojoSettings> content() {
    return Generator.<AnyOfContainer, PojoSettings>emptyGen()
        .append(memberGenerator(), AnyOfContainer::memberContent)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), AnyOfContainer::constructorContent)
        .appendSingleBlankLine()
        .append(anyOFromFactoryMethods())
        .appendSingleBlankLine()
        .append(equalsMethod(), AnyOfContainer::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), AnyOfContainer::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), AnyOfContainer::getToStringContent);
  }
}
