package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion.fromApiTypeConversion;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSetting;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;

public class StandardSetter {
  private StandardSetter() {}

  public static Generator<JavaPojoMember, PojoSettings> setterGenerator(
      SetterGeneratorSetting... settings) {
    return setterGenerator(new SetterGeneratorSettings(PList.of(settings)));
  }

  public static Generator<JavaPojoMember, PojoSettings> setterGenerator(
      SetterGeneratorSettings settings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(javaDoc(), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(setterMethod(settings));
  }

  private static Generator<JavaPojoMember, PojoSettings> setterMethod(
      SetterGeneratorSettings settings) {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SetterModifier.modifiers())
        .noGenericTypes()
        .returnType("Builder")
        .methodName((m, s) -> m.prefixedMethodName(s.getBuilderMethodPrefix()))
        .singleArgument(
            member ->
                new MethodGen.Argument(argumentType(member, settings), member.getName().asString()))
        .doesNotThrow()
        .content(methodContent(settings))
        .build()
        .append(RefsGenerator.fieldRefs())
        .append(settings.wrappingRefs());
  }

  private static String argumentType(JavaPojoMember member, SetterGeneratorSettings settings) {
    return String.format(
        settings.typeFormat(),
        member.getJavaType().getWriteableParameterizedClassName().asString());
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent(
      SetterGeneratorSettings settings) {
    if (settings.isTristateSetter()) {
      return tristateMethodContent(settings);
    } else if (settings.isOptionalSetter()) {
      return optionalMethodContent(settings);
    } else {
      return standardMethodContent(settings);
    }
  }

  private static Generator<JavaPojoMember, PojoSettings> standardMethodContent(
      SetterGeneratorSettings settings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) -> {
              final String expression =
                  m.getJavaType()
                      .getApiType()
                      .map(
                          apiType ->
                              FromApiTypeConversion.fromApiTypeConversion(
                                  apiType,
                                  m.getName().asString(),
                                  ConversionGenerationMode.NULL_SAFE))
                      .map(Writer::asString)
                      .orElse(m.getName().asString());
              return w.println("this.%s = %s;", m.getName(), expression);
            })
        .append(settings.flagAssigment())
        .append(constant("return this;"));
  }

  private static Generator<JavaPojoMember, PojoSettings> optionalMethodContent(
      SetterGeneratorSettings settings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) -> {
              final String mapping = wrappedTypeMapping(m);
              return w.println("this.%s = %s%s.orElse(null);", m.getName(), m.getName(), mapping);
            })
        .append(settings.flagAssigment())
        .append(constant("return this;"));
  }

  private static Generator<JavaPojoMember, PojoSettings> tristateMethodContent(
      SetterGeneratorSettings settings) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) -> {
              final String mapping = wrappedTypeMapping(m);
              return w.println(
                  "this.%s = %s%s.%s;", m.getName(), m.getName(), mapping, m.tristateToProperty());
            })
        .append(settings.flagAssigment())
        .append(constant("return this;"));
  }

  private static String wrappedTypeMapping(JavaPojoMember member) {
    final String localVariableName = "val";
    return member
        .getJavaType()
        .getApiType()
        .map(apiType -> fromApiTypeConversion(apiType, localVariableName, NO_NULL_CHECK))
        .map(Writer::asString)
        .map(conversion -> String.format(".map(%s -> %s)", localVariableName, conversion))
        .orElse("");
  }
}
