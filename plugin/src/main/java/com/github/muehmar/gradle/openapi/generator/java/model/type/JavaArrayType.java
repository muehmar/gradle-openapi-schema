package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Wraps a {@link ArrayType} but not to be confused with actual java arrays. */
@EqualsAndHashCode
@ToString
public class JavaArrayType implements JavaType {
  private final ClassName className;
  private final JavaType itemType;
  private final ArrayType arrayType;
  private final Constraints constraints;

  private static final ClassName JAVA_CLASS_NAME = ClassNames.LIST;

  private JavaArrayType(
      ClassName className, JavaType itemType, ArrayType arrayType, Constraints constraints) {
    this.className = className;
    this.itemType = itemType;
    this.arrayType = arrayType;
    this.constraints = constraints;
  }

  public static JavaArrayType wrap(ArrayType arrayType, TypeMappings typeMappings) {
    final ClassName className =
        JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaArrayType(
        className,
        JavaType.wrap(arrayType.getItemType(), typeMappings),
        arrayType,
        arrayType.getConstraints());
  }

  @Override
  public Name getClassName() {
    return className.getClassName();
  }

  @Override
  public Type getType() {
    return arrayType;
  }

  @Override
  public PList<Name> getAllQualifiedClassNames() {
    return PList.single(className.getQualifiedClassName())
        .concat(itemType.getAllQualifiedClassNames());
  }

  @Override
  public Name getFullClassName() {
    return getFullAnnotatedClassName(AnnotationsCreator.empty()).getClassName();
  }

  @Override
  public AnnotatedClassName getFullAnnotatedClassName(AnnotationsCreator creator) {
    final AnnotationsCreator.Annotations annotations = creator.createForType(itemType);
    final String annotatedType =
        String.format("%s %s", annotations.getAnnotations(), itemType.getFullClassName()).trim();
    final Name fullClassName = className.getClassNameWithGenerics(Name.ofString(annotatedType));
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

  public JavaType getItemType() {
    return itemType;
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
    return onArrayType.apply(this);
  }
}
