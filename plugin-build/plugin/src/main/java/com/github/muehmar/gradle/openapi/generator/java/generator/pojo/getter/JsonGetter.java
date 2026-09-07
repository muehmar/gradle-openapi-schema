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
        .content(methodContent())
        .build()
        .append(RefsGenerator.fieldRefs(), MemberAndNameScope::getMember);
  }

  private static String methodReturnType(JavaPojoMember member) {
    return member.isNullable()
        ? "Object"
        : member.getJavaType().getParameterizedClassName().asString();
  }

  private static Generator<MemberAndNameScope, PojoSettings> methodContent() {
    return notNullableMethodContent()
        .append(requiredNullableMethodContent())
        .append(optionalNullableMethodContent());
  }

  private static Generator<MemberAndNameScope, PojoSettings> notNullableMethodContent() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append((mas, s, w) -> w.println("return %s;", mas.getMember().getName()))
        .filter(mas -> mas.getMember().isNotNullable());
  }

  private static Generator<MemberAndNameScope, PojoSettings> requiredNullableMethodContent() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(
            (mas, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : null;",
                    mas.getIsPresentFlagName(), mas.getMember().getName()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(mas -> mas.getMember().isRequiredAndNullable());
  }

  private static Generator<MemberAndNameScope, PojoSettings> optionalNullableMethodContent() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .append(
            (mas, s, w) ->
                w.println(
                    "return %s ? new JacksonNullContainer<>(%s) : %s;",
                    mas.getIsNullFlagName(), mas.getMember().getName(), mas.getMember().getName()))
        .append(ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(mas -> mas.getMember().isOptionalAndNullable());
  }
}
