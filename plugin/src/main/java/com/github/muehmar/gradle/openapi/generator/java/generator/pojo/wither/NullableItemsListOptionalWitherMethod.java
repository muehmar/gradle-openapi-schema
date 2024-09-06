package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Map;

class NullableItemsListOptionalWitherMethod extends WitherMethod {
  private final NullableItemsListDelegate delegate;

  public NullableItemsListOptionalWitherMethod(
      WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    super(witherContent, pojoMember);
    final OptionalWitherMethod delegateWitherMethod =
        new OptionalWitherMethod(witherContent, pojoMember);
    delegate = new NullableItemsListDelegate(delegateWitherMethod, pojoMember);
  }

  @Override
  boolean shouldBeUsed() {
    return delegate.shouldBeUsed();
  }

  @Override
  String argumentType(WriteableParameterizedClassName parameterizedClassName) {
    return String.format(
        "Optional<%s>", parameterizedClassName.asStringWrappingNullableValueType());
  }

  @Override
  String witherName() {
    return delegate.witherName();
  }

  @Override
  Map<JavaName, String> propertyNameReplacementForConstructorCall() {
    return delegate.nullableItemsListPropertyNameReplacement();
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
