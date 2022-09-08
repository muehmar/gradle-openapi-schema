package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaNumericType extends NonGenericJavaType {
  private static final Map<NumericType.Format, ClassName> FORMAT_CLASS_NAME_MAP =
      createFormatClassNameMap();
  private final NumericType numericType;

  protected JavaNumericType(ClassName className, NumericType numericType) {
    super(className);
    this.numericType = numericType;
  }

  public static JavaNumericType wrap(NumericType numericType, TypeMappings typeMappings) {
    final ClassName className =
        classNameFromFormat(numericType, typeMappings.getFormatTypeMappings());
    final ClassName finalClassName =
        className.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaNumericType(finalClassName, numericType);
  }

  private static ClassName classNameFromFormat(
      NumericType numericType, PList<FormatTypeMapping> formatTypeMappings) {
    final Optional<ClassName> userFormatMappedClassName =
        ClassName.fromFormatTypeMapping(numericType.getFormat().asString(), formatTypeMappings);
    final ClassName formatMappedClassName =
        Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(numericType.getFormat()))
            .orElse(ClassNames.DOUBLE);
    return userFormatMappedClassName.orElse(formatMappedClassName);
  }

  private static Map<NumericType.Format, ClassName> createFormatClassNameMap() {
    final Map<NumericType.Format, ClassName> map = new EnumMap<>(NumericType.Format.class);
    map.put(NumericType.Format.DOUBLE, ClassNames.DOUBLE);
    map.put(NumericType.Format.FLOAT, ClassNames.FLOAT);
    map.put(NumericType.Format.LONG, ClassNames.LONG);
    map.put(NumericType.Format.INTEGER, ClassNames.INTEGER);
    return map;
  }
}
