package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.OptionalNotNullableGetter.optionalNotNullableGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.OptionalNullableGetter.optionalNullableGetter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.RequiredNotNullableGetter.requiredNotNullableGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.RequiredNullableGetter.requiredNullableGetterGenerator;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class NullableItemsListGetterGenerator {
  private NullableItemsListGetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> nullableItemsListGetterGenerator(
      GetterGenerator.GeneratorOption option) {
    return requiredNotNullableGetterGenerator(option)
        .append(requiredNullableGetterGenerator(option))
        .append(optionalNotNullableGetter(option))
        .append(optionalNullableGetter(option));
  }
}
