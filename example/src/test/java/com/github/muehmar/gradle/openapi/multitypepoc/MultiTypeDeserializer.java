package com.github.muehmar.gradle.openapi.multitypepoc;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MultiTypeDeserializer extends StdDeserializer<MultiType> {
  protected MultiTypeDeserializer() {
    super(MultiType.class);
  }

  @Override
  public MultiType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

    final JsonNode node = p.getCodec().readTree(p);

    if (node.isNull()) {
      return MultiType.fromNull();
    }

    if (node.isFloat()) {
      return MultiType.fromFloat(node.floatValue());
    }

    if (node.isDouble()) {
      final double doubleValue = node.asDouble();
      final float floatValue = node.floatValue();
      if (doubleValue == floatValue) {
        return MultiType.fromFloat(floatValue);
      }
      return MultiType.fromDouble(node.asDouble());
    }

    if (node.canConvertToInt()) {
      return MultiType.fromInt(node.asInt());
    }

    if (node.canConvertToLong()) {
      return MultiType.fromLong(node.asLong());
    }

    if (node.isTextual()) {
      return MultiType.fromString(node.asText());
    }

    if (node.isBoolean()) {
      return MultiType.fromBoolean(node.asBoolean());
    }

    if (node.isArray()) {
      final Iterator<JsonNode> elements = node.elements();
      final ArrayList<String> list = new ArrayList<>();
      for (JsonNode e : (Iterable<JsonNode>) (() -> elements)) {
        list.add(p.getCodec().treeToValue(e, String.class));
      }
      return MultiType.fromList(list);
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
