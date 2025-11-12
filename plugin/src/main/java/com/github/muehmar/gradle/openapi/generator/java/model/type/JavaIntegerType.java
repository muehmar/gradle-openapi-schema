package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
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
      QualifiedClassName className,
      Optional<ApiType> apiClassName,
      Constraints constraints,
      Nullability nullability) {
    super(className, apiClassName, nullability);
    this.constraints = constraints;
  }

  public static JavaIntegerType wrap(IntegerType integerType, TypeMappings typeMappings) {
    final QualifiedClassName originalClassName = internalClassNameFormFormat(integerType);
    final TypeMapping typeMapping = mapType(integerType, typeMappings, originalClassName);
    return new JavaIntegerType(
        typeMapping.getClassName(),
        typeMapping.getApiType(),
        integerType.getConstraints(),
        integerType.getNullability());
  }

  private static TypeMapping mapType(
      IntegerType integerType, TypeMappings typeMappings, QualifiedClassName internalClassName) {

    final TypeMapping formatTypeMapping =
        TypeMapping.fromFormatMappings(
            internalClassName,
            Optional.empty(),
            integerType.getFormat().asString(),
            typeMappings.getFormatTypeMappings());

    final TypeMapping classTypeMapping =
        TypeMapping.fromClassMappings(
            internalClassName, Optional.empty(), typeMappings.getClassTypeMappings());

    return formatTypeMapping.or(classTypeMapping, internalClassName);
  }

  private static QualifiedClassName internalClassNameFormFormat(IntegerType integerType) {
    return Optional.ofNullable(FORMAT_CLASS_NAME_MAP.get(integerType.getFormat()))
        .orElse(QualifiedClassNames.DOUBLE);
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
  public JavaType withNullability(Nullability nullability) {
    return new JavaIntegerType(className, apiType, constraints, nullability);
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
