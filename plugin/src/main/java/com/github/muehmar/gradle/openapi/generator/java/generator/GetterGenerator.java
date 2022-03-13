package com.github.muehmar.gradle.openapi.generator.java.generator;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PRIVATE;
import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;

public class GetterGenerator {
  private static final Resolver RESOLVER = new JavaResolver();

  private GetterGenerator() {}

  public static Generator<PojoMember, PojoSettings> generator() {
    return mainGetter()
        .appendNewLine()
        .append(jacksonNullableSerializer())
        .append(jacksonOptionalSerializer())
        .append(getNullableFlag());
  }

  public static Generator<PojoMember, PojoSettings> mainGetter() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .appendConditionally(PojoMember::isRequired, mainRequiredGetter())
        .appendConditionally(PojoMember::isOptional, mainOptionalGetter());
  }

  public static Generator<PojoMember, PojoSettings> mainRequiredGetter() {
    return MethodGen.<PojoMember, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(f -> f.getTypeName(RESOLVER).asString())
        .methodName(f -> f.getterName(RESOLVER).asString())
        .noArguments()
        .content(f -> String.format("return %s;", f.memberName(RESOLVER)))
        .append(RefsGenerator.fieldRefs());
  }

  public static Generator<PojoMember, PojoSettings> mainOptionalGetter() {
    final Generator<PojoMember, PojoSettings> method =
        MethodGen.<PojoMember, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(f -> String.format("Optional<%s>", f.getTypeName(RESOLVER)))
            .methodName(f -> f.getterName(RESOLVER).asString())
            .noArguments()
            .content(f -> String.format("return Optional.ofNullable(%s);", f.memberName(RESOLVER)))
            .append(RefsGenerator.fieldRefs())
            .append(RefsGenerator.optionalRef());
    return jsonIgnore().append(method);
  }

  public static Generator<PojoMember, PojoSettings> jacksonNullableSerializer() {
    final Generator<PojoMember, PojoSettings> method =
        MethodGen.<PojoMember, PojoSettings>modifiers(PRIVATE)
            .noGenericTypes()
            .returnType("Object")
            .methodName(f -> String.format("%sJackson", f.getterName(RESOLVER)))
            .noArguments()
            .content(
                f ->
                    String.format(
                        "return is%sNull ? new JacksonNullContainer<>(%s) : %s;",
                        f.memberName(RESOLVER), f.getName(), f.getName()))
            .append(RefsGenerator.fieldRefs());
    return jsonProperty()
        .append(w -> w.ref(JacksonRefs.JSON_PROPERTY))
        .append(writer -> writer.println("@JsonInclude(JsonInclude.Include.NON_NULL"))
        .append(w -> w.ref(JacksonRefs.JSON_INCLUDE))
        .append(method)
        .appendNewLine()
        .filter((field, settings) -> settings.isJacksonJson() && field.isNullable());
  }

  public static Generator<PojoMember, PojoSettings> jacksonOptionalSerializer() {
    final Generator<PojoMember, PojoSettings> method =
        MethodGen.<PojoMember, PojoSettings>modifiers(PRIVATE)
            .noGenericTypes()
            .returnType(field -> field.getType().getFullName().asString())
            .methodName(f -> String.format("get%sNullable", f.getName().startUpperCase()))
            .noArguments()
            .content(f -> String.format("return %s;", f.getName()))
            .append(RefsGenerator.fieldRefs());
    return jsonProperty()
        .append(method)
        .appendNewLine()
        .filter(
            (field, settings) ->
                field.isOptional() && !field.isNullable() && settings.isJacksonJson());
  }

  public static Generator<PojoMember, PojoSettings> getNullableFlag() {
    final Generator<PojoMember, PojoSettings> method =
        MethodGen.<PojoMember, PojoSettings>modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(f -> String.format("is%sNull", f.getName().startUpperCase()))
            .noArguments()
            .content(f -> String.format("return %s;", f.memberName(RESOLVER)))
            .append(RefsGenerator.fieldRefs());

    return jsonIgnore().append(method).filter((field, setting) -> field.isNullable());
  }

  private static Generator<PojoMember, PojoSettings> jsonIgnore() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonIgnore").ref(JacksonRefs.JSON_IGNORE))
        .filter((ignore, settings) -> settings.isJacksonJson());
  }

  private static Generator<PojoMember, PojoSettings> jsonProperty() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(
            (field, s, w) ->
                w.println("@JsonProperty(\"%s\")", field.getName()).ref(JacksonRefs.JSON_PROPERTY))
        .filter((ignore, settings) -> settings.isJacksonJson());
  }
}
