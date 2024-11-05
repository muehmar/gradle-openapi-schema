package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.standard;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion.fromApiTypeConversion;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;

public class OptionalSetter {
  private OptionalSetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalSetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(javaDoc(), JavaPojoMember::getDescription)
        .append(jsonIgnore())
        .append(setterMethod());
  }

  private static Generator<JavaPojoMember, PojoSettings> setterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(SetterModifier.modifiers())
        .noGenericTypes()
        .returnType("Builder")
        .methodName((m, s) -> m.prefixedMethodName(s.getBuilderMethodPrefix()))
        .singleArgument(
            member -> new MethodGen.Argument(argumentType(member), member.getName().asString()))
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(RefsGenerator.fieldRefs())
        .append(ref(JAVA_UTIL_OPTIONAL));
  }

  private static String argumentType(JavaPojoMember member) {
    return String.format(
        "Optional<%s>", member.getJavaType().getWriteableParameterizedClassName().asString());
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) -> {
              final String localVariableName = m.getName().append("_").asString();
              final String mapping =
                  m.getJavaType()
                      .getApiType()
                      .map(
                          apiType ->
                              fromApiTypeConversion(apiType, localVariableName, NO_NULL_CHECK))
                      .map(Writer::asString)
                      .map(
                          conversion ->
                              String.format(".map(%s -> %s)", localVariableName, conversion))
                      .orElse("");
              return w.println("this.%s = %s%s.orElse(null);", m.getName(), m.getName(), mapping);
            })
        .append(FlagAssignments.forWrappedMemberSetter())
        .append(constant("return this;"));
  }
}
