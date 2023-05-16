package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class BuildMethodGenerator {
  private BuildMethodGenerator() {}

  public static Generator<NormalBuilderGenerator.NormalBuilderContent, PojoSettings> generator() {
    return MethodGenBuilder.<NormalBuilderGenerator.NormalBuilderContent, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnTypeName(NormalBuilderGenerator.NormalBuilderContent::getClassName)
        .methodName("build")
        .noArguments()
        .content(buildMethodContent())
        .build();
  }

  private static Generator<NormalBuilderGenerator.NormalBuilderContent, PojoSettings>
      buildMethodContent() {
    return (pojo, settings, writer) ->
        writer.print(
            "return new %s(%s);",
            pojo.getClassName(),
            pojo.getMembers()
                .flatMap(JavaPojoMember::createFieldNames)
                .add(JavaAdditionalProperties.getPropertyName())
                .mkString(", "));
  }
}
