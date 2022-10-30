package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaArrayType implements JavaType {
  private final ClassName className;
  private final JavaType itemType;
  private final Constraints constraints;

  private static final ClassName JAVA_CLASS_NAME = ClassNames.LIST;

  private JavaArrayType(ClassName className, JavaType itemType, Constraints constraints) {
    this.className = className;
    this.itemType = itemType;
    this.constraints = constraints;
  }

  public static JavaArrayType wrap(ArrayType arrayType, TypeMappings typeMappings) {
    final ClassName className =
        JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaArrayType(
        className,
        JavaType.wrap(arrayType.getItemType(), typeMappings),
        arrayType.getConstraints());
  }

  @Override
  public Name getClassName() {
    return className.getClassName();
  }

  @Override
  public PList<Name> getAllQualifiedClassNames() {
    return PList.single(className.getQualifiedClassName())
        .concat(itemType.getAllQualifiedClassNames());
  }

  @Override
  public Name getFullClassName() {
    return className.getClassNameWithGenerics(itemType.getFullClassName());
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
