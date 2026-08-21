package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Optional;
import lombok.Value;

@Value
public class FactoryMethodConversion {
  QualifiedClassName className;
  Name methodName;

  public static Optional<FactoryMethodConversion> fromString(
      QualifiedClassName className, String factoryMethodConversion) {
    final String[] parts = factoryMethodConversion.split("#");
    if (parts.length != 2 && factoryMethodConversion.contains("#")) {
      throw createInvalidFormatException(factoryMethodConversion);
    }
    if (parts.length < 2) {
      return Optional.empty();
    }

    try {
      final QualifiedClassName parsedClassName = QualifiedClassName.ofQualifiedClassName(parts[0]);
      final Name methodName = Name.ofString(parts[1]);
      if (not(parsedClassName.getPackageName().isPresent())
          && parsedClassName.getClassName().equals(className.getClassName())) {
        return Optional.of(new FactoryMethodConversion(className, methodName));
      }
      return Optional.of(new FactoryMethodConversion(parsedClassName, methodName));
    } catch (Exception e) {
      throw createInvalidFormatException(factoryMethodConversion);
    }
  }

  private static OpenApiGeneratorException createInvalidFormatException(
      String factoryMethodConversion) {
    return new OpenApiGeneratorException(
        "Invalid configuration for type conversion '%s'. Expected format for static factory methods: 'package.name.ClassName#methodName'.",
        factoryMethodConversion);
  }
}
