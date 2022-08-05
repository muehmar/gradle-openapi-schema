package com.github.muehmar.gradle.openapi.generator.java.generator;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.BiPredicate;

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
    final Generator<PojoMember, PojoSettings> requiredNullableFlag =
        (field, settings, writer) ->
            writer.println(
                "private final boolean is%sPresent;", field.memberName(RESOLVER).startUpperCase());
    final Generator<PojoMember, PojoSettings> optionalNullableFlag =
        (field, settings, writer) ->
            writer.println(
                "private final boolean is%sNull;", field.memberName(RESOLVER).startUpperCase());
    return fieldDeclaration
        .appendConditionally(PojoMember::isRequiredAndNullable, requiredNullableFlag)
        .appendConditionally(PojoMember::isOptionalAndNullable, optionalNullableFlag)
        .append(
            (field, settings, writer) ->
                field.getType().getImports().foldLeft(writer, Writer::ref));
  }

  private static Generator<Pojo, PojoSettings> jsonValueAnnotation() {
    return JacksonAnnotationGenerator.<Pojo>jsonValue().filter(isArrayPojo());
  }

  private static BiPredicate<Pojo, PojoSettings> isArrayPojo() {
    return (pojo, settings) -> pojo.isArray();
  }
}
