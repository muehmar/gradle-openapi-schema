package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class SetterGenerator {
  private SetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> setterGenerator() {
    return SetterGroupsDefinition.create().generator();
  }
}
