package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.standard;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;

public class JsonSetter {
  private JsonSetter() {}

  public static Generator<JavaPojoMember, PojoSettings> jsonSetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonProperty())
        .append(setterMethod())
        .filter(isJacksonJson());
  }

  private static Generator<JavaPojoMember, PojoSettings> setterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("Builder")
        .methodName((m, s) -> m.prefixedMethodName(s.getBuilderMethodPrefix()).append("Json"))
        .singleArgument(
            member ->
                new MethodGen.Argument(
                    member.getJavaType().getParameterizedClassName().asString(),
                    member.getName().asString()))
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("this.%s = %s;", m.getName(), m.getName()))
        .append(FlagAssignments.forStandardMemberSetter())
        .append(constant("return this;"));
  }
}
