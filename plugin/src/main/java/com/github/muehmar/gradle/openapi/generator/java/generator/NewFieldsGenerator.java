package com.github.muehmar.gradle.openapi.generator.java.generator;

import com.github.muehmar.gradle.openapi.generator.java.generator.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.function.BiPredicate;

public class NewFieldsGenerator {
  private NewFieldsGenerator() {}

  public static Generator<JavaPojo, PojoSettings> fields() {
    return Generator.<JavaPojo, PojoSettings>emptyGen()
        .append(jsonValueAnnotation())
        .appendList(singleField(), JavaPojo::getMembersOrEmpty);
  }

  public static Generator<JavaPojoMember, PojoSettings> singleField() {
    final Generator<JavaPojoMember, PojoSettings> fieldDeclaration =
        (field, settings, writer) ->
            writer.println(
                "private final %s %s;", field.getJavaType().getFullClassName(), field.getName());
    final Generator<JavaPojoMember, PojoSettings> requiredNullableFlag =
        (field, settings, writer) ->
            writer.println("private final boolean is%sPresent;", field.getName().startUpperCase());
    final Generator<JavaPojoMember, PojoSettings> optionalNullableFlag =
        (field, settings, writer) ->
            writer.println("private final boolean is%sNull;", field.getName().startUpperCase());
    return fieldDeclaration
        .appendConditionally(JavaPojoMember::isRequiredAndNullable, requiredNullableFlag)
        .appendConditionally(JavaPojoMember::isOptionalAndNullable, optionalNullableFlag)
        .append(
            (field, settings, writer) ->
                field.getJavaType().getImportsAsString().foldLeft(writer, Writer::ref));
  }

  private static Generator<JavaPojo, PojoSettings> jsonValueAnnotation() {
    return JacksonAnnotationGenerator.<JavaPojo>jsonValue().filter(isArrayPojo());
  }

  private static BiPredicate<JavaPojo, PojoSettings> isArrayPojo() {
    return (pojo, settings) -> pojo.isArray();
  }
}
