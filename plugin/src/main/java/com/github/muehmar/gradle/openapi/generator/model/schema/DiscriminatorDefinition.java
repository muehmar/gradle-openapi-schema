package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiscriminatorDefinition {
  private DiscriminatorDefinition() {}

  public static Optional<UntypedDiscriminator> extractFromSchema(Schema<?> schema) {
    return Optional.ofNullable(schema.getDiscriminator())
        .filter(discriminator -> discriminator.getPropertyName() != null)
        .map(DiscriminatorDefinition::fromOpenApiDiscriminator);
  }

  private static UntypedDiscriminator fromOpenApiDiscriminator(
      io.swagger.v3.oas.models.media.Discriminator oasDiscriminator) {
    final Name propertyName = Name.ofString(oasDiscriminator.getPropertyName());
    final Optional<Map<String, Name>> pojoNameMapping =
        Optional.ofNullable(oasDiscriminator.getMapping())
            .map(DiscriminatorDefinition::fromOpenApiDiscriminatorMapping);
    return UntypedDiscriminator.fromPropertyName(propertyName).withMapping(pojoNameMapping);
  }

  private static Map<String, Name> fromOpenApiDiscriminatorMapping(Map<String, String> mapping) {
    return mapping.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> mapMappingReference(e.getValue())));
  }

  private static Name mapMappingReference(String reference) {
    return SchemaReference.fromRefString(reference).getSchemaName();
  }
}
