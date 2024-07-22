package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.OptionalNotNullableGetter.optionalNotNullableGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.OptionalNullableGetter.optionalNullableGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.RequiredNotNullableGetter.requiredNotNullableGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.RequiredNullableGetter.requiredNullableGetterGenerator;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class NullableItemsListGetterGenerator {
  private NullableItemsListGetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> nullableItemsListGetterGenerator(
      GetterType getterType) {
    return requiredNotNullableGetterGenerator(getterType)
        .append(requiredNullableGetterGenerator(getterType))
        .append(optionalNotNullableGetter(getterType))
        .append(optionalNullableGetter(getterType));
  }
}
