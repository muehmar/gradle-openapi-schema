package com.github.muehmar.gradle.openapi.generator.java.generator;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class FieldsGenerator {
  private static final Resolver RESOLVER = new JavaResolver();

  private FieldsGenerator() {}

  public static Generator<Pojo, PojoSettings> fields() {
    return Generator.<Pojo, PojoSettings>emptyGen()
        .append(jsonValueAnnotation())
        .appendList(singleField(), Pojo::getMembers);
  }

  public static Generator<PojoMember, PojoSettings> singleField() {
    final Generator<PojoMember, PojoSettings> fieldDeclaration =
        (field, settings, writer) ->
            writer.println(
                "private final %s %s;", field.getTypeName(RESOLVER), field.memberName(RESOLVER));
    final Generator<PojoMember, PojoSettings> nullableFieldFlag =
        (field, settings, writer) ->
            writer.println(
                "private final boolean is%sNull;", field.memberName(RESOLVER).startUpperCase());
    return fieldDeclaration
        .appendConditionally(PojoMember::isNullable, nullableFieldFlag)
        .append(
            (field, settings, writer) ->
                field.getType().getImports().foldLeft(writer, Writer::ref));
  }

  private static Generator<Pojo, PojoSettings> jsonValueAnnotation() {
    final Generator<Pojo, PojoSettings> jsonValueGen =
        Generator.ofWriterFunction(w -> w.println("@JsonValue").ref(JacksonRefs.JSON_VALUE));
    return Generator.<Pojo, PojoSettings>emptyGen()
        .appendConditionally(
            (pojo, settings) -> pojo.isArray() && settings.isJacksonJson(), jsonValueGen);
  }
}
