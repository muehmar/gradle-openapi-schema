package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonXml;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.*;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class JsonGetter {
  private JsonGetter() {}

  public static Generator<MemberAndNameScope, PojoSettings> jsonGetterGenerator() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(jsonProperty(), MemberAndNameScope::getMember)
        .append(jsonFormat(), MemberAndNameScope::getMember)
        .append(jsonIncludeNonNull(), MemberAndNameScope::getMember)
        .append(jacksonXmlProperty(), MemberAndNameScope::getMember)
        .append(jacksonXmlElementWrapper(), MemberAndNameScope::getMember)
        .append(getterMethod())
        .filter(Filters.<MemberAndNameScope>isJacksonJson().or(isJacksonXml()));
  }

  private static Generator<MemberAndNameScope, PojoSettings> getterMethod() {
    return JavaGenerators.<MemberAndNameScope, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(m -> methodReturnType(m.getMember()))
        .methodName(m -> m.getMember().getJsonGetterName(m.getDtoNameScope()))
        .noArguments()
        .doesNotThrow()
        .content(methodContent().contraMap(MemberAndNameScope::getMember))
        .build()
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember);
  }

  private static String methodReturnType(JavaPojoMember member) {
    return member.isNullable()
        ? "Object"
        : member.getJavaType().getParameterizedClassName().asString();
  }

  private static Generator<JavaPojoMember, PojoSettings> methodContent() {
    return notNullableMethodContent()
        .append(requiredNullableMethodContent())
        .append(optionalNullableMethodContent());
  }

  private static Generator<JavaPojoMember, PojoSettings> notNullableMethodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("return %s;", m.getName()))
        .filter(JavaPojoMember::isNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> requiredNullableMethodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : null;",
                    m.getIsPresentFlagName(), m.getName()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(JavaPojoMember::isRequiredAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> optionalNullableMethodContent() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : %s;",
                    m.getIsNullFlagName(), m.getName(), m.getName()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(JavaPojoMember::isOptionalAndNullable);
  }
}
