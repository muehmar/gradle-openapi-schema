package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversionRenderer.fromApiTypeConversion;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSetting;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSettings;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;

public class StandardSetter {
  private StandardSetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> setterGenerator(
      SetterGeneratorSetting... settings) {
    return setterGenerator(new SetterGeneratorSettings(PList.of(settings)));
  }

  public static Generator<MemberAndNameScope, PojoSettings> setterGenerator(
      SetterGeneratorSettings settings) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(javaDoc(), mas -> mas.getMember().getDescription())
        .append(setterMethod(settings));
  }

  private static Generator<MemberAndNameScope, PojoSettings> setterMethod(
      SetterGeneratorSettings settings) {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(SetterModifier.scopedModifiers())
        .noGenericTypes()
        .returnType("Builder")
        .methodName((mas, s) -> mas.getMember().prefixedMethodName(s.getBuilderMethodPrefix()))
        .singleArgument(
            mas ->
                new MethodGen.Argument(
                    argumentType(mas.getMember(), settings), mas.getMember().getName().asString()))
        .doesNotThrow()
        .content(methodContent(settings))
        .build()
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember)
        .append(settings.wrappingRefs(), MemberAndNameScope::getMember);
  }

  private static String argumentType(JavaPojoMember member, SetterGeneratorSettings settings) {
    return String.format(
        settings.typeFormat(),
        member.getJavaType().getWriteableParameterizedClassName().asString());
  }

  private static Generator<MemberAndNameScope, PojoSettings> methodContent(
      SetterGeneratorSettings settings) {
    if (settings.isTristateSetter()) {
      return tristateMethodContent(settings);
    } else if (settings.isOptionalSetter()) {
      return optionalMethodContent(settings);
    } else {
      return standardMethodContent(settings);
    }
  }

  private static Generator<MemberAndNameScope, PojoSettings> standardMethodContent(
      SetterGeneratorSettings settings) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(
            (mas, s, w) -> {
              final JavaPojoMember m = mas.getMember();
              final String expression =
                  m.getJavaType()
                      .getApiType()
                      .map(
                          apiType ->
                              FromApiTypeConversionRenderer.fromApiTypeConversion(
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

  private static Generator<MemberAndNameScope, PojoSettings> optionalMethodContent(
      SetterGeneratorSettings settings) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(
            (mas, s, w) -> {
              final JavaPojoMember m = mas.getMember();
              final String mapping = wrappedTypeMapping(m);
              return w.println("this.%s = %s%s.orElse(null);", m.getName(), m.getName(), mapping);
            })
        .append(settings.flagAssigment())
        .append(constant("return this;"));
  }

  private static Generator<MemberAndNameScope, PojoSettings> tristateMethodContent(
      SetterGeneratorSettings settings) {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(
            (mas, s, w) -> {
              final JavaPojoMember m = mas.getMember();
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
