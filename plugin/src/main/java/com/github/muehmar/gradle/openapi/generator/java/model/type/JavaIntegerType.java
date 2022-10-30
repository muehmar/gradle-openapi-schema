package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
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
public class JavaIntegerType extends NonGenericJavaType {
  private static final Map<IntegerType.Format, ClassName> FORMAT_CLASS_NAME_MAP =
      createFormatClassNameMap();
  private final Constraints constraints;

  protected JavaIntegerType(ClassName className, Constraints constraints) {
    super(className);
    this.constraints = constraints;
  }

  public static JavaIntegerType wrap(IntegerType integerType, TypeMappings typeMappings) {
    final ClassName className =
        classNameFromFormat(integerType, typeMappings.getFormatTypeMappings());
    final ClassName finalClassName =
        className.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaIntegerType(finalClassName, integerType.getConstraints());
  }

  private static ClassName classNameFromFormat(
      IntegerType integerType, PList<FormatTypeMapping> formatTypeMappings) {
    final Optional<ClassName> userFormatMappedClassName =
        ClassName.fromFormatTypeMapping(integerType.getFormat().asString(), formatTypeMappings);
    final ClassName formatMappedClassName =
        Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(integerType.getFormat()))
            .orElse(ClassNames.DOUBLE);
    return userFormatMappedClassName.orElse(formatMappedClassName);
  }

  private static Map<IntegerType.Format, ClassName> createFormatClassNameMap() {
    final Map<IntegerType.Format, ClassName> map = new EnumMap<>(IntegerType.Format.class);
    map.put(IntegerType.Format.LONG, ClassNames.LONG);
    map.put(IntegerType.Format.INTEGER, ClassNames.INTEGER);
    return map;
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
      Function<JavaNoType, T> onNoType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return onIntegerType.apply(this);
  }
}
