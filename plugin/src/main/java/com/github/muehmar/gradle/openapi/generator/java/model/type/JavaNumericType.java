package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
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
      Optional<ApiType> apiType,
      Nullability nullability,
      Constraints constraints) {
    super(className, apiType, nullability);
    this.constraints = constraints;
  }

  public static JavaNumericType wrap(NumericType numericType, TypeMappings typeMappings) {
    final QualifiedClassName originalClassName = internalClassNameFromFormat(numericType);
    final TypeMapping typeMapping = mapType(numericType, typeMappings, originalClassName);
    return new JavaNumericType(
        typeMapping.getClassName(),
        typeMapping.getApiType(),
        numericType.getNullability(),
        numericType.getConstraints());
  }

  private static TypeMapping mapType(
      NumericType numericType, TypeMappings typeMappings, QualifiedClassName internalClassName) {

    final TypeMapping formatTypeMapping =
        TypeMapping.fromFormatMappings(
            internalClassName,
            Optional.empty(),
            numericType.getFormat().asString(),
            typeMappings.getFormatTypeMappings(),
            PList.empty(),
            typeMappings.getTaskIdentifier());

    final TypeMapping classTypeMapping =
        TypeMapping.fromClassMappings(
            internalClassName,
            Optional.empty(),
            typeMappings.getClassTypeMappings(),
            PList.empty(),
            typeMappings.getTaskIdentifier());

    return formatTypeMapping.or(classTypeMapping, internalClassName);
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
    return new JavaNumericType(getQualifiedClassName(), apiType, nullability, constraints);
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
