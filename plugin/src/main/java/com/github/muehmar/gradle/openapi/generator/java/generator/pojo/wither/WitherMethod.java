package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Map;
import java.util.Optional;

abstract class WitherMethod {
  protected final WitherGenerator.WitherContent witherContent;
  protected final JavaPojoMember pojoMember;

  public static PList<WitherMethod> fromWitherContent(WitherGenerator.WitherContent witherContent) {
    return witherContent
        .getMembersForWithers()
        .flatMap(
            member ->
                PList.of(
                    new NormalWitherMethod(witherContent, member),
                    new OptionalWitherMethod(witherContent, member),
                    new TristateWitherMethod(witherContent, member),
                    new NullableItemsListNormalWitherMethod(witherContent, member),
                    new NullableItemsListOptionalWitherMethod(witherContent, member),
                    new NullableItemsListTristateWitherMethod(witherContent, member)))
        .filter(WitherMethod::shouldBeUsed);
  }

  WitherMethod(WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    this.witherContent = witherContent;
    this.pojoMember = pojoMember;
  }

  abstract boolean shouldBeUsed();

  public String javaDocString() {
    return pojoMember.getDescription();
  }

  String className() {
    return witherContent.getClassName().asString();
  }

  String witherName() {
    return pojoMember.getWitherName().asString();
  }

  PList<MethodGen.Argument> argument() {
    return PList.single(
        new MethodGen.Argument(
            String.format(argumentType(pojoMember.getJavaType().getParameterizedClassName())),
            pojoMember.getName().asString()));
  }

  abstract String argumentType(WriteableParameterizedClassName parameterizedClassName);

  Writer constructorCall() {
    final Writer newClassWriter =
        Writer.javaWriter().println("new %s(", witherContent.getClassName());

    final PList<String> members =
        witherContent
            .getTechnicalPojoMembers()
            .map(TechnicalPojoMember::getName)
            .map(
                name ->
                    Optional.ofNullable(propertyNameReplacementForConstructorCall().get(name))
                        .orElse(name.asString()));

    return members
        .zipWithIndex()
        .foldLeft(
            newClassWriter,
            (w, p) -> {
              final String memberValue = p.first();
              final int index = p.second();
              return w.tab(1).println("%s%s", memberValue, index == members.size() - 1 ? "" : ",");
            })
        .println(");");
  }

  abstract Map<JavaName, String> propertyNameReplacementForConstructorCall();

  abstract Writer addRefs(Writer writer);
}
