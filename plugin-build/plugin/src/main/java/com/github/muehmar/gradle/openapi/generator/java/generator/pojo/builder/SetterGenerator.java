package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class SetterGenerator {
  private SetterGenerator() {}

  /**
   * The setters of every property. The members are paired with the names taken by their siblings,
   * as the anchors must not collide with them, see issue #438.
   */
  public static Generator<JavaObjectPojo, PojoSettings> setterGenerator() {
    final Generator<MemberAndNameScope, PojoSettings> memberSetterGenerator =
        memberSetterGenerator();
    return (pojo, settings, writer) ->
        Generator.<JavaObjectPojo, PojoSettings>emptyGen()
            .appendList(
                memberSetterGenerator,
                ignore -> MemberAndNameScope.forMembers(pojo.getAllMembers(), settings))
            .generate(pojo, settings, writer);
  }

  public static Generator<MemberAndNameScope, PojoSettings> memberSetterGenerator() {
    return SetterGroupsDefinition.create().generator();
  }
}
