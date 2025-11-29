package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import java.util.Optional;
import lombok.Value;

@Value
public class ConstructorConversion {
  QualifiedClassName referenceClassName;
  Optional<QualifiedClassName> constructorClassName;
  boolean isGenericClass;

  public static ConstructorConversion conversionForSet() {
    return new ConstructorConversion(
        QualifiedClassNames.SET, Optional.of(QualifiedClassNames.HASH_SET), true);
  }

  public static ConstructorConversion conversionForList() {
    return new ConstructorConversion(
        QualifiedClassNames.LIST, Optional.of(QualifiedClassNames.ARRAY_LIST), true);
  }

  public ConstructorConversion replaceClassName(
      QualifiedClassName currentClassName, QualifiedClassName newClassName) {
    return new ConstructorConversion(
        referenceClassName.replaceIfEquals(currentClassName, newClassName),
        constructorClassName.map(cc -> cc.replaceIfEquals(currentClassName, newClassName)),
        isGenericClass);
  }
}
