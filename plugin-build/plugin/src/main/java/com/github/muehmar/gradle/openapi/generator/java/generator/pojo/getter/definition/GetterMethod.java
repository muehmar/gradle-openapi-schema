package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FlagGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.FlagValidationGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.JsonGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.OptionalOrGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.StandardGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.TristateGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ValidationGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerOptionalGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerOptionalOrGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerStandardGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype.ContainerTristateGetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile.Visibility;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Supplier;

enum GetterMethod {
  STANDARD_GETTER(StandardGetter::standardGetterGenerator),
  OPTIONAL_GETTER(OptionalGetter::optionalGetterGenerator),
  TRISTATE_GETTER(TristateGetter::tristateGetterGenerator),
  CONTAINER_STANDARD_GETTER(ContainerStandardGetter::containerStandardGetterGenerator),
  CONTAINER_OPTIONAL_GETTER(ContainerOptionalGetter::containerOptionalGetterGenerator),
  CONTAINER_TRISTATE_GETTER(ContainerTristateGetter::containerTristateGetterGenerator),
  OPTIONAL_OR_GETTER(OptionalOrGetter::optionalOrGetterGenerator),
  CONTAINER_OPTIONAL_OR_GETTER(ContainerOptionalOrGetter::containerOptionalOrGetterGenerator),
  JSON_GETTER(JsonGetter::jsonGetterGenerator),
  VALIDATION_GETTER(ValidationGetter::validationGetterGenerator),
  FLAG_VALIDATION_GETTER(FlagValidationGetter::flagValidationGetterGenerator),
  FLAG_GETTER(FlagGetter::flagGetterGenerator);

  private final Function<Visibility, Generator<JavaPojoMember, PojoSettings>> generator;

  GetterMethod(Function<Visibility, Generator<JavaPojoMember, PojoSettings>> generator) {
    this.generator = generator;
  }

  GetterMethod(Supplier<Generator<JavaPojoMember, PojoSettings>> generator) {
    this(ignoredVisibility -> generator.get());
  }

  public Generator<JavaPojoMember, PojoSettings> createGenerator(Visibility visibility) {
    return generator.apply(visibility);
  }
}
