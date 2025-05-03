package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonXml;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.*;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class JsonGetter {
  private JsonGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> jsonGetterGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(jacksonXmlProperty())
        .append(jacksonXmlElementWrapper())
        .append(getterMethod())
        .filter(Filters.<JavaPojoMember>isJacksonJson().or(isJacksonXml()));
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(JsonGetter::methodReturnType)
        .methodName(f -> f.getGetterName().append("Json"))
        .noArguments()
        .doesNotThrow()
        .content(methodContent())
        .build()
        .append(RefsGenerator.fieldRefs());
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
