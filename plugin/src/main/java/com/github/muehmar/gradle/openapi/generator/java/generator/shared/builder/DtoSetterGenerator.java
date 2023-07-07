package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.fieldRefs;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class DtoSetterGenerator {
  private DtoSetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> dtoSetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(
            dtoSetters(), pojo -> pojo.getAllOfComposition().map(JavaAllOfComposition::getPojos))
        .appendSingleBlankLine()
        .appendOptional(
            dtoSetters(), pojo -> pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos))
        .appendSingleBlankLine()
        .appendOptional(
            dtoSetters(), pojo -> pojo.getAnyOfComposition().map(JavaAnyOfComposition::getPojos));
  }

  private static Generator<NonEmptyList<JavaObjectPojo>, PojoSettings> dtoSetters() {
    return Generator.<NonEmptyList<JavaObjectPojo>, PojoSettings>emptyGen()
        .appendList(singleDtoSetter(), p -> p, newLine());
  }

  private static Generator<JavaObjectPojo, PojoSettings> singleDtoSetter() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("Builder")
        .methodName(
            (pojo, settings) ->
                pojo.prefixedClassNameForMethod(settings.getBuilderMethodPrefix()).asString())
        .singleArgument(pojo -> String.format("%s dto", pojo.getClassName()))
        .content(dtoSetterContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> dtoSetterContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(setSingleMember(), JavaObjectPojo::getAllMembers)
        .append(setAdditionalProperties())
        .append(constant("return this;"));
  }

  private static Generator<JavaPojoMember, PojoSettings> setSingleMember() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (member, s, w) ->
                w.println(
                    "%s(dto.%s());",
                    member.prefixedMethodName(s.getBuilderMethodPrefix()),
                    member.getGetterNameWithSuffix(s)))
        .append(fieldRefs());
  }

  private static <A, B> Generator<A, B> setAdditionalProperties() {
    return Generator.constant(
        "dto.getAdditionalProperties().forEach((key, value) -> addAdditionalProperty(key, value));");
  }
}
