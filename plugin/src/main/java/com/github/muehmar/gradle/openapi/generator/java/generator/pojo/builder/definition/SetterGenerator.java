package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Predicate;
import lombok.Value;

@Value
class SetterGenerator {
  SetterMethod setterMethod;
  SetterGeneratorSettings settings;

  Generator<JavaPojoMember, PojoSettings> create(Predicate<JavaPojoMember> memberFilter) {
    return setterMethod.createGenerator(memberFilter, settings);
  }
}
