package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
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
    return true;
  }

  @Override
  String argumentType(ParameterizedClassName parameterizedClassName) {
    return parameterizedClassName.asString();
  }

  @Override
  Map<JavaName, String> propertyNameReplacementForConstructorCall() {
    final HashMap<JavaName, String> propertyNameReplacement = new HashMap<>();
    if (pojoMember.isRequiredAndNullable()) {
      propertyNameReplacement.put(pojoMember.getIsPresentFlagName(), "true");
    } else if (pojoMember.isOptionalAndNullable()) {
      propertyNameReplacement.put(pojoMember.getIsNullFlagName(), "false");
    }
    return propertyNameReplacement;
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer;
  }
}
