package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaAnyType extends NonGenericJavaType {
  private static final QualifiedClassName CLASS_NAME = QualifiedClassNames.OBJECT;

  private JavaAnyType(Nullability nullability) {
    super(CLASS_NAME, Optional.empty(), nullability);
  }

  public static JavaAnyType javaAnyType(AnyType anyType) {
    return new JavaAnyType(anyType.getNullability());
  }

  public static JavaAnyType javaAnyType(Nullability nullability) {
    return new JavaAnyType(nullability);
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaAnyType(nullability);
  }

  @Override
  public <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaAnyType, T> onAnyType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return onAnyType.apply(this);
  }
}
