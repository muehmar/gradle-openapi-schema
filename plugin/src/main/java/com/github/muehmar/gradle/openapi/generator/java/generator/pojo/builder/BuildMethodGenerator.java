package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
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
        .returnType(JavaObjectPojo::getClassName)
        .methodName("build")
        .noArguments()
        .content(buildMethodContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> buildMethodContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            (m, s, w) ->
                w.println(
                    "%s.remove(\"%s\");",
                    additionalPropertiesName(), m.getName().getOriginalName()),
            JavaObjectPojo::getAllMembers)
        .append(
            Generator.<JavaObjectPojo, PojoSettings>newLine()
                .filter(p -> p.getAllMembers().nonEmpty()))
        .append(buildMethodCall());
  }

  private static Generator<JavaObjectPojo, PojoSettings> buildMethodCall() {
    return (pojo, settings, writer) ->
        writer.print(
            "return new %s(%s);",
            pojo.getClassName(),
            pojo.getTechnicalMembers().map(TechnicalPojoMember::getName).mkString(", "));
  }
}
