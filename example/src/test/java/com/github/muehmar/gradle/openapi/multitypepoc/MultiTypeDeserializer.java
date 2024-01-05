package com.github.muehmar.gradle.openapi.multitypepoc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class MultiTypeDeserializer extends StdDeserializer<MultiType> {
  protected MultiTypeDeserializer() {
    super(MultiType.class);
  }

  @Override
  public MultiType deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException {

    final JsonNode node = p.getCodec().readTree(p);

    if (node.canConvertToLong()) {
      return MultiType.fromLong(node.asLong());
    } else if (node.isTextual()) {
      return MultiType.fromString(node.asText());
    } else if (node.isBoolean()) {
      return MultiType.fromBoolean(node.asBoolean());
    }

    // Parse objects
    try {
      final SuperObject superObject = p.getCodec().treeToValue(node, SuperObject.class);
      return MultiType.fromObject(superObject);
    } catch (Exception e) {
      // Try next object
    }

    throw new IllegalArgumentException("Could not deserialize MultiType");
  }
}
