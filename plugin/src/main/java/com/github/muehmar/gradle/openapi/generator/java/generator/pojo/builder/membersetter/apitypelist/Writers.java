package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NULL_SAFE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;

class Writers {
  private Writers() {}

  public static Writer itemWriter(JavaPojoMember member, JavaArrayType javaArrayType) {
    final String variableName = "item" + (member.getName().asString().equals("item") ? "_" : "");
    return javaArrayType
        .getItemType()
        .getApiType()
        .map(itemApiType -> conversionWriter(itemApiType, variableName))
        .map(
            writer ->
                javaWriter()
                    .print("%s -> %s", variableName, writer.asString())
                    .refs(writer.getRefs()))
        .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  public static Writer conversionWriter(ApiType apiType, String variableName) {
    return FromApiTypeConversion.fromApiTypeConversion(apiType, variableName, NULL_SAFE);
  }
}
