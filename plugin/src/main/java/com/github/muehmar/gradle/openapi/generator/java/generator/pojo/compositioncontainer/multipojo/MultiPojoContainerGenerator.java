package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.multipojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.ContainerGetter.containerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.FactoryMethodGenerator.anyOfFromFactoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.MultiPojoContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;

public class MultiPojoContainerGenerator {
  private MultiPojoContainerGenerator() {}

  public static Generator<MultiPojoContainer, PojoSettings> multiPojoContainerGenerator() {
    return ClassGenBuilder.<MultiPojoContainer, PojoSettings>create()
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

  private static Generator<MultiPojoContainer, PojoSettings> containerJavaDoc() {
    return JavaDocGenerator.javaDoc(
        (c, s) -> {
          final String pojoLinks =
              c.getComposition()
                  .getPojos()
                  .map(p -> String.format("{@link %s}", p.getClassName()))
                  .toPList()
                  .mkString(", ");
          return String.format(
              "This is a container for the %s composition of {@link %s}. It can hold any instance of %s. "
                  + "Use the corresponding from-factory methods to create an instance for each of the objects "
                  + "and then merge all instances to one using {@link %s#merge}. The resulting instance can be "
                  + "used in the builder of {@link %s}.",
              c.getComposition().getType().getName().startLowerCase(),
              c.getPojoName(),
              pojoLinks,
              c.getContainerName(),
              c.getPojoName());
        });
  }

  private static Generator<MultiPojoContainer, PojoSettings> content() {
    return Generator.<MultiPojoContainer, PojoSettings>emptyGen()
        .append(memberGenerator(), MultiPojoContainer::memberContent)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), MultiPojoContainer::constructorContent)
        .appendSingleBlankLine()
        .append(anyOfFromFactoryMethods())
        .appendSingleBlankLine()
        .append(containerGetter(), MultiPojoContainer::getComposition)
        .appendSingleBlankLine()
        .append(MultiPojoWitherMethodsGenerator.multiPojoWitherMethodsGenerator())
        .appendSingleBlankLine()
        .append(MultiPojoMergeMethodGenerator.multiPojoMergeMethodGenerator())
        .appendSingleBlankLine()
        .append(equalsMethod(), MultiPojoContainer::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), MultiPojoContainer::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), MultiPojoContainer::getToStringContent);
  }
}
