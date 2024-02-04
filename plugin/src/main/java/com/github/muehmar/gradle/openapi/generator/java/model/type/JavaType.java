package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;

public interface JavaType {
  QualifiedClassName getQualifiedClassName();

  Nullability getNullability();

  JavaType withNullability(Nullability nullability);

  /**
   * Returns the qualified classnames used for this type, including the classes of possible type
   * parameters.s
   */
  PList<QualifiedClassName> getAllQualifiedClassNames();

  ParameterizedClassName getParameterizedClassName();

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

  default boolean isArrayType() {
    return fold(
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance,
        JavaArrayType.class::isInstance);
  }

  default boolean isMapType() {
    return fold(
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance,
        JavaMapType.class::isInstance);
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

  default boolean isObjectType() {
    return onObjectType().isPresent();
  }

  default Optional<JavaObjectType> onObjectType() {
    return fold(
        javaArrayType -> Optional.empty(),
        javaBooleanType -> Optional.empty(),
        javaEnumType -> Optional.empty(),
        javaMapType -> Optional.empty(),
        javaAnyType -> Optional.empty(),
        javaNumericType -> Optional.empty(),
        javaIntegerType -> Optional.empty(),
        Optional::of,
        javaStringType -> Optional.empty());
  }

  default PList<QualifiedClassName> getImports() {
    return getAllQualifiedClassNames()
        .filter(qualifiedClassName -> qualifiedClassName.getPackageName().isPresent())
        .filter(qualifiedClassName -> not(qualifiedClassName.isJavaLangPackage()));
  }

  default PList<String> getImportsAsString() {
    return getImports().map(QualifiedClassName::asString);
  }

  static JavaType wrap(Type type, TypeMappings typeMappings) {
    return type.fold(
        numericType -> JavaNumericType.wrap(numericType, typeMappings),
        numericType -> JavaIntegerType.wrap(numericType, typeMappings),
        stringType -> JavaStringType.wrap(stringType, typeMappings),
        arrayType -> JavaArrayType.wrap(arrayType, typeMappings),
        booleanType -> JavaBooleanType.wrap(booleanType, typeMappings),
        JavaObjectType::wrap,
        enumType -> JavaEnumType.wrap(enumType, typeMappings),
        mapType -> JavaMapType.wrap(mapType, typeMappings),
        JavaAnyType::javaAnyType);
  }
}
