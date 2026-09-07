package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSetting.S_NULLABLE_CONTAINER_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSetting.S_OPTIONAL_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSetting.S_TRISTATE_SETTER;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.generator.ContainerSetter.containerSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.generator.JsonSetter.jsonSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.generator.StandardSetter.setterGenerator;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Predicate;

enum SetterMethod {
  STANDARD_SETTER(setterGenerator()),
  OPTIONAL_SETTER(setterGenerator(S_OPTIONAL_SETTER)),
  TRISTATE_SETTER(setterGenerator(S_TRISTATE_SETTER)),
  CONTAINER_STANDARD_SETTER(containerSetterGenerator()),
  CONTAINER_NULLABLE_VALUE_STANDARD_SETTER(containerSetterGenerator(S_NULLABLE_CONTAINER_VALUE)),
  CONTAINER_OPTIONAL_SETTER(containerSetterGenerator(S_OPTIONAL_SETTER)),
  CONTAINER_NULLABLE_VALUE_OPTIONAL_SETTER(
      containerSetterGenerator(S_OPTIONAL_SETTER, S_NULLABLE_CONTAINER_VALUE)),
  CONTAINER_TRISTATE_SETTER(containerSetterGenerator(S_TRISTATE_SETTER)),
  CONTAINER_NULLABLE_VALUE_TRISTATE_SETTER(
      containerSetterGenerator(S_TRISTATE_SETTER, S_NULLABLE_CONTAINER_VALUE)),
  JSON_SETTER(SetterMethod.NameScopeAware.JSON);

  private final Generator<MemberAndNameScope, PojoSettings> generator;

  /** Every setter carries the name scope, as it assigns the companion flag field. */
  SetterMethod(Generator<MemberAndNameScope, PojoSettings> generator) {
    this.generator = generator;
  }

  SetterMethod(NameScopeAware nameScopeAware) {
    this.generator = nameScopeAware.generator;
  }

  /** An enum constant cannot reference a generator of its own enum before it is initialised. */
  private enum NameScopeAware {
    JSON(jsonSetterGenerator());

    private final Generator<MemberAndNameScope, PojoSettings> generator;

    NameScopeAware(Generator<MemberAndNameScope, PojoSettings> generator) {
      this.generator = generator;
    }
  }

  public Generator<MemberAndNameScope, PojoSettings> createGenerator(
      Predicate<JavaPojoMember> memberFilter) {
    return generator.filter(
        memberAndNameScope -> memberFilter.test(memberAndNameScope.getMember()));
  }
}
