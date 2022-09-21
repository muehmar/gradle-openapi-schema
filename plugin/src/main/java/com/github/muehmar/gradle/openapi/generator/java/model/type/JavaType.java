package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageNames;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;

public interface JavaType {
  Name getClassName();

  PList<Name> getAllQualifiedClassNames();

  Name getFullClassName();

  Constraints getConstraints();

  <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaNoType, T> onNoType,
      Function<JavaNumericType, T> onNumericType,
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
        stringType -> JavaStringType.wrap(stringType, typeMappings),
        arrayType -> JavaArrayType.wrap(arrayType, typeMappings),
        booleanType -> JavaBooleanType.wrap(typeMappings),
        JavaObjectType::wrap,
        JavaEnumType::wrap,
        mapType -> JavaMapType.wrap(mapType, typeMappings),
        noType -> JavaNoType.create());
  }
}
