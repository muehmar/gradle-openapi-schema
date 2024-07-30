package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.JsonGetter.jsonGetterGenerator;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FlagValidationGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FrameworkGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalOrGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.StandardGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.TristateGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list.ListOptionalGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list.ListOptionalOrGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list.ListStandardGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list.ListTristateGetter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Predicate;

enum GetterMethod {
  FRAMEWORK_GETTER(FrameworkGetter::frameworkGetter),
  STANDARD_GETTER(StandardGetter::standardGetterGenerator),
  OPTIONAL_GETTER(OptionalGetter::optionalGetterGenerator),
  OPTIONAL_OR_GETTER(OptionalOrGetter::optionalOrGetterGenerator),
  TRISTATE_GETTER(TristateGetter::tristateGetterGenerator),
  JSON_GETTER(ignore -> jsonGetterGenerator()),
  LIST_STANDARD_GETTER(ListStandardGetter::listStandardGetterGenerator),
  LIST_OPTIONAL_GETTER(ListOptionalGetter::listOptionalGetterGenerator),
  LIST_OPTIONAL_OR_GETTER(ListOptionalOrGetter::listOptionalOrGetterGenerator),
  LIST_TRISTATE_GETTER(ListTristateGetter::listTristateGetterGenerator),
  FLAG_VALIDATION_GETTER(FlagValidationGetter::flagValidationGetterGenerator);

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
