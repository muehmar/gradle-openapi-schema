package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;

public class SetterModifier {

  private SetterModifier() {}

  public static JavaModifier forMember(
      JavaPojoMember member, PojoSettings settings, SetterJavaType type) {
    final JavaType javaType = member.getJavaType();
    if (type == SetterJavaType.DEFAULT && javaType.hasApiTypeDeep()) {
      return JavaModifier.PRIVATE;
    }
    if (settings.isEnableStagedBuilder() && member.isRequired()) {
      return JavaModifier.PRIVATE;
    }
    return JavaModifier.PUBLIC;
  }

  public enum SetterJavaType {
    DEFAULT,
    API
  }
}
