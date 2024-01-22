package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.singlepojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.ContainerGetter.containerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.FactoryMethodGenerator.oneOfFromFactoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.SinglePojoContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;

public class SinglePojoContainerGenerator {
  private SinglePojoContainerGenerator() {}

  public static Generator<SinglePojoContainer, PojoSettings> singlePojoContainerGenerator() {
    return ClassGenBuilder.<SinglePojoContainer, PojoSettings>create()
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

  private static Generator<SinglePojoContainer, PojoSettings> containerJavaDoc() {
    return JavaDocGenerator.javaDoc(
        (c, s) -> {
          final String pojoLinks =
              c.getComposition()
                  .getPojos()
                  .map(p -> String.format("{@link %s}", p.getClassName()))
                  .toPList()
                  .mkString(", ");
          return String.format(
              "This is a container for the %s composition of {@link %s}. It can hold exactly one instance of %s. "
                  + "Use the corresponding from-factory methods to create an instance for one of the objects. The"
                  + " resulting instance can be used in the builder of {@link %s}.",
              c.getComposition().getType().getName().startLowerCase(),
              c.getPojoName(),
              pojoLinks,
              c.getPojoName());
        });
  }

  private static Generator<SinglePojoContainer, PojoSettings> content() {
    return Generator.<SinglePojoContainer, PojoSettings>emptyGen()
        .append(memberGenerator(), SinglePojoContainer::memberContent)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), SinglePojoContainer::constructorContent)
        .appendSingleBlankLine()
        .append(oneOfFromFactoryMethods())
        .appendSingleBlankLine()
        .append(containerGetter(), SinglePojoContainer::getComposition)
        .appendSingleBlankLine()
        .append(equalsMethod(), SinglePojoContainer::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), SinglePojoContainer::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), SinglePojoContainer::getToStringContent);
  }
}
