package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageNames;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;

public interface JavaType {
  QualifiedClassName getQualifiedClassName();

  Type getType();

  /**
   * Returns the qualified classnames used for this type, including the classes of possible type
   * parameters.s
   */
  PList<Name> getAllQualifiedClassNames();

  /** Returns the full classname, i.e. including possible type parameters. */
  Name getFullClassName();

  /**
   * Returns the full classname, where the 'value type' for generic container types like lists and
   * maps is annotated using the given {@link AnnotationsCreator}.
   */
  AnnotatedClassName getFullAnnotatedClassName(AnnotationsCreator creator);

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
      Function<JavaAnyType, T> onAnyType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType);

  default PList<Name> getImports() {
    return getAllQualifiedClassNames()
        .filter(
            qualifiedClassName ->
                not(qualifiedClassName.equals(qualifiedClassName.startUpperCase())))
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
        noType -> JavaAnyType.create());
  }
}
