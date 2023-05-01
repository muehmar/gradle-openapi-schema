package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;

public interface JavaType {
  /** Returns the name of the class of this type without any type parameter classes. */
  Name getClassName();

  Type getType();

  /**
   * Returns the qualified classnames used for this type, including the classes of possible type
   * parameters.s
   */
  PList<Name> getAllQualifiedClassNames();

  /** Returns the full classname, i.e. including possible type parameters. */
  Name getFullClassName();

  /**
   * Returns true in case this class is a java array (not to be confused with the openapi
   * array-type).
   */
  boolean isJavaArray();

  Constraints getConstraints();

  <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaNoType, T> onNoType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType);

  default PList<Name> getImports() {
    return getAllQualifiedClassNames()
        .filter(qualifiedClassName -> qualifiedClassName.contains("."))
        .filter(
            qualifiedClassName ->
                qualifiedClassName.startsNotWith(PackageNames.JAVA_LANG.asString()));
  }

  default PList<String> getImportsAsString() {
    return getImports().map(Name::asString);
  }

  static JavaType wrap(Type type, TypeMappings typeMappings) {
    return type.fold(
        numericType -> JavaNumericType.wrap(numericType, typeMappings),
        numericType -> JavaIntegerType.wrap(numericType, typeMappings),
        stringType -> JavaStringType.wrap(stringType, typeMappings),
        arrayType -> JavaArrayType.wrap(arrayType, typeMappings),
        booleanType -> JavaBooleanType.wrap(typeMappings),
        JavaObjectType::wrap,
        JavaEnumType::wrap,
        mapType -> JavaMapType.wrap(mapType, typeMappings),
        noType -> JavaNoType.create());
  }
}
