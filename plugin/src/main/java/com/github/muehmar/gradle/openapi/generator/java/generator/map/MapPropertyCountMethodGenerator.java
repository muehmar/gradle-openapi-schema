package com.github.muehmar.gradle.openapi.generator.java.generator.map;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaMapPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class MapPropertyCountMethodGenerator {
  private MapPropertyCountMethodGenerator() {}

  private static final String JAVA_DOC = "Returns the number of present properties of this object.";

  public static Generator<JavaMapPojo, PojoSettings> propertyCountMethod() {
    final MethodGen<JavaMapPojo, PojoSettings> method =
        MethodGenBuilder.<JavaMapPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("getPropertyCount")
            .noArguments()
            .content(
                pojo -> String.format("return %s.size();", pojo.getMember().getNameAsIdentifier()))
            .build();
    return JavaDocGenerator.<JavaMapPojo, PojoSettings>ofJavaDocString(JAVA_DOC)
        .append(ValidationGenerator.minAnnotationForPropertyCount(), JavaMapPojo::getConstraints)
        .append(ValidationGenerator.maxAnnotationForPropertyCount(), JavaMapPojo::getConstraints)
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }
}
