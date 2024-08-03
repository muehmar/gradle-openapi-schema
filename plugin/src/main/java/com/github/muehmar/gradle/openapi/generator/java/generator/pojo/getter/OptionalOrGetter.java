package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversion.toApiTypeConversion;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class OptionalOrGetter {
  private OptionalOrGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalOrGetterGenerator(
      GetterGeneratorSettings generatorSettings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(generatorSettings.javaDocGenerator())
        .append(jsonIgnore())
        .append(getterMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(OptionalOrGetter::className)
        .methodName(member -> String.format("%sOr", member.getGetterName()))
        .singleArgument(member -> argument(className(member), "defaultValue"))
        .doesNotThrow()
        .content(getterContent())
        .build();
  }

  private static Generator<JavaPojoMember, PojoSettings> getterContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("return this.%s == null", m.getName()))
        .append(constant("? defaultValue"), 2)
        .append((m, s, w) -> w.println(": %s;", apiMapping(m)), 2);
  }

  private static String className(JavaPojoMember member) {
    return ParameterizedApiClassName.fromJavaType(member.getJavaType())
        .map(ParameterizedApiClassName::asString)
        .orElse(member.getJavaType().getParameterizedClassName().asString());
  }

  private static String apiMapping(JavaPojoMember member) {
    final String variable = String.format("this.%s", member.getName());
    return member
        .getJavaType()
        .getApiType()
        .map(apiType -> toApiTypeConversion(apiType, variable, NO_NULL_CHECK))
        .map(Writer::asString)
        .orElse(variable);
  }
}
