package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer;

import static io.github.muehmar.codegenerator.Generator.newLine;

import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
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
        .returnType(member -> member.getJavaType().getParameterizedClassName())
        .methodName(
            member ->
                String.format("get%s", Name.ofString(member.getName().asString()).startUpperCase()))
        .noArguments()
        .doesNotThrow()
        .content(member -> String.format("return %s;", member.getName()))
        .build();
  }
}
