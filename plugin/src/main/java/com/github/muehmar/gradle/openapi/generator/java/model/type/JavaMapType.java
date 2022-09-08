package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
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

  private JavaMapType(ClassName className, MapType mapType, JavaType key, JavaType value) {
    this.className = className;
    this.mapType = mapType;
    this.key = key;
    this.value = value;
  }

  public static JavaMapType wrap(MapType mapType, TypeMappings typeMappings) {
    final ClassName className =
        JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    final JavaType key = JavaType.wrap(mapType.getKey(), typeMappings);
    final JavaType value = JavaType.wrap(mapType.getValue(), typeMappings);
    return new JavaMapType(className, mapType, key, value);
  }

  @Override
  public Name getClassName() {
    return className.getClassName();
  }

  @Override
  public PList<Name> getAllQualifiedClassNames() {
    return PList.single(className.getQualifiedClassName())
        .concat(key.getAllQualifiedClassNames())
        .concat(value.getAllQualifiedClassNames());
  }

  @Override
  public Name getFullClassName() {
    return className.getClassNameWithGenerics(key.getFullClassName(), value.getFullClassName());
  }

  @Override
  public JavaType asPrimitive() {
    return this;
  }
}
