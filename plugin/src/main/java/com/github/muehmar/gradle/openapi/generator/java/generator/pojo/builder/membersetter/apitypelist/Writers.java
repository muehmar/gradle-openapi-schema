package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;

public class Writers {
  private Writers() {}

  public static Writer itemMappingWriter(JavaPojoMember member, JavaArrayType javaArrayType) {
    final String variableName = "item" + (member.getName().asString().equals("item") ? "_" : "");
    return javaArrayType
        .getItemType()
        .getApiType()
        .map(
            itemApiType ->
                conversionWriter(itemApiType, variableName, ConversionGenerationMode.NULL_SAFE))
        .map(
            writer ->
                javaWriter()
                    .print("%s -> %s", variableName, writer.asString())
                    .refs(writer.getRefs()))
        .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  public static Writer tristateListArgumentConversionWriter(
      JavaPojoMember member, JavaArrayType javaArrayType) {
    final String unwrapList =
        String.format(
            "%s.onValue(Function.identity()).onNull(() -> null).onAbsent(() -> null)",
            member.getName());
    return nullSafeListArgumentConversionWriter(unwrapList, javaArrayType)
        .ref(JavaRefs.JAVA_UTIL_FUNCTION);
  }

  public static Writer optionalListArgumentConversionWriter(
      JavaPojoMember member, JavaArrayType javaArrayType) {
    final String unwrapList = String.format("%s.orElse(null)", member.getName());
    return nullSafeListArgumentConversionWriter(unwrapList, javaArrayType);
  }

  public static Writer nullSafeListArgumentConversionWriter(
      JavaPojoMember member, JavaArrayType javaArrayType) {
    return nullSafeListArgumentConversionWriter(member.getName().asString(), javaArrayType);
  }

  public static Writer nullSafeListArgumentConversionWriter(
      String argument, JavaArrayType javaArrayType) {
    return javaArrayType
        .getApiType()
        .map(
            listApiType ->
                conversionWriter(listApiType, argument, ConversionGenerationMode.NULL_SAFE))
        .orElse(javaWriter().print(argument));
  }

  public static Writer noNullCheckListArgumentConversionWriter(
      JavaPojoMember member, JavaArrayType javaArrayType) {
    return javaArrayType
        .getApiType()
        .map(
            listApiType ->
                conversionWriter(
                    listApiType,
                    member.getName().asString(),
                    ConversionGenerationMode.NO_NULL_CHECK))
        .orElse(javaWriter().print(member.getName().asString()));
  }

  public static Writer conversionWriter(
      ApiType apiType, String variableName, ConversionGenerationMode conversionGenerationMode) {
    return FromApiTypeConversion.fromApiTypeConversion(
        apiType, variableName, conversionGenerationMode);
  }
}
