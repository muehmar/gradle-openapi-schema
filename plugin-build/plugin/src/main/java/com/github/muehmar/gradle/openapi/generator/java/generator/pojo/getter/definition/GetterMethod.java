package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FlagGetter.flagGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FlagValidationGetter.flagValidationGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.JsonGetter.jsonGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ValidationGetter.validationGetterGenerator;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalOrGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.StandardGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.TristateGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerOptionalGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerOptionalOrGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerStandardGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerTristateGetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Predicate;

enum GetterMethod {
  STANDARD_GETTER(StandardGetter::standardGetterGenerator),
  OPTIONAL_GETTER(OptionalGetter::optionalGetterGenerator),
  OPTIONAL_OR_GETTER(OptionalOrGetter::optionalOrGetterGenerator),
  TRISTATE_GETTER(TristateGetter::tristateGetterGenerator),
  CONTAINER_STANDARD_GETTER(ContainerStandardGetter::containerStandardGetterGenerator),
  CONTAINER_OPTIONAL_GETTER(ContainerOptionalGetter::containerOptionalGetterGenerator),
  CONTAINER_OPTIONAL_OR_GETTER(ContainerOptionalOrGetter::containerOptionalOrGetterGenerator),
  CONTAINER_TRISTATE_GETTER(ContainerTristateGetter::containerTristateGetterGenerator),
  JSON_GETTER(ignore -> jsonGetterGenerator()),
  VALIDATION_GETTER(ignore -> validationGetterGenerator()),
  FLAG_VALIDATION_GETTER(ignore -> flagValidationGetterGenerator()),
  FLAG_GETTER(ignore -> flagGetterGenerator());

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
