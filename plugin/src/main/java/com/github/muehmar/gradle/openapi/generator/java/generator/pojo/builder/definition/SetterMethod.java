package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Predicate;

enum SetterMethod {
  STANDARD_SETTER(ignore -> Generator.emptyGen()),
  OPTIONAL_SETTER(ignore -> Generator.emptyGen()),
  TRISTATE_SETTER(ignore -> Generator.emptyGen()),
  CONTAINER_STANDARD_SETTER(ignore -> Generator.emptyGen()),
  CONTAINER_OPTIONAL_SETTER(ignore -> Generator.emptyGen()),
  CONTAINER_TRISTATE_SETTER(ignore -> Generator.emptyGen()),
  JSON_SETTER(ignore -> Generator.emptyGen());

  private final Function<SetterGeneratorSettings, Generator<JavaPojoMember, PojoSettings>>
      generator;

  SetterMethod(
      Function<SetterGeneratorSettings, Generator<JavaPojoMember, PojoSettings>> generator) {
    this.generator = generator;
  }

  public Generator<JavaPojoMember, PojoSettings> createGenerator(
      Predicate<JavaPojoMember> memberFilter, SetterGeneratorSettings generatorSettings) {
    return generator.apply(generatorSettings).filter(memberFilter);
  }
}
