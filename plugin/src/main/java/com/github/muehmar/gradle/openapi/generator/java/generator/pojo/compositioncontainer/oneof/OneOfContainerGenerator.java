package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.oneof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.ContainerGetter.oneOfContainerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.FactoryMethodGenerator.oneOfFromFactoryMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.hashCodeMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator.pojoConstructorGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;

public class OneOfContainerGenerator {
  private OneOfContainerGenerator() {}

  public static Generator<OneOfContainer, PojoSettings> oneOfContainerGenerator() {
    return ClassGenBuilder.<OneOfContainer, PojoSettings>create()
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

  private static Generator<OneOfContainer, PojoSettings> containerJavaDoc() {
    return JavaDocGenerator.javaDoc(
        (c, s) -> {
          final String pojoLinks =
              c.getComposition()
                  .getPojos()
                  .map(p -> String.format("{@link %s}", p.getClassName()))
                  .toPList()
                  .mkString(", ");
          return String.format(
              "This is a container for the one of composition of {@link %s}. It can hold exactly one instance of %s. "
                  + "Use the corresponding from-factory methods to create an instance for one of the objects. The"
                  + " resulting instance can be used in the builder of {@link %s}.",
              c.getPojoName(), pojoLinks, c.getPojoName());
        });
  }

  private static Generator<OneOfContainer, PojoSettings> content() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen()
        .append(memberGenerator(), OneOfContainer::memberContent)
        .appendSingleBlankLine()
        .append(pojoConstructorGenerator(), OneOfContainer::constructorContent)
        .appendSingleBlankLine()
        .append(oneOfFromFactoryMethods())
        .appendSingleBlankLine()
        .append(oneOfContainerGetter())
        .appendSingleBlankLine()
        .append(equalsMethod(), OneOfContainer::getEqualsContent)
        .appendSingleBlankLine()
        .append(hashCodeMethod(), OneOfContainer::getHashCodeContent)
        .appendSingleBlankLine()
        .append(toStringMethod(), OneOfContainer::getToStringContent);
  }
}
