package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer;

import static io.github.muehmar.codegenerator.Generator.newLine;

import com.github.muehmar.gradle.openapi.generator.java.model.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class ContainerGetter {

  private ContainerGetter() {}

  public static Generator<OneOfContainer, PojoSettings> oneOfContainerGetter() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen()
        .appendList(
            technicalGetter(),
            container -> container.getComposition().getPojosAsTechnicalMembers(),
            newLine());
  }

  public static Generator<AnyOfContainer, PojoSettings> anyOfContainerGetter() {
    return Generator.<AnyOfContainer, PojoSettings>emptyGen()
        .appendList(
            technicalGetter(),
            container -> container.getComposition().getPojosAsTechnicalMembers(),
            newLine());
  }

  private static Generator<TechnicalPojoMember, PojoSettings> technicalGetter() {
    return MethodGenBuilder.<TechnicalPojoMember, PojoSettings>create()
        .modifiers()
        .noGenericTypes()
        .returnType(member -> member.getJavaType().getFullClassName().asString())
        .methodName(
            member ->
                String.format("get%s", Name.ofString(member.getName().asString()).startUpperCase()))
        .noArguments()
        .content(member -> String.format("return %s;", member.getName()))
        .build();
  }
}
