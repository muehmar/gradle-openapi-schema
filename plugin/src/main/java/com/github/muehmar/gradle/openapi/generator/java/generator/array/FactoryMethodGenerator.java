package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class FactoryMethodGenerator {
  private FactoryMethodGenerator() {}

  public static Generator<JavaArrayPojo, PojoSettings> factoryMethodGenerator() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen().append(itemsFactoryMethod());
  }

  private static Generator<JavaArrayPojo, PojoSettings> itemsFactoryMethod() {
    return MethodGenBuilder.<JavaArrayPojo, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType(p -> p.getClassName().asString())
        .methodName("fromItems")
        .singleArgument(
            p ->
                argument(p.getArrayPojoMember().getJavaType().getParameterizedClassName(), "items"))
        .doesNotThrow()
        .content(p -> String.format("return new %s(items);", p.getClassName()))
        .build()
        .append(RefsGenerator.fieldRefs(), JavaArrayPojo::getArrayPojoMember);
  }
}
