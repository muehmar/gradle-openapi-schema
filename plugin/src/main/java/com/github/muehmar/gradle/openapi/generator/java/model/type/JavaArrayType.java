package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaArrayType implements JavaType {
  private final ClassName className;
  private final ArrayType arrayType;
  private final JavaType itemType;

  private static final ClassName JAVA_CLASS_NAME = ClassNames.LIST;

  private JavaArrayType(ClassName className, ArrayType arrayType, JavaType itemType) {
    this.className = className;
    this.arrayType = arrayType;
    this.itemType = itemType;
  }

  public static JavaArrayType wrap(ArrayType arrayType, TypeMappings typeMappings) {
    final ClassName className =
        JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaArrayType(
        className, arrayType, JavaType.wrap(arrayType.getItemType(), typeMappings));
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
  public JavaType asPrimitive() {
    return this;
  }
}
