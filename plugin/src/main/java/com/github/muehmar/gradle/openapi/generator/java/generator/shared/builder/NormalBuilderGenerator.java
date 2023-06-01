package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGen;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.ConstructorGen;
import io.github.muehmar.codegenerator.java.ConstructorGenBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

public class NormalBuilderGenerator {

  private NormalBuilderGenerator() {}

  public static Generator<NormalBuilderContent, PojoSettings> generator() {
    final ClassGen<NormalBuilderContent, PojoSettings> classGen =
        ClassGenBuilder.<NormalBuilderContent, PojoSettings>create()
            .clazz()
            .nested()
            .packageGen(new PackageGenerator<>())
            .noJavaDoc()
            .noAnnotations()
            .modifiers(PUBLIC, STATIC, FINAL)
            .className("Builder")
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
    return NormalBuilderGenerator.<NormalBuilderContent>factoryMethod()
        .append(JacksonAnnotationGenerator.jsonPojoBuilderWithPrefix("set"))
        .append(classGen);
  }

  private static <A> Generator<A, PojoSettings> factoryMethod() {
    return Generator.<A, PojoSettings>constant("public static Builder newBuilder() {")
        .append(Generator.constant("return new Builder();"), 1)
        .append(Generator.constant("}"))
        .appendNewLine()
        .filter((data, settings) -> settings.isDisableSafeBuilder());
  }

  private static Generator<NormalBuilderContent, PojoSettings> content() {
    return Generator.<NormalBuilderContent, PojoSettings>emptyGen()
        .appendSingleBlankLine()
        .append(constructor())
        .append(MemberDeclarationGenerator.generator())
        .appendSingleBlankLine()
        .append(SetterGenerator.generator())
        .appendSingleBlankLine()
        .append(AdditionalPropertiesSetterGenerator.generator())
        .appendSingleBlankLine()
        .append(BuildMethodGenerator.generator());
  }

  private static <A> Generator<A, PojoSettings> constructor() {
    final ConstructorGen<A, PojoSettings> constructor =
        ConstructorGenBuilder.<A, PojoSettings>create()
            .modifiers(PRIVATE)
            .className("Builder")
            .noArguments()
            .noContent()
            .build();
    return constructor.appendNewLine().filter((data, settings) -> settings.isEnableSafeBuilder());
  }

  @Value
  @PojoBuilder(builderName = "NormalBuilderContentBuilder")
  public static class NormalBuilderContent {
    JavaIdentifier className;
    PList<JavaPojoMember> members;
    Optional<JavaAdditionalProperties> additionalProperties;
  }
}
