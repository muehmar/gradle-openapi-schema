package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.AdditionalPropertiesSetterGenerator.additionalPropertiesSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.BuildMethodGenerator.buildMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.DtoSetterGenerator.dtoSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.MemberDeclarationGenerator.memberDeclarationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.RequiredAdditionalPropertiesSetterGenerator.requiredAdditionalPropertiesSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterGenerator.setterGenerator;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGen;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.ConstructorGen;
import io.github.muehmar.codegenerator.java.ConstructorGenBuilder;

public class NormalBuilderGenerator {

  private NormalBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> normalBuilderGenerator() {
    final ClassGen<JavaObjectPojo, PojoSettings> classGen =
        ClassGenBuilder.<JavaObjectPojo, PojoSettings>create()
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
    return NormalBuilderGenerator.<JavaObjectPojo>factoryMethod()
        .append(JacksonAnnotationGenerator.jsonPojoBuilderWithPrefix("set"))
        .append(classGen);
  }

  private static <A> Generator<A, PojoSettings> factoryMethod() {
    return Generator.<A, PojoSettings>constant("public static Builder newBuilder() {")
        .append(Generator.constant("return new Builder();"), 1)
        .append(Generator.constant("}"))
        .appendNewLine()
        .filter((data, settings) -> settings.isDisableStagedBuilder());
  }

  private static Generator<JavaObjectPojo, PojoSettings> content() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendSingleBlankLine()
        .append(constructor())
        .append(memberDeclarationGenerator())
        .appendSingleBlankLine()
        .append(setterGenerator())
        .appendSingleBlankLine()
        .append(dtoSetterGenerator())
        .appendSingleBlankLine()
        .append(additionalPropertiesSetterGenerator())
        .appendSingleBlankLine()
        .append(requiredAdditionalPropertiesSetterGenerator())
        .appendSingleBlankLine()
        .append(buildMethodGenerator());
  }

  private static <A> Generator<A, PojoSettings> constructor() {
    final ConstructorGen<A, PojoSettings> constructor =
        ConstructorGenBuilder.<A, PojoSettings>create()
            .modifiers(PRIVATE)
            .className("Builder")
            .noArguments()
            .noContent()
            .build();
    return constructor.appendNewLine().filter((data, settings) -> settings.isEnableStagedBuilder());
  }
}
