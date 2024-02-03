package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
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
  private static final Map<IntegerType.Format, QualifiedClassName> FORMAT_CLASS_NAME_MAP =
      createFormatClassNameMap();
  private final Constraints constraints;

  protected JavaIntegerType(
      QualifiedClassName className, Constraints constraints, Nullability nullability) {
    super(className, nullability);
    this.constraints = constraints;
  }

  public static JavaIntegerType wrap(IntegerType integerType, TypeMappings typeMappings) {
    final QualifiedClassName className =
        classNameFromFormat(integerType, typeMappings.getFormatTypeMappings());
    final QualifiedClassName finalClassName =
        className.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaIntegerType(
        finalClassName, integerType.getConstraints(), integerType.getNullability());
  }

  private static QualifiedClassName classNameFromFormat(
      IntegerType integerType, PList<FormatTypeMapping> formatTypeMappings) {
    final Optional<QualifiedClassName> userFormatMappedClassName =
        QualifiedClassName.fromFormatTypeMapping(
            integerType.getFormat().asString(), formatTypeMappings);
    final QualifiedClassName formatMappedClassName =
        Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(integerType.getFormat()))
            .orElse(QualifiedClassNames.DOUBLE);
    return userFormatMappedClassName.orElse(formatMappedClassName);
  }

  private static Map<IntegerType.Format, QualifiedClassName> createFormatClassNameMap() {
    final Map<IntegerType.Format, QualifiedClassName> map = new EnumMap<>(IntegerType.Format.class);
    map.put(IntegerType.Format.LONG, QualifiedClassNames.LONG);
    map.put(IntegerType.Format.INTEGER, QualifiedClassNames.INTEGER);
    return map;
  }

  @Override
  public boolean isJavaArray() {
    return false;
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
    return onIntegerType.apply(this);
  }
}
