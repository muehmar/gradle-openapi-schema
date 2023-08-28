package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.FactoryMethodGenerator.anyOfFromFactoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.anyof.AnyOfWitherMethodsGenerator.anyOfWitherMethodsGenerator;
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
        .javaDoc(containerJavaDoc())
        .noAnnotations()
        .modifiers(PUBLIC)
        .className(container -> container.getContainerName().asString())
        .noSuperClass()
        .noInterfaces()
        .content(content())
        .build();
  }

  private static Generator<AnyOfContainer, PojoSettings> containerJavaDoc() {
    return JavaDocGenerator.javaDoc(
        (c, s) -> {
          final String pojoLinks =
              c.getComposition()
                  .getPojos()
                  .map(p -> String.format("{@link %s}", p.getClassName()))
                  .toPList()
                  .mkString(", ");
          return String.format(
              "This is a container for the any of composition of {@link %s}. It can hold any instance of %s. "
                  + "Use the corresponding from-factory methods to create an instance for each of the objects "
                  + "and then merge all instances to one using {@link %s#merge}. The resulting instance can be "
                  + "used in the builder of {@link %s}.",
              c.getPojoName(), pojoLinks, c.getContainerName(), c.getPojoName());
        });
  }

  private static Generator<AnyOfContainer, PojoSettings> content() {
    return Generator.<AnyOfContainer, PojoSettings>emptyGen()
        .append(memberGenerator(), AnyOfContainer::memberContent)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), AnyOfContainer::constructorContent)
        .appendSingleBlankLine()
        .append(anyOfFromFactoryMethods())
        .appendSingleBlankLine()
        .append(anyOfWitherMethodsGenerator())
        .appendSingleBlankLine()
        .append(equalsMethod(), AnyOfContainer::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), AnyOfContainer::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), AnyOfContainer::getToStringContent);
  }
}
