package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import java.util.function.BiFunction;

public class SetterModifier {

  private SetterModifier() {}

  public static BiFunction<JavaPojoMember, PojoSettings, JavaModifiers> modifiers() {
    return (member, settings) ->
        JavaModifiers.of(
            SetterModifier.forMember(
                member,
                settings,
                member.getJavaType().hasApiTypeDeep()
                    ? SetterModifier.SetterJavaType.API
                    : SetterModifier.SetterJavaType.DEFAULT));
  }

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
