package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
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
public class JavaStringType extends NonGenericJavaType {
  private static final Map<StringType.Format, QualifiedClassName> FORMAT_CLASS_NAME_MAP =
      createFormatClassNameMap();

  private final Constraints constraints;

  private static Map<StringType.Format, QualifiedClassName> createFormatClassNameMap() {
    final Map<StringType.Format, QualifiedClassName> map = new EnumMap<>(StringType.Format.class);
    map.put(StringType.Format.DATE, QualifiedClassNames.LOCAL_DATE);
    map.put(StringType.Format.DATE_TIME, QualifiedClassNames.LOCAL_DATE_TIME);
    map.put(StringType.Format.TIME, QualifiedClassNames.LOCAL_TIME);
    map.put(StringType.Format.BINARY, QualifiedClassNames.BYTE_ARRAY);
    map.put(StringType.Format.UUID, QualifiedClassNames.UUID);
    map.put(StringType.Format.URL, QualifiedClassNames.URL);
    map.put(StringType.Format.URI, QualifiedClassNames.URI);
    return map;
  }

  protected JavaStringType(
      QualifiedClassName className,
      Optional<QualifiedClassName> apiClassName,
      Nullability nullability,
      Constraints constraints) {
    super(className, apiClassName, nullability);
    this.constraints = constraints;
  }

  public static JavaStringType wrap(StringType stringType, TypeMappings typeMappings) {
    final QualifiedClassName internalClassName = internalClassNameFromFormat(stringType);
    final Optional<QualifiedClassName> apiClassName =
        determineApiClassName(stringType, typeMappings, internalClassName);
    return new JavaStringType(
        internalClassName, apiClassName, stringType.getNullability(), stringType.getConstraints());
  }

  private static Optional<QualifiedClassName> determineApiClassName(
      StringType stringType, TypeMappings typeMappings, QualifiedClassName internalClassName) {
    final Optional<QualifiedClassName> formatMappedClassName =
        QualifiedClassName.fromFormatTypeMapping(
            stringType.getFormatString(), typeMappings.getFormatTypeMappings());
    final Optional<QualifiedClassName> classNameMappedClassName =
        internalClassName.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return Optionals.or(formatMappedClassName, classNameMappedClassName);
  }

  public static JavaStringType noFormat() {
    return new JavaStringType(
        QualifiedClassNames.STRING, Optional.empty(), NOT_NULLABLE, Constraints.empty());
  }

  private static QualifiedClassName internalClassNameFromFormat(StringType stringType) {
    return Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(stringType.getFormat()))
        .orElse(QualifiedClassNames.STRING);
  }

  @Override
  public boolean isJavaArray() {
    return internalClassName.equals(QualifiedClassNames.BYTE_ARRAY);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaStringType(internalClassName, apiClassName, nullability, constraints);
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
    return onStringType.apply(this);
  }
}
