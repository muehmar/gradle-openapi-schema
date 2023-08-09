package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer.ContainerGetter.containerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer.FactoryMethodGenerator.factoryMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;

public class OneOfContainerGenerator {
  private OneOfContainerGenerator() {}

  public static Generator<OneOfContainer, PojoSettings> oneOfContainerGenerator() {
    return ClassGenBuilder.<OneOfContainer, PojoSettings>create()
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

  private static Generator<OneOfContainer, PojoSettings> content() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen()
        .append(memberGenerator(), OneOfContainer::memberContent)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), OneOfContainer::constructorContent)
        .appendSingleBlankLine()
        .append(factoryMethodGenerator())
        .appendSingleBlankLine()
        .append(containerGetter())
        .appendSingleBlankLine()
        .append(equalsMethod(), OneOfContainer::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), OneOfContainer::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), OneOfContainer::getToStringContent);
  }
}
