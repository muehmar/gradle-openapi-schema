package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaMapType implements JavaType {
  private static final ClassName JAVA_CLASS_NAME = ClassNames.MAP;

  private final ClassName className;
  private final MapType mapType;
  private final JavaType key;
  private final JavaType value;
  private final Constraints constraints;

  private JavaMapType(
      ClassName className, MapType mapType, JavaType key, JavaType value, Constraints constraints) {
    this.className = className;
    this.mapType = mapType;
    this.key = key;
    this.value = value;
    this.constraints = constraints;
  }

  public static JavaMapType wrap(MapType mapType, TypeMappings typeMappings) {
    final ClassName className =
        JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    final JavaType key = JavaType.wrap(mapType.getKey(), typeMappings);
    final JavaType value = JavaType.wrap(mapType.getValue(), typeMappings);
    return new JavaMapType(className, mapType, key, value, mapType.getConstraints());
  }

  @Override
  public Name getClassName() {
    return className.getClassName();
  }

  @Override
  public Type getType() {
    return mapType;
  }

  @Override
  public PList<Name> getAllQualifiedClassNames() {
    return PList.single(className.getQualifiedClassName())
        .concat(key.getAllQualifiedClassNames())
        .concat(value.getAllQualifiedClassNames());
  }

  @Override
  public Name getFullClassName() {
    return getFullAnnotatedClassName(AnnotationsCreator.empty()).getClassName();
  }

  @Override
  public AnnotatedClassName getFullAnnotatedClassName(AnnotationsCreator creator) {
    final AnnotationsCreator.Annotations annotations = creator.createForType(value);
    final String annotatedValueType =
        String.format("%s %s", annotations.getAnnotations(), value.getFullClassName()).trim();
    final Name fullClassName =
        className.getClassNameWithGenerics(
            key.getFullClassName(), Name.ofString(annotatedValueType));
    return AnnotatedClassName.fromClassNameAndImports(fullClassName, annotations.getImports());
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  public JavaType getKey() {
    return key;
  }

  public JavaType getValue() {
    return value;
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
    return onMapType.apply(this);
  }
}
