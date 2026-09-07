package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CrossDtoAccessor;
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
  JSON_GETTER(GetterMethod.NameScopeAware.JSON),
  VALIDATION_GETTER(GetterMethod.NameScopeAware.VALIDATION),
  FLAG_VALIDATION_GETTER(FlagValidationGetter::flagValidationGetterGenerator),
  CROSS_DTO_VALUE_ACCESSOR(CrossDtoAccessor::valueAccessorGenerator),
  CROSS_DTO_FLAG_ACCESSOR(CrossDtoAccessor::flagAccessorGenerator);

  private final Function<Visibility, Generator<MemberAndNameScope, PojoSettings>> generator;

  /**
   * The getters which are derived from the member alone. Only the anchors whose name has to avoid
   * the names of their siblings need the whole {@link MemberAndNameScope}.
   */
  GetterMethod(Function<Visibility, Generator<JavaPojoMember, PojoSettings>> generator) {
    this.generator =
        visibility -> generator.apply(visibility).contraMap(MemberAndNameScope::getMember);
  }

  GetterMethod(Supplier<Generator<JavaPojoMember, PojoSettings>> generator) {
    this(ignoredVisibility -> generator.get());
  }

  GetterMethod(NameScopeAware nameScopeAware) {
    this.generator = ignoredVisibility -> nameScopeAware.generator;
  }

  /**
   * Holder for the getters needing the sibling names: an enum constant cannot reference a generator
   * of its own enum before the enum is initialised.
   */
  private enum NameScopeAware {
    JSON(JsonGetter.jsonGetterGenerator()),
    VALIDATION(ValidationGetter.validationGetterGenerator());

    private final Generator<MemberAndNameScope, PojoSettings> generator;

    NameScopeAware(Generator<MemberAndNameScope, PojoSettings> generator) {
      this.generator = generator;
    }
  }

  public Generator<MemberAndNameScope, PojoSettings> createGenerator(Visibility visibility) {
    return generator.apply(visibility);
  }
}
