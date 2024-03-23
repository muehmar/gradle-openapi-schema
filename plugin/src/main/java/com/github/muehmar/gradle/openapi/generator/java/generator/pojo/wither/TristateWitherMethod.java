package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
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
    return pojoMember.isOptionalAndNullable();
  }

  @Override
  String argumentType(ParameterizedClassName parameterizedClassName) {
    return String.format("Tristate<%s>", parameterizedClassName);
  }

  Map<JavaName, String> propertyNameReplacementForConstructorCall() {
    final HashMap<JavaName, String> propertyNameReplacement = new HashMap<>();
    if (pojoMember.isOptionalAndNullable()) {
      propertyNameReplacement.put(
          pojoMember.getName(),
          String.format("%s.%s", pojoMember.getName(), pojoMember.tristateToProperty()));
      propertyNameReplacement.put(
          pojoMember.getIsNullFlagName(),
          String.format("%s.%s", pojoMember.getName(), pojoMember.tristateToIsNullFlag()));
    }
    return propertyNameReplacement;
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer.ref(OpenApiUtilRefs.TRISTATE);
  }
}
