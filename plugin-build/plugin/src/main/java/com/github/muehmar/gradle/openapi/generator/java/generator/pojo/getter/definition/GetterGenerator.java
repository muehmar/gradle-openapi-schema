package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import lombok.Value;

@Value
class GetterGenerator {
  GetterMethod getterMethod;
  Visibility visibility;

  Generator<JavaPojoMember, PojoSettings> create() {
    return getterMethod.createGenerator(visibility);
  }
}
