package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class FactoryMethodGenerator {
  private FactoryMethodGenerator() {}

  public static Generator<JavaArrayPojo, PojoSettings> factoryMethodGenerator() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen()
        .append(itemsFactoryMethod())
        .appendSingleBlankLine()
        .append(emptyFactoryMethod());
  }

  private static Generator<JavaArrayPojo, PojoSettings> itemsFactoryMethod() {
    return MethodGenBuilder.<JavaArrayPojo, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType(p -> p.getClassName().asString())
        .methodName("fromItems")
        .singleArgument(
            p ->
                argument(
                    p.getArrayPojoMember()
                        .getJavaType()
                        .getWriteableParameterizedClassName()
                        .asString(),
                    "items"))
        .doesNotThrow()
        .content(factoryMethodContent())
        .build()
        .append(RefsGenerator.fieldRefs(), JavaArrayPojo::getArrayPojoMember);
  }

  private static Generator<JavaArrayPojo, PojoSettings> factoryMethodContent() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.getClassName().asString()))
        .append(
            (p, s, w) ->
                w.append(
                    2,
                    fullListAssigmentWriterBuilder()
                        .member(p.getArrayPojoMember())
                        .expressionOnly()
                        .unwrapListNotNecessary()
                        .unmapListType(p.getJavaArrayType())
                        .unwrapListItemNotNecessary()
                        .unmapListItemType(p.getJavaArrayType())
                        .build()))
        .append(constant(");"));
  }

  private static Generator<JavaArrayPojo, PojoSettings> emptyFactoryMethod() {
    return MethodGenBuilder.<JavaArrayPojo, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType(p -> p.getClassName().asString())
        .methodName("empty")
        .noArguments()
        .doesNotThrow()
        .content(constant("return fromItems(Collections.emptyList());"))
        .build()
        .append(RefsGenerator.fieldRefs(), JavaArrayPojo::getArrayPojoMember)
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_COLLECTIONS))
        .filter(FactoryMethodGenerator::arrayPojoHasStandardListType);
  }

  private static boolean arrayPojoHasStandardListType(JavaArrayPojo arrayPojo) {
    return arrayPojo.getJavaArrayType().getQualifiedClassName().equals(QualifiedClassNames.LIST)
        && arrayPojo.getJavaArrayType().hasNoApiTypeDeep();
  }
}
