package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
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
    map.put(StringType.Format.DATE_TIME, QualifiedClassNames.LOCAL_DATE_TIME);
    map.put(StringType.Format.TIME, QualifiedClassNames.LOCAL_TIME);
    map.put(StringType.Format.BINARY, QualifiedClassNames.BYTE_ARRAY);
    map.put(StringType.Format.UUID, QualifiedClassNames.UUID);
    map.put(StringType.Format.URL, QualifiedClassNames.URL);
    map.put(StringType.Format.URI, QualifiedClassNames.URI);
    return map;
  }

  protected JavaStringType(
      QualifiedClassName className, Nullability nullability, Constraints constraints) {
    super(className, nullability);
    this.constraints = constraints;
  }

  public static JavaStringType wrap(StringType stringType, TypeMappings typeMappings) {
    final QualifiedClassName className =
        classNameFromFormat(stringType, typeMappings.getFormatTypeMappings());
    final QualifiedClassName finalClassName =
        className.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaStringType(
        finalClassName, stringType.getNullability(), stringType.getConstraints());
  }

  public static JavaStringType noFormat() {
    return new JavaStringType(QualifiedClassNames.STRING, NOT_NULLABLE, Constraints.empty());
  }

  private static QualifiedClassName classNameFromFormat(
      StringType stringType, PList<FormatTypeMapping> formatTypeMappings) {
    final Optional<QualifiedClassName> userFormatMappedClassName =
        QualifiedClassName.fromFormatTypeMapping(stringType.getFormatString(), formatTypeMappings);
    final QualifiedClassName formatMappedClassName =
        Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(stringType.getFormat()))
            .orElse(QualifiedClassNames.STRING);
    return userFormatMappedClassName.orElse(formatMappedClassName);
  }

  @Override
  public boolean isJavaArray() {
    return qualifiedClassName.equals(QualifiedClassNames.BYTE_ARRAY);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaStringType(qualifiedClassName, nullability, constraints);
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
