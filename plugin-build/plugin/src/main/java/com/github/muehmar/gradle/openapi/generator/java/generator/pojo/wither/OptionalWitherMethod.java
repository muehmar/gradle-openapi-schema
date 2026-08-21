package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Map;

class OptionalWitherMethod extends WitherMethod {
  public OptionalWitherMethod(
      WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    super(witherContent, pojoMember);
  }

  @Override
  boolean shouldBeUsed() {
    return (pojoMember.isRequiredAndNullable() || pojoMember.isOptionalAndNotNullable())
        && not(pojoMember.getJavaType().isContainerType());
  }

  @Override
  String argumentType(WriteableParameterizedClassName parameterizedClassName) {
    return String.format("Optional<%s>", parameterizedClassName);
  }

  @Override
  boolean isOverloadedWither() {
    return true;
  }

  @Override
  Map<JavaName, StringOrWriter> propertyNameReplacementForConstructorCall() {
    final HashMap<JavaName, StringOrWriter> propertyNameReplacement = new HashMap<>();

    final String apiTypeMapping =
        pojoMember
            .getJavaType()
            .getApiType()
            .map(
                apiType ->
                    FromApiTypeConversionRenderer.fromApiTypeConversion(
                        apiType, "val", NO_NULL_CHECK))
            .map(Writer::asString)
            .map(conversion -> String.format(".map(val -> %s)", conversion))
            .orElse("");

    final String replacment =
        String.format("%s%s.orElse(null)", pojoMember.getName(), apiTypeMapping);

    propertyNameReplacement.put(pojoMember.getName(), StringOrWriter.ofString(replacment));

    return propertyNameReplacement;
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
