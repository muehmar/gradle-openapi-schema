package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class BuildMethodGenerator {
  private BuildMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> buildMethodGenerator() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnTypeName(JavaObjectPojo::getClassName)
        .methodName("build")
        .noArguments()
        .content(buildMethodContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> buildMethodContent() {
    return (pojo, settings, writer) ->
        writer.print(
            "return new %s(%s);",
            pojo.getClassName(),
            pojo.getAllMembers()
                .flatMap(JavaPojoMember::createFieldNames)
                .add(JavaAdditionalProperties.getPropertyName())
                .mkString(", "));
  }
}
