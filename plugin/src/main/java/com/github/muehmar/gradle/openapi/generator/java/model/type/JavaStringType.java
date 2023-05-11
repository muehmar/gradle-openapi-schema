package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
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
  private static final Map<StringType.Format, ClassName> FORMAT_CLASS_NAME_MAP =
      createFormatClassNameMap();

  private final Constraints constraints;

  private static Map<StringType.Format, ClassName> createFormatClassNameMap() {
    final Map<StringType.Format, ClassName> map = new EnumMap<>(StringType.Format.class);
    map.put(StringType.Format.DATE, ClassNames.LOCAL_DATE);
    map.put(StringType.Format.DATE_TIME, ClassNames.LOCAL_DATE_TIME);
    map.put(StringType.Format.TIME, ClassNames.LOCAL_TIME);
    map.put(StringType.Format.BINARY, ClassNames.BYTE_ARRAY);
    map.put(StringType.Format.UUID, ClassNames.UUID);
    map.put(StringType.Format.URL, ClassNames.URL);
    map.put(StringType.Format.URI, ClassNames.URI);
    return map;
  }

  protected JavaStringType(ClassName className, Constraints constraints, StringType stringType) {
    super(className, stringType);
    this.constraints = constraints;
  }

  public static JavaStringType wrap(StringType stringType, TypeMappings typeMappings) {
    final ClassName className =
        classNameFromFormat(stringType, typeMappings.getFormatTypeMappings());
    final ClassName finalClassName =
        className.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaStringType(finalClassName, stringType.getConstraints(), stringType);
  }

  private static ClassName classNameFromFormat(
      StringType stringType, PList<FormatTypeMapping> formatTypeMappings) {
    final Optional<ClassName> userFormatMappedClassName =
        ClassName.fromFormatTypeMapping(stringType.getFormatString(), formatTypeMappings);
    final ClassName formatMappedClassName =
        Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(stringType.getFormat()))
            .orElse(ClassNames.STRING);
    return userFormatMappedClassName.orElse(formatMappedClassName);
  }

  @Override
  public boolean isJavaArray() {
    return className.equals(ClassNames.BYTE_ARRAY);
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
    return onStringType.apply(this);
  }
}
