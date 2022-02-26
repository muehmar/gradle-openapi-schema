package com.github.muehmar.gradle.openapi.generator.java.generator;

import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;

public class JacksonBuilderGenerator {
  private JacksonBuilderGenerator() {}

  public static Generator<Pojo, PojoSettings> jacksonBuilderGen() {
    return JacksonBuilderGenerator.<Pojo, PojoSettings>jsonPojoBuilderAnnotation()
        .append(builderClass());
  }

  public static <A, B> Generator<A, B> jsonPojoBuilderAnnotation() {
    return Generator.<A, B>ofWriterFunction(w -> w.println("@JsonPOJOBuilder(withPrefix = \"\")"))
        .append(w -> w.ref("com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder"));
  }

  public static Generator<Pojo, PojoSettings> builderClass() {
    return ClassGen.<Pojo, PojoSettings>clazz()
        .nested()
        .modifiers(JavaModifier.STATIC)
        .className("Builder")
        .noSuperClassAndInterface()
        .content(builderClassContent());
  }

  public static Generator<Pojo, PojoSettings> builderClassContent() {
    return builderFields().append(builderConstructor());
  }

  public static Generator<Pojo, PojoSettings> builderFields() {
    return (pojo, settings, writer) ->
        pojo.getMembers()
            .foldLeft(writer, (w, member) -> w.println("private %s", member.getName().asString()));
  }

  public static <A, B> Generator<A, B> builderConstructor() {
    return Generator.ofWriterFunction(w -> w.println("private Builder() {}"));
  }

  public static Generator<PojoMember, PojoSettings> memberMethod() {
    return MethodGen.<PojoMember, PojoSettings>modifiers()
        .noGenericTypes()
        .returnType("Builder")
        .methodName(member -> member.getName().asString())
        .singleArgument(
            member ->
                String.format("%s %s", member.getType().getFullName(), member.getName().asString()))
        .content(
            (member, settings, writer) ->
                writer
                    .println(
                        "this.%s = %s;", member.getName().asString(), member.getName().asString())
                    .println("return this;"));
  }
}
