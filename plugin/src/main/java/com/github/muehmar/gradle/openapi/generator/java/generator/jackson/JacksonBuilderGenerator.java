package com.github.muehmar.gradle.openapi.generator.java.generator.jackson;

import static io.github.muehmar.pojoextension.generator.impl.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.gen.ClassGen;
import io.github.muehmar.pojoextension.generator.impl.gen.MethodGen;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class JacksonBuilderGenerator {
  private static final Resolver RESOLVER = new JavaResolver();

  private JacksonBuilderGenerator() {}

  public static Generator<Pojo, PojoSettings> jacksonBuilderGen() {
    return JacksonBuilderGenerator.<Pojo, PojoSettings>jsonPojoBuilderAnnotation()
        .append(builderClass());
  }

  public static <A, B> Generator<A, B> jsonPojoBuilderAnnotation() {
    return Generator.<A, B>ofWriterFunction(w -> w.println("@JsonPOJOBuilder(withPrefix = \"\")"))
        .append(w -> w.ref(JacksonRefs.JSON_POJO_BUILDER));
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
    return builderFields()
        .appendNewLine()
        .append(builderConstructor())
        .appendNewLine()
        .appendList(memberMethod().appendNewLine(), Pojo::getMembers)
        .append(buildMethod());
  }

  public static Generator<Pojo, PojoSettings> builderFields() {
    return (pojo, settings, writer) ->
        pojo.getMembers().foldLeft(writer, JacksonBuilderGenerator::printMemberFields);
  }

  private static Writer printMemberFields(Writer writer, PojoMember member) {
    final Writer writerMemberField =
        writer.println("private %s %s;", member.getTypeName(RESOLVER), member.getName());
    if (member.isNullable()) {
      return writerMemberField.println(
          "private boolean is%sNull = false;", member.getName().startUpperCase());
    } else {
      return writerMemberField;
    }
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
            member -> String.format("%s %s", member.getTypeName(RESOLVER), member.getName()))
        .content(memberMethodContent());
  }

  public static Generator<PojoMember, PojoSettings> memberMethodContent() {
    final Generator<PojoMember, PojoSettings> assignment =
        (member, settings, writer) ->
            writer.println("this.%s = %s;", member.getName(), member.getName());

    final Generator<PojoMember, PojoSettings> nullableAssignment =
        (member, settings, writer) ->
            writer
                .println("if(%s == null) {", member.getName())
                .println("  this.is%sNull = true;", member.getName().startUpperCase())
                .println("}");

    return assignment
        .appendConditionally(PojoMember::isNullable, nullableAssignment)
        .append(w -> w.println("return this;"));
  }

  public static Generator<Pojo, PojoSettings> buildMethod() {
    return MethodGen.<Pojo, PojoSettings>modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(pojo -> pojo.className(RESOLVER).asString())
        .methodName("build")
        .noArguments()
        .content(
            pojo -> {
              final String members =
                  pojo.getMembers().map(member -> member.memberName(RESOLVER)).mkString(", ");
              return String.format("return new %s(%s);", pojo.className(RESOLVER), members);
            });
  }
}
