package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class NullableItemsListWrappers {
  private NullableItemsListWrappers() {}

  public static Generator<JavaObjectPojo, PojoSettings> nullableItemsListWrappers() {
    return WrapNullableItemsListMethod.<JavaObjectPojo>wrapNullableItemsListMethod()
        .appendSingleBlankLine()
        .append(UnwrapNullableItemsListMethod.unwrapNullableItemsListMethod())
        .appendSingleBlankLine()
        .append(UnwrapOptionalNullableItemsListMethod.unwrapOptionalNullableItemsListMethod())
        .appendSingleBlankLine()
        .append(UnwrapTristateNullableItemsListMethod.unwrapTristateNullableItemsListMethod())
        .filter(NullableItemsListWrappers::needsNullableItemsListWrappers);
  }

  private static boolean needsNullableItemsListWrappers(JavaObjectPojo pojo) {
    return pojo.getAllMembers().exists(member -> member.getJavaType().isNullableItemsArrayType());
  }
}
