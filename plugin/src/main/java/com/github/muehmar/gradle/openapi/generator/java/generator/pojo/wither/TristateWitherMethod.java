package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Map;

class TristateWitherMethod extends WitherMethod {
  public TristateWitherMethod(
      WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    super(witherContent, pojoMember);
  }

  @Override
  boolean shouldBeUsed() {
    return pojoMember.isOptionalAndNullable() && not(pojoMember.getJavaType().isContainerType());
  }

  @Override
  String argumentType(WriteableParameterizedClassName parameterizedClassName) {
    return String.format("Tristate<%s>", parameterizedClassName);
  }

  @Override
  boolean isOverloadedWither() {
    return true;
  }

  Map<JavaName, StringOrWriter> propertyNameReplacementForConstructorCall() {
    final HashMap<JavaName, StringOrWriter> propertyNameReplacement = new HashMap<>();

    final String apiTypeMapping =
        pojoMember
            .getJavaType()
            .getApiType()
            .map(
                apiType ->
                    FromApiTypeConversion.fromApiTypeConversion(apiType, "val", NO_NULL_CHECK))
            .map(Writer::asString)
            .map(conversion -> String.format(".map(val -> %s)", conversion))
            .orElse("");

    final StringOrWriter replacement =
        StringOrWriter.ofString(
            String.format(
                "%s%s.%s", pojoMember.getName(), apiTypeMapping, pojoMember.tristateToProperty()));

    propertyNameReplacement.put(pojoMember.getName(), replacement);
    return propertyNameReplacement;
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer.ref(OpenApiUtilRefs.TRISTATE);
  }
}
