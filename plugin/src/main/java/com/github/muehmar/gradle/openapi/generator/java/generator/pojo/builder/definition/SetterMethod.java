package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.ContainerNullableValueOptionalSetter.containerNullableValueOptionalSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.ContainerNullableValueStandardSetter.containerNullableValueStandardGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.ContainerNullableValueTristateSetter.containerNullableValueTristateSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.ContainerOptionalSetter.containerOptionalSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.ContainerStandardSetter.containerStandardSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.ContainerTristateSetter.containerTristateSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.JsonSetter.jsonSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.OptionalSetter.optionalSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.StandardSetter.standardSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.TristateSetter.tristateSetterGenerator;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Predicate;

enum SetterMethod {
  STANDARD_SETTER(ignore -> standardSetterGenerator()),
  OPTIONAL_SETTER(ignore -> optionalSetterGenerator()),
  TRISTATE_SETTER(ignore -> tristateSetterGenerator()),
  CONTAINER_STANDARD_SETTER(ignore -> containerStandardSetterGenerator()),
  CONTAINER_NULLABLE_VALUE_STANDARD_SETTER(ignore -> containerNullableValueStandardGetter()),
  CONTAINER_OPTIONAL_SETTER(ignore -> containerOptionalSetterGenerator()),
  CONTAINER_NULLABLE_VALUE_OPTIONAL_SETTER(
      ignore -> containerNullableValueOptionalSetterGenerator()),
  CONTAINER_TRISTATE_SETTER(ignore -> containerTristateSetterGenerator()),
  CONTAINER_NULLABLE_VALUE_TRISTATE_SETTER(
      ignore -> containerNullableValueTristateSetterGenerator()),
  JSON_SETTER(ignore -> jsonSetterGenerator());

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
