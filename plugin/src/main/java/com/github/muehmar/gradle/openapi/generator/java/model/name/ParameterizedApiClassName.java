package com.github.muehmar.gradle.openapi.generator.java.model.name;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import java.util.Optional;
import lombok.EqualsAndHashCode;

/**
 * Parameterized classname for API types, i.e. contains all the classnames which are used in the API
 * (including API types of the type parameters).
 */
@EqualsAndHashCode
public class ParameterizedApiClassName {
  private final QualifiedClassName qualifiedClassName;
  private final PList<QualifiedClassName> genericTypes;

  private ParameterizedApiClassName(
      QualifiedClassName qualifiedClassName, PList<QualifiedClassName> genericTypes) {
    this.qualifiedClassName = qualifiedClassName;
    this.genericTypes = genericTypes;
  }

  /**
   * Returns a {@link ParameterizedApiClassName} in case the type itself or one of its type
   * parameters in case of a generic type has an {@link ApiType}.
   */
  public static Optional<ParameterizedApiClassName> fromJavaType(JavaType javaType) {
    return javaType.fold(
        ParameterizedApiClassName::ofJavaArrayType,
        ParameterizedApiClassName::fromNonGenericJavaType,
        ParameterizedApiClassName::fromNonGenericJavaType,
        ParameterizedApiClassName::ofJavaMapType,
        ParameterizedApiClassName::fromNonGenericJavaType,
        ParameterizedApiClassName::fromNonGenericJavaType,
        ParameterizedApiClassName::fromNonGenericJavaType,
        ParameterizedApiClassName::fromNonGenericJavaType,
        ParameterizedApiClassName::fromNonGenericJavaType);
  }

  private static Optional<ParameterizedApiClassName> ofJavaArrayType(JavaArrayType arrayType) {
    if (arrayType.hasApiType() || arrayType.getItemType().hasApiType()) {
      final QualifiedClassName apiQualifiedClassName =
          arrayType
              .getApiType()
              .map(ApiType::getClassName)
              .orElse(arrayType.getQualifiedClassName());
      return Optional.of(
          ofClassNameAndGenerics(apiQualifiedClassName, PList.single(arrayType.getItemType())));
    } else {
      return Optional.empty();
    }
  }

  private static Optional<ParameterizedApiClassName> ofJavaMapType(JavaMapType mapType) {
    if (mapType.hasApiType() || mapType.getKey().hasApiType() || mapType.getValue().hasApiType()) {
      final QualifiedClassName apiQualifiedClassName =
          mapType.getApiType().map(ApiType::getClassName).orElse(mapType.getQualifiedClassName());
      return Optional.of(
          ofClassNameAndGenerics(
              apiQualifiedClassName, PList.of(mapType.getKey(), mapType.getValue())));
    } else {
      return Optional.empty();
    }
  }

  private static Optional<ParameterizedApiClassName> fromNonGenericJavaType(JavaType javaType) {
    return javaType.getApiType().map(ApiType::getParameterizedClassName);
  }

  public static ParameterizedApiClassName ofClassNameAndGenerics(
      QualifiedClassName qualifiedClassName) {
    return ofClassNameAndGenerics(qualifiedClassName, PList.empty());
  }

  public static ParameterizedApiClassName ofClassNameAndGenerics(
      QualifiedClassName qualifiedClassName, PList<JavaType> genericTypes) {
    return new ParameterizedApiClassName(
        qualifiedClassName,
        genericTypes.map(
            javaType ->
                javaType
                    .getApiType()
                    .map(ApiType::getClassName)
                    .orElseGet(javaType::getQualifiedClassName)));
  }

  public PList<QualifiedClassName> getAllQualifiedClassNames() {
    return PList.single(qualifiedClassName).concat(genericTypes);
  }

  public String asString() {
    return qualifiedClassName
        .getClassNameWithGenerics(this.genericTypes.map(QualifiedClassName::getClassName))
        .asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
