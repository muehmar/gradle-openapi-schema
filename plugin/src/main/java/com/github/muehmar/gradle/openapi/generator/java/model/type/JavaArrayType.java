package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
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
  private final Optional<QualifiedClassName> apiQualifiedClassName;
  private final JavaType itemType;
  private final Nullability nullability;
  private final Constraints constraints;

  private static final QualifiedClassName INTERNAL_JAVA_CLASS_NAME = QualifiedClassNames.LIST;

  private JavaArrayType(
      Optional<QualifiedClassName> apiQualifiedClassName,
      JavaType itemType,
      Nullability nullability,
      Constraints constraints) {
    this.apiQualifiedClassName = apiQualifiedClassName;
    this.itemType = itemType;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static JavaArrayType wrap(ArrayType arrayType, TypeMappings typeMappings) {
    final Optional<QualifiedClassName> userTypeClassName =
        INTERNAL_JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaArrayType(
        userTypeClassName,
        JavaType.wrap(arrayType.getItemType(), typeMappings),
        arrayType.getNullability(),
        arrayType.getConstraints());
  }

  @Override
  public QualifiedClassName getInternalClassName() {
    return INTERNAL_JAVA_CLASS_NAME;
  }

  @Override
  public Optional<QualifiedClassName> getApiClassName() {
    return apiQualifiedClassName;
  }

  @Override
  public PList<QualifiedClassName> getAllQualifiedClassNames() {
    return PList.single(getInternalClassName())
        .concat(PList.fromOptional(getApiClassName()))
        .concat(itemType.getAllQualifiedClassNames());
  }

  @Override
  public ParameterizedClassName getInternalParameterizedClassName() {
    return ParameterizedClassName.fromGenericClass(getInternalClassName(), PList.single(itemType));
  }

  @Override
  public Optional<ParameterizedClassName> getApiParameterizedClassName() {
    return getApiClassName()
        .map(cn -> ParameterizedClassName.fromGenericClass(cn, PList.single(itemType)));
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
    return new JavaArrayType(apiQualifiedClassName, itemType, nullability, constraints);
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
