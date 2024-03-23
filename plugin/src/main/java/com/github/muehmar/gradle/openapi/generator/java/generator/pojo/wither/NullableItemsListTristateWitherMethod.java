package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Map;

class NullableItemsListTristateWitherMethod extends WitherMethod {
  private final NullableItemsListDelegate delegate;

  public NullableItemsListTristateWitherMethod(
      WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    super(witherContent, pojoMember);
    final TristateWitherMethod delegateWitherMethod =
        new TristateWitherMethod(witherContent, pojoMember);
    delegate = new NullableItemsListDelegate(delegateWitherMethod, pojoMember);
  }

  @Override
  boolean shouldBeUsed() {
    return delegate.shouldBeUsed();
  }

  @Override
  String argumentType(ParameterizedClassName parameterizedClassName) {
    return String.format(
        "Tristate<%s>", parameterizedClassName.asStringWrappingNullableValueType());
  }

  @Override
  Map<JavaName, String> propertyNameReplacementForConstructorCall() {
    return delegate.nullableItemsListPropertyNameReplacement();
  }

  @Override
  Writer addRefs(Writer writer) {
    return writer.ref(OpenApiUtilRefs.TRISTATE).ref(JavaRefs.JAVA_UTIL_OPTIONAL);
  }
}
