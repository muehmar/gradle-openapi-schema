package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaMapType implements JavaType {
  private static final QualifiedClassName INTERNAL_JAVA_CLASS_NAME = QualifiedClassNames.MAP;

  private final QualifiedClassName className;
  private final Optional<ApiType> apiType;
  private final JavaType key;
  private final JavaType value;
  private final Nullability nullability;
  private final Constraints constraints;

  private JavaMapType(
      QualifiedClassName className,
      Optional<ApiType> apiType,
      JavaType key,
      JavaType value,
      Nullability nullability,
      Constraints constraints) {
    this.className = className;
    this.apiType = apiType;
    this.key = key;
    this.value = value;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static JavaMapType wrap(MapType mapType, TypeMappings typeMappings) {
    final JavaType key = JavaType.wrap(mapType.getKey(), TypeMappings.empty());
    final JavaType value = JavaType.wrap(mapType.getValue(), typeMappings);
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            INTERNAL_JAVA_CLASS_NAME,
            Optional.empty(),
            typeMappings.getClassTypeMappings(),
            PList.of(key, value),
            typeMappings.getTaskIdentifier());
    return new JavaMapType(
        typeMapping.getClassName(),
        typeMapping.getApiType(),
        key,
        value,
        mapType.getNullability(),
        mapType.getConstraints());
  }

  public static JavaMapType ofKeyAndValueType(JavaType key, JavaType value) {
    return new JavaMapType(
        INTERNAL_JAVA_CLASS_NAME, Optional.empty(), key, value, NOT_NULLABLE, Constraints.empty());
  }

  @Override
  public QualifiedClassName getQualifiedClassName() {
    return className;
  }

  @Override
  public Optional<ApiType> getApiType() {
    return apiType;
  }

  @Override
  public PList<QualifiedClassName> getAllQualifiedClassNames() {
    return PList.single(getQualifiedClassName())
        .concat(PList.fromOptional(getApiType().map(ApiType::getClassName)))
        .concat(key.getAllQualifiedClassNames())
        .concat(value.getAllQualifiedClassNames());
  }

  @Override
  public ParameterizedClassName getParameterizedClassName() {
    return ParameterizedClassName.fromGenericClass(getQualifiedClassName(), PList.of(key, value));
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public Nullability getNullability() {
    return nullability;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaMapType(className, apiType, key, value, nullability, constraints);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  public JavaType getKey() {
    return key;
  }

  public JavaType getValue() {
    return value;
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
    return onMapType.apply(this);
  }
}
