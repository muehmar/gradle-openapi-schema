package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Map;

class NormalWitherMethod extends WitherMethod {
  public NormalWitherMethod(
      WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    super(witherContent, pojoMember);
  }

  @Override
  boolean shouldBeUsed() {
    return not(pojoMember.getJavaType().isContainerType());
  }

  @Override
  String argumentType(WriteableParameterizedClassName parameterizedClassName) {
    return parameterizedClassName.asString();
  }

  @Override
  boolean isOverloadedWither() {
    return false;
  }

  @Override
  Map<JavaName, StringOrWriter> propertyNameReplacementForConstructorCall() {
    final HashMap<JavaName, StringOrWriter> replacement = new HashMap<>();
    pojoMember
        .getJavaType()
        .getApiType()
        .map(
            apiType ->
                FromApiTypeConversion.fromApiTypeConversion(
                    apiType, pojoMember.getName().asString(), NO_NULL_CHECK))
        .map(StringOrWriter::ofWriter)
        .ifPresent(writer -> replacement.put(pojoMember.getName(), writer));
    return replacement;
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer;
  }
}
