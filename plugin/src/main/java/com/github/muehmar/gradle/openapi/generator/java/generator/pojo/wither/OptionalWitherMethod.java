package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
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
    return pojoMember.isRequiredAndNullable() || pojoMember.isOptionalAndNotNullable();
  }

  @Override
  String argumentType(ParameterizedClassName parameterizedClassName) {
    return String.format("Optional<%s>", parameterizedClassName);
  }

  @Override
  Map<JavaName, String> propertyNameReplacementForConstructorCall() {
    final HashMap<JavaName, String> propertyNameReplacement = new HashMap<>();
    if (pojoMember.isRequiredAndNullable()) {
      propertyNameReplacement.put(
          pojoMember.getName(), String.format("%s.orElse(null)", pojoMember.getName()));
      propertyNameReplacement.put(pojoMember.getIsPresentFlagName(), "true");
    } else if (pojoMember.isOptionalAndNotNullable()) {
      propertyNameReplacement.put(
          pojoMember.getName(), String.format("%s.orElse(null)", pojoMember.getName()));
      propertyNameReplacement.put(
          pojoMember.getIsNullFlagName(), String.format("!%s.isPresent()", pojoMember.getName()));
    }
    return propertyNameReplacement;
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
