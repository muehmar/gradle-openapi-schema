package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Map;

class NullableItemsListNormalWitherMethod extends NormalWitherMethod {
  private final NullableItemsListDelegate delegate;

  public NullableItemsListNormalWitherMethod(
      WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    super(witherContent, pojoMember);
    final NormalWitherMethod delegateWitherMethod =
        new NormalWitherMethod(witherContent, pojoMember);
    delegate = new NullableItemsListDelegate(delegateWitherMethod, pojoMember);
  }

  @Override
  boolean shouldBeUsed() {
    return delegate.shouldBeUsed();
  }

  @Override
  String argumentType(ParameterizedClassName parameterizedClassName) {
    return parameterizedClassName.asStringWrappingNullableValueType();
  }

  @Override
  Map<JavaName, String> propertyNameReplacementForConstructorCall() {
    return delegate.nullableItemsListPropertyNameReplacement();
  }

  @Override
  Writer addRefs(Writer writer) {
    return super.addRefs(writer).ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
