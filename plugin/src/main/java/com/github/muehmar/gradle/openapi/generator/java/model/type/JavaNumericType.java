package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.util.Optionals;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaNumericType extends NonGenericJavaType {
  private static final Map<NumericType.Format, QualifiedClassName> FORMAT_CLASS_NAME_MAP =
      createFormatClassNameMap();
  private final Constraints constraints;

  protected JavaNumericType(
      QualifiedClassName className,
      Optional<QualifiedClassName> apiClassName,
      Nullability nullability,
      Constraints constraints) {
    super(className, apiClassName, nullability);
    this.constraints = constraints;
  }

  public static JavaNumericType wrap(NumericType numericType, TypeMappings typeMappings) {
    final QualifiedClassName internalClassName = internalClassNameFromFormat(numericType);
    final Optional<QualifiedClassName> apiClassName =
        determineApiClassName(numericType, typeMappings, internalClassName);
    return new JavaNumericType(
        internalClassName,
        apiClassName,
        numericType.getNullability(),
        numericType.getConstraints());
  }

  private static Optional<QualifiedClassName> determineApiClassName(
      NumericType numericType, TypeMappings typeMappings, QualifiedClassName internalClassName) {
    final Optional<QualifiedClassName> formatMappedClassName =
        QualifiedClassName.fromFormatTypeMapping(
            numericType.getFormat().asString(), typeMappings.getFormatTypeMappings());
    final Optional<QualifiedClassName> classNameMappedClassName =
        internalClassName.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return Optionals.or(formatMappedClassName, classNameMappedClassName);
  }

  private static QualifiedClassName internalClassNameFromFormat(NumericType numericType) {
    return Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(numericType.getFormat()))
        .orElse(QualifiedClassNames.DOUBLE);
  }

  private static Map<NumericType.Format, QualifiedClassName> createFormatClassNameMap() {
    final Map<NumericType.Format, QualifiedClassName> map = new EnumMap<>(NumericType.Format.class);
    map.put(NumericType.Format.DOUBLE, QualifiedClassNames.DOUBLE);
    map.put(NumericType.Format.FLOAT, QualifiedClassNames.FLOAT);
    return map;
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaNumericType(getInternalClassName(), apiClassName, nullability, constraints);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
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
    return onNumericType.apply(this);
  }
}
