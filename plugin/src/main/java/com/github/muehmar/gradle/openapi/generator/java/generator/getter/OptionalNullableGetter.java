package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonIncludeNonNull;
import static com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator.jsonProperty;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;
import static io.github.muehmar.pojoextension.generator.impl.gen.Generators.newLine;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGenBuilder;

public class OptionalNullableGetter {
  private static final Resolver RESOLVER = new JavaResolver();

  private OptionalNullableGetter() {}

  public static Generator<PojoMember, PojoSettings> getter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(jsonIgnore())
        .append(tristateGetterMethod())
        .appendConditionally(isJacksonJson(), newLine())
        .append(jsonProperty())
        .append(jsonIncludeNonNull())
        .append(jacksonSerializerMethod())
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<PojoMember, PojoSettings> tristateGetterMethod() {
    return MethodGenBuilder.<PojoMember, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> String.format("Tristate<%s>", f.memberName(RESOLVER)))
        .methodName(f -> f.getterName(RESOLVER).asString())
        .noArguments()
        .content(
            f ->
                String.format(
                    "return Tristate.ofNullableAndNullFlag(%s, is%sNull);",
                    f.memberName(RESOLVER), f.memberName(RESOLVER).startUpperCase()))
        .build()
        .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
  }

  private static Generator<PojoMember, PojoSettings> jacksonSerializerMethod() {
    return MethodGenBuilder.<PojoMember, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("Object")
        .methodName(f -> String.format("%sJackson", f.getterName(RESOLVER)))
        .noArguments()
        .content(
            f ->
                String.format(
                    "return is%sNull ? new JacksonNullContainer<>(%s) : %s;",
                    f.memberName(RESOLVER).startUpperCase(), f.getName(), f.getName()))
        .build()
        .append(RefsGenerator.fieldRefs())
        .append(w -> w.ref(OpenApiUtilRefs.JACKSON_NULL_CONTAINER))
        .filter(isJacksonJson());
  }
}
