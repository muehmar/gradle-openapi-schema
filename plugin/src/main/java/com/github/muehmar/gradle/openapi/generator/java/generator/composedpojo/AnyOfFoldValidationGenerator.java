package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ANY_OF;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AnyOfFoldValidationGenerator {

  private AnyOfFoldValidationGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    final MethodGen<JavaComposedPojo, PojoSettings> method =
        MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .modifiers(PRIVATE)
            .noGenericTypes()
            .returnType("List<Object>")
            .methodName("getAnyOf")
            .noArguments()
            .content(methodContent())
            .build();

    return ValidationGenerator.<JavaComposedPojo>validAnnotation()
        .append(method)
        .filter(p -> p.getCompositionType().equals(ANY_OF));
  }

  private static Generator<JavaComposedPojo, PojoSettings> methodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(constant("if (getValidCount() == 0) {"))
        .append(constant("return null;"), 1)
        .append(constant("}"))
        .append(
            (p, s, w) ->
                w.println(
                    "return fold(%s);",
                    p.getJavaPojos()
                        .map(jp -> jp.getName().asString().toLowerCase())
                        .map(name -> String.format("%s -> %s", name, name))
                        .mkString(", ")));
  }
}
