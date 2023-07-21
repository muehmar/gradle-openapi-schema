package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator.validAnnotation;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class ComposedDtoGetterGenerator {
  private ComposedDtoGetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> composedDtoGetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(
            allOfDtoGetter(), p -> p.getAllOfComposition().map(JavaAllOfComposition::getPojos));
  }

  private static Generator<NonEmptyList<JavaObjectPojo>, PojoSettings> allOfDtoGetter() {
    return Generator.<NonEmptyList<JavaObjectPojo>, PojoSettings>emptyGen()
        .appendList(annotatedAllOfGetter(), l -> l, Generator.newLine());
  }

  private static Generator<JavaObjectPojo, PojoSettings> annotatedAllOfGetter() {
    return JacksonAnnotationGenerator.<JavaObjectPojo>jsonIgnore()
        .append(validAnnotation())
        .append(allOfGetter());
  }

  private static Generator<JavaObjectPojo, PojoSettings> allOfGetter() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(p -> p.getClassName().asString())
        .methodName(p -> String.format("get%s", p.getClassName()))
        .noArguments()
        .content(p -> String.format("return as%s();", p.getClassName()))
        .build();
  }
}
