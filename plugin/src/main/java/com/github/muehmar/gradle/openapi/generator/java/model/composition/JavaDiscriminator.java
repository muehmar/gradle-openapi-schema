package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.model.composition.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaDiscriminator {
  private final JavaName propertyName;
  private final JavaDiscriminatorType type;
  private final Optional<Map<String, Name>> mapping;

  JavaDiscriminator(
      JavaName propertyName, JavaDiscriminatorType type, Optional<Map<String, Name>> mapping) {
    this.propertyName = propertyName;
    this.type = type;
    this.mapping = mapping;
  }

  public static JavaDiscriminator wrap(Discriminator discriminator) {
    return new JavaDiscriminator(
        JavaName.fromName(discriminator.getPropertyName()),
        JavaDiscriminatorType.wrap(discriminator.getType()),
        discriminator.getMapping());
  }

  public JavaName getPropertyName() {
    return propertyName;
  }

  public String discriminatorPropertyToStringValue() {
    final String messageFormat = type.fold(stringType -> "%s", enumType -> "%s.getValue()");
    return String.format(messageFormat, propertyName);
  }

  public String getStringValueForSchemaName(Name schemaName) {
    return mapping.orElse(Collections.emptyMap()).entrySet().stream()
        .filter(e -> e.getValue().equals(schemaName))
        .findFirst()
        .map(Map.Entry::getKey)
        .orElse(schemaName.asString());
  }

  public <T> T getValueForSchemaName(
      Name schemaName, Function<String, T> onStringType, Function<JavaName, T> onEnumType) {
    final String stringValue = getStringValueForSchemaName(schemaName);

    return type.fold(
        stringType -> onStringType.apply(stringValue),
        enumType ->
            enumType
                .getMembers()
                .find(
                    enumConstantName -> enumConstantName.getOriginalConstant().equals(stringValue))
                .map(EnumConstantName::asJavaConstant)
                .map(onEnumType)
                .orElseThrow(
                    () ->
                        new OpenApiGeneratorException(
                            "Value '%s' is not a valid mapping for the discriminator %s for schema %s..",
                            stringValue, propertyName, schemaName)));
  }
}
