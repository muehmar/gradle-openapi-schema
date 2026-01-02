package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
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
    map.put(StringType.Format.DATE_TIME, QualifiedClassNames.ZONED_DATE_TIME);
    map.put(StringType.Format.TIME, QualifiedClassNames.LOCAL_TIME);
    map.put(StringType.Format.BINARY, QualifiedClassNames.BYTE_ARRAY);
    map.put(StringType.Format.UUID, QualifiedClassNames.UUID);
    map.put(StringType.Format.URL, QualifiedClassNames.URL);
    map.put(StringType.Format.URI, QualifiedClassNames.URI);
    return map;
  }

  protected JavaStringType(
      QualifiedClassName className,
      Optional<ApiType> apiType,
      Nullability nullability,
      Constraints constraints) {
    super(className, apiType, nullability);
    this.constraints = constraints;
  }

  public static JavaStringType wrap(StringType stringType, TypeMappings typeMappings) {
    final QualifiedClassName originalClassName = internalClassNameFromFormat(stringType);
    final TypeMapping typeMapping = mapType(stringType, typeMappings, originalClassName);
    return new JavaStringType(
        typeMapping.getClassName(),
        typeMapping.getApiType(),
        stringType.getNullability(),
        stringType.getConstraints());
  }

  private static TypeMapping mapType(
      StringType stringType, TypeMappings typeMappings, QualifiedClassName internalClassName) {

    final TypeMapping formatTypeMapping =
        TypeMapping.fromFormatMappings(
            internalClassName,
            Optional.empty(),
            stringType.getFormatString(),
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
    return className.equals(QualifiedClassNames.BYTE_ARRAY);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaStringType(className, apiType, nullability, constraints);
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
