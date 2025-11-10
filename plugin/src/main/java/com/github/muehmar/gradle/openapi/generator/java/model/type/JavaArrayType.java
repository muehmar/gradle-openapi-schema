package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.PluginApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Wraps a {@link ArrayType} but not to be confused with actual java arrays. */
@EqualsAndHashCode
@ToString
public class JavaArrayType implements JavaType {
  private final QualifiedClassName className;
  private final Optional<ApiType> apiType;
  private final JavaType itemType;
  private final Nullability nullability;
  private final Constraints constraints;

  private static final QualifiedClassName INTERNAL_JAVA_CLASS_NAME = QualifiedClassNames.LIST;

  private JavaArrayType(
      QualifiedClassName className,
      Optional<ApiType> apiType,
      JavaType itemType,
      Nullability nullability,
      Constraints constraints) {
    this.className = className;
    this.apiType = apiType;
    this.itemType = itemType;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static JavaArrayType wrap(ArrayType arrayType, TypeMappings typeMappings) {
    final JavaType itemType = JavaType.wrap(arrayType.getItemType(), typeMappings);
    final Optional<PluginApiType> pluginApiType =
        arrayType.getConstraints().isUniqueItems()
            ? Optional.of(PluginApiType.useSetForListType(itemType))
            : Optional.empty();
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            INTERNAL_JAVA_CLASS_NAME,
            pluginApiType,
            typeMappings.getClassTypeMappings(),
            PList.single(itemType));
    return new JavaArrayType(
        typeMapping.getClassName(),
        typeMapping.getApiType(),
        itemType,
        arrayType.getNullability(),
        arrayType.getConstraints());
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
        .concat(itemType.getAllQualifiedClassNames());
  }

  @Override
  public ParameterizedClassName getParameterizedClassName() {
    return ParameterizedClassName.fromGenericClass(getQualifiedClassName(), PList.single(itemType));
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
  public JavaArrayType withNullability(Nullability nullability) {
    return new JavaArrayType(className, apiType, itemType, nullability, constraints);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  public JavaType getItemType() {
    return itemType;
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
    return onArrayType.apply(this);
  }
}
