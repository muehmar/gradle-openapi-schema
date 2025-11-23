package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;

public interface JavaType {

  static JavaType wrap(Type type, TypeMappings typeMappings) {
    return type.fold(
        numericType -> JavaNumericType.wrap(numericType, typeMappings),
        numericType -> JavaIntegerType.wrap(numericType, typeMappings),
        stringType -> JavaStringType.wrap(stringType, typeMappings),
        arrayType -> JavaArrayType.wrap(arrayType, typeMappings),
        booleanType -> JavaBooleanType.wrap(booleanType, typeMappings),
        javaObjectType -> JavaObjectType.wrap(javaObjectType, typeMappings),
        enumType -> JavaEnumType.wrap(enumType, typeMappings),
        mapType -> JavaMapType.wrap(mapType, typeMappings),
        JavaAnyType::javaAnyType);
  }

  /**
   * Returns the qualified classname of the java type used internally in the DTO for serialization
   * and validation.
   */
  QualifiedClassName getQualifiedClassName();

  /**
   * Returns the {@link ApiType} of this {@link JavaType}, not for possible generics. I.e. it
   * returns also an empty {@link Optional} in case the {@link JavaType} itself has no {@link
   * ApiType} but it has generics with an {@link ApiType}.
   */
  Optional<ApiType> getApiType();

  /** Returns true if the {@link JavaType} or any possible generics have an {@link ApiType}. */
  default boolean hasApiTypeDeep() {
    return fold(
        arrayType -> arrayType.getApiType().isPresent() || arrayType.getItemType().hasApiTypeDeep(),
        javaBooleanType -> javaBooleanType.getApiType().isPresent(),
        javaEnumType -> javaEnumType.getApiType().isPresent(),
        mapType ->
            mapType.getApiType().isPresent()
                || mapType.getKey().hasApiTypeDeep()
                || mapType.getValue().hasApiTypeDeep(),
        javaAnyType -> javaAnyType.getApiType().isPresent(),
        javaNumericType -> javaNumericType.getApiType().isPresent(),
        javaIntegerType -> javaIntegerType.getApiType().isPresent(),
        javaObjectType -> javaObjectType.getApiType().isPresent(),
        javaStringType -> javaStringType.getApiType().isPresent());
  }

  /**
   * Returns false if neither the {@link JavaType} nor any possible generics have an {@link
   * ApiType}.
   */
  default boolean hasNoApiTypeDeep() {
    return not(hasApiTypeDeep());
  }

  /**
   * Returns the qualified classnames used for this type, including the classes of possible type
   * parameters as well as possible api types.
   */
  PList<QualifiedClassName> getAllQualifiedClassNames();

  Nullability getNullability();

  JavaType withNullability(Nullability nullability);

  /** Returns the parameterized classname of the java type used internally. */
  ParameterizedClassName getParameterizedClassName();

  /** Returns the parameterized API classname of the java type if it has an api type. */
  default Optional<ParameterizedApiClassName> getParameterizedApiClassName() {
    return ParameterizedApiClassName.fromJavaType(this);
  }

  /**
   * Returns a writeable parameterized classname which gets rendered as api type if any or as normal
   * type if not.
   */
  default WriteableParameterizedClassName getWriteableParameterizedClassName() {
    return getParameterizedApiClassName()
        .<WriteableParameterizedClassName>map(Function.identity())
        .orElse(getParameterizedClassName());
  }

  /**
   * Returns true in case this class is a java array (not to be confused with the openapi
   * array-type).
   */
  boolean isJavaArray();

  Constraints getConstraints();

  <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaAnyType, T> onAnyType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType);

  default Optional<JavaArrayType> onArrayType() {
    return fold(
        Optional::of,
        javaBooleanType -> Optional.empty(),
        javaEnumType -> Optional.empty(),
        javaMapType -> Optional.empty(),
        javaAnyType -> Optional.empty(),
        javaNumericType -> Optional.empty(),
        javaIntegerType -> Optional.empty(),
        javaObjectType -> Optional.empty(),
        javaStringType -> Optional.empty());
  }

  default boolean isArrayType() {
    return onArrayType().isPresent();
  }

  default boolean isNullableContainerValueType() {
    return isNullableValuesMapType() || isNullableItemsArrayType();
  }

  default boolean isNullableItemsArrayType() {
    return onArrayType()
        .map(JavaArrayType::getItemType)
        .map(JavaType::getNullability)
        .map(Nullability::isNullable)
        .orElse(false);
  }

  default boolean isNullableValuesMapType() {
    return onMapType()
        .map(JavaMapType::getValue)
        .map(JavaType::getNullability)
        .map(Nullability::isNullable)
        .orElse(false);
  }

  default Optional<JavaMapType> onMapType() {
    return fold(
        javaArrayType -> Optional.empty(),
        javaBooleanType -> Optional.empty(),
        javaEnumType -> Optional.empty(),
        Optional::of,
        javaAnyType -> Optional.empty(),
        javaNumericType -> Optional.empty(),
        javaIntegerType -> Optional.empty(),
        javaObjectType -> Optional.empty(),
        javaStringType -> Optional.empty());
  }

  default boolean isMapType() {
    return onMapType().isPresent();
  }

  default Optional<JavaEnumType> onEnumType() {
    return fold(
        javaArrayType -> Optional.empty(),
        javaBooleanType -> Optional.empty(),
        Optional::of,
        javaMapType -> Optional.empty(),
        javaAnyType -> Optional.empty(),
        javaNumericType -> Optional.empty(),
        javaIntegerType -> Optional.empty(),
        javaObjectType -> Optional.empty(),
        javaStringType -> Optional.empty());
  }

  default boolean isAnyType() {
    return fold(
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance,
        JavaAnyType.class::isInstance);
  }

  default boolean isIntegerType() {
    return fold(
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance,
        JavaIntegerType.class::isInstance);
  }

  default boolean isNumericType() {
    return fold(
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance,
        JavaNumericType.class::isInstance);
  }

  /** Returns true if this type is a container type, i.e. a list or a map. */
  default boolean isContainerType() {
    return isArrayType() || isMapType();
  }

  default PList<QualifiedClassName> getImports() {
    return getAllQualifiedClassNames().filter(QualifiedClassName::usedForImport);
  }

  default PList<String> getImportsAsString() {
    return getImports().map(QualifiedClassName::asString);
  }
}
