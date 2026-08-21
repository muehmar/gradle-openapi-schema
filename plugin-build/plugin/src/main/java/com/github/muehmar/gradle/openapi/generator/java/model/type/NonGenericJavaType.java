package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public abstract class NonGenericJavaType extends BaseJavaType {
  protected NonGenericJavaType(
      QualifiedClassName className, Optional<ApiType> apiType, Nullability nullability) {
    super(className, apiType, nullability);
  }

  @Override
  public PList<QualifiedClassName> getAllQualifiedClassNames() {
    return PList.of(className).concat(PList.fromOptional(apiType.map(ApiType::getClassName)));
  }

  @Override
  public ParameterizedClassName getParameterizedClassName() {
    return ParameterizedClassName.fromNonGenericClass(className);
  }

  /**
   * Folds over the possible non-generic java types. In contrast to {@link JavaType#fold} the
   * container types are not part of the cases, as they are generic types and therefore never a
   * {@link NonGenericJavaType}.
   */
  public <T> T foldNonGenericJavaType(
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaAnyType, T> onAnyType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return fold(
        arrayType -> {
          throw new IllegalStateException("An array type is not a non-generic java type");
        },
        onBooleanType,
        onEnumType,
        mapType -> {
          throw new IllegalStateException("A map type is not a non-generic java type");
        },
        onAnyType,
        onNumericType,
        onIntegerType,
        onObjectType,
        onStringType);
  }
}
