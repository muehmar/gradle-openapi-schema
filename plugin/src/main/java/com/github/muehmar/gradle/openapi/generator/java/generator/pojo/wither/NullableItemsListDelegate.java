package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapNullableItemsListMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Common delegate for nullable items list methods, which delegates itself again to the actual
 * {@link WitherMethod}.
 */
class NullableItemsListDelegate {
  private final WitherMethod delegate;
  private final JavaPojoMember pojoMember;

  public NullableItemsListDelegate(WitherMethod delegate, JavaPojoMember pojoMember) {
    this.delegate = delegate;
    this.pojoMember = pojoMember;
  }

  boolean shouldBeUsed() {
    return delegate.shouldBeUsed() && pojoMember.getJavaType().isNullableItemsArrayType();
  }

  Map<JavaName, String> nullableItemsListPropertyNameReplacement() {
    final Map<JavaName, String> propertyNameReplacement =
        delegate.propertyNameReplacementForConstructorCall();
    final String argument =
        Optional.ofNullable(propertyNameReplacement.get(pojoMember.getName()))
            .orElse(pojoMember.getName().asString());

    final HashMap<JavaName, String> adjustedPropertyNameReplacement =
        new HashMap<>(propertyNameReplacement);

    adjustedPropertyNameReplacement.put(
        pojoMember.getName(),
        String.format("%s(%s)", UnwrapNullableItemsListMethod.METHOD_NAME, argument));
    return adjustedPropertyNameReplacement;
  }
}
