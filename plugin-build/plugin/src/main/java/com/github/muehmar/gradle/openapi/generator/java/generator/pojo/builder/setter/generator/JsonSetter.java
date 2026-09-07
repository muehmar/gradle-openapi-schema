package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.generator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonXml;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.*;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;

public class JsonSetter {
  private JsonSetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> jsonSetterGenerator() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(jsonProperty(), MemberAndNameScope::getMember)
        .append(jsonDeserializeForMember(), MemberAndNameScope::getMember)
        .append(jacksonXmlProperty(), MemberAndNameScope::getMember)
        .append(jacksonXmlElementWrapper(), MemberAndNameScope::getMember)
        .append(setterMethod())
        .filter(Filters.<MemberAndNameScope>isJacksonJson().or(isJacksonXml()));
  }

  private static Generator<MemberAndNameScope, PojoSettings> setterMethod() {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("Builder")
        .methodName((m, s) -> m.getMember().getJsonSetterName(s, m.getBuilderNameScope()))
        .singleArgument(
            m ->
                new MethodGen.Argument(
                    m.getMember().getJavaType().getParameterizedClassName().asString(),
                    m.getMember().getName().asString()))
        .doesNotThrow()
        .content(methodContent().contraMap(MemberAndNameScope::getMember))
        .build()
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember);
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("this.%s = %s;", m.getName(), m.getName()))
        .append(FlagAssignments.forStandardMemberSetter())
        .append(constant("return this;"));
  }
}
