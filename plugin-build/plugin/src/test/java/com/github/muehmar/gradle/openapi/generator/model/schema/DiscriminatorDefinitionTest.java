package com.github.muehmar.gradle.openapi.generator.model.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class DiscriminatorDefinitionTest {

  @Test
  void extractFromSchema_when_containsDiscriminator_then_mappedCorrectly() {
    final ComposedSchema composedSchema = new ComposedSchema();
    composedSchema.addOneOfItem(new Schema<>());
    final io.swagger.v3.oas.models.media.Discriminator discriminator =
        new io.swagger.v3.oas.models.media.Discriminator();
    composedSchema.setDiscriminator(discriminator);

    discriminator.propertyName("propXY");
    discriminator.mapping("hello", "#/components/schemas/World");
    discriminator.mapping("lorem", "#/components/schemas/Ipsum");

    // method call
    final Optional<UntypedDiscriminator> extractedDiscriminator =
        DiscriminatorDefinition.extractFromSchema(composedSchema);

    final HashMap<String, Name> mappings = new HashMap<>();
    mappings.put("hello", Name.ofString("World"));
    mappings.put("lorem", Name.ofString("Ipsum"));
    final UntypedDiscriminator expectedDiscriminator =
        UntypedDiscriminator.fromPropertyNameAndMapping(Name.ofString("propXY"), mappings);

    assertEquals(Optional.of(expectedDiscriminator), extractedDiscriminator);
  }
}
