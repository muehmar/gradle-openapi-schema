package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FlagValidationGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FrameworkGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalOrGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.StandardGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.TristateGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.TristateJsonGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.list.ListStandardGetter;
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
  TRISTATE_JSON_GETTER(TristateJsonGetter::tristateJsonGetterGenerator),
  LIST_STANDARD_GETTER(ListStandardGetter::listStandardGetterGenerator),
  LIST_OPTIONAL_GETTER(generatorSettings -> Generator.emptyGen()),
  LIST_OPTIONAL_OR_GETTER(generatorSettings -> Generator.emptyGen()),
  LIST_TRISTATE_GETTER(generatorSettings -> Generator.emptyGen()),
  COMPOSITION_JSON_GETTER(generatorSettings -> Generator.emptyGen()),
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
