package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
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

  protected JavaStringType(ClassName className, Constraints constraints) {
    super(className);
    this.constraints = constraints;
  }

  public static JavaStringType wrap(StringType stringType, TypeMappings typeMappings) {
    final ClassName className =
        classNameFromFormat(stringType, typeMappings.getFormatTypeMappings());
    final ClassName finalClassName =
        className.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaStringType(finalClassName, stringType.getConstraints());
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
  public JavaType asPrimitive() {
    return new JavaStringType(className.asPrimitive(), constraints);
  }
}
