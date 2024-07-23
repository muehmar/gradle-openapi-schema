package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FrameworkGetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Predicate;

enum GetterMethod {
  FRAMEWORK_GETTER(FrameworkGetter::frameworkGetter),
  STANDARD_GETTER(generatorSettings -> Generator.emptyGen()),
  OPTIONAL_GETTER(generatorSettings -> Generator.emptyGen()),
  OPTIONAL_OR_GETTER(generatorSettings -> Generator.emptyGen()),
  TRISTATE_GETTER(generatorSettings -> Generator.emptyGen()),
  TRISTATE_JSON_GETTER(generatorSettings -> Generator.emptyGen()),
  LIST_STANDARD_GETTER(generatorSettings -> Generator.emptyGen()),
  LIST_OPTIONAL_GETTER(generatorSettings -> Generator.emptyGen()),
  LIST_OPTIONAL_OR_GETTER(generatorSettings -> Generator.emptyGen()),
  LIST_TRISTATE_GETTER(generatorSettings -> Generator.emptyGen()),
  COMPOSITION_JSON_GETTER(generatorSettings -> Generator.emptyGen()),
  FLAG_VALIDATION_GETTER(generatorSettings -> Generator.emptyGen());

  private final Function<GetterGeneratorSettings, Generator<JavaPojoMember, PojoSettings>>
      generator;

  GetterMethod(
      Function<GetterGeneratorSettings, Generator<JavaPojoMember, PojoSettings>> generator) {
    this.generator = generator;
  }

  public Generator<JavaPojoMember, PojoSettings> createGenerator(
      Predicate<JavaPojoMember> memberFilter, GetterGeneratorSettings generatorSettings) {
    return generator.apply(generatorSettings).filter(memberFilter);
  }
}
