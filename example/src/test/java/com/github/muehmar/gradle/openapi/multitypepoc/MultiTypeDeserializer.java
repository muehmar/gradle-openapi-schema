package com.github.muehmar.gradle.openapi.multitypepoc;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import java.io.IOException;

public class MultiTypeDeserializer extends StdDeserializer<MultiType> {
  protected MultiTypeDeserializer() {
    super(MultiType.class);
  }

  @Override
  public MultiType deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {

    // Necessary if objects need to be parsed
    final TokenBuffer tokenBuffer = ctxt.bufferAsCopyOfValue(p);

    final TreeNode treeNode = p.readValueAsTree();

    if (treeNode instanceof ValueNode) {
      final ValueNode valueNode = (ValueNode) treeNode;

      if (valueNode instanceof NumericNode) {
        final NumericNode numericNode = (NumericNode) valueNode;

        if (numericNode instanceof IntNode) {
          // Parse integers
        }

        if (numericNode instanceof LongNode) {
          return MultiType.fromLong(tokenBuffer.asParser().readValueAs(Long.class));
        }

        if (numericNode instanceof FloatNode) {
          // Parse float
        }

        if (numericNode instanceof DoubleNode) {
          // Parse double
        }

        // Parse directly into integer/long or float/double if possible
        try {
          return MultiType.fromLong(tokenBuffer.asParser().readValueAs(Long.class));
        } catch (Exception e) {
          // Try next
        }
      }

      if (valueNode instanceof BinaryNode) {
        // Parse binary?
      }

      if (valueNode instanceof TextNode) {
        return MultiType.fromString(tokenBuffer.asParser().readValueAs(String.class));
      }

      if (valueNode instanceof BooleanNode) {
        return MultiType.fromBoolean(tokenBuffer.asParser().readValueAs(Boolean.class));
      }
    }

    if (treeNode instanceof ObjectNode) {
      // Parse objects
      try {
        // tokenBuffer.asParser().readValueAs(ObjectDto.class);
      } catch (Exception e) {
        // Try next object
      }
    }

    throw new IllegalArgumentException("Could not deserialize MultiType");
  }
}
