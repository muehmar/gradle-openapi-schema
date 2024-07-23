package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Predicate;
import lombok.Value;

@Value
class GetterGenerator {
  GetterMethod getterMethod;
  GetterGeneratorSettings settings;

  Generator<JavaPojoMember, PojoSettings> create(Predicate<JavaPojoMember> memberFilter) {
    return getterMethod.createGenerator(memberFilter, settings);
  }
}
