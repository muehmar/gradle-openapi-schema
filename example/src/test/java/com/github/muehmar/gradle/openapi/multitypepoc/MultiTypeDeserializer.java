package com.github.muehmar.gradle.openapi.multitypepoc;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.TextNode;
import java.io.IOException;

public class MultiTypeDeserializer extends StdDeserializer<MultiType> {
  protected MultiTypeDeserializer() {
    super(MultiType.class);
  }

  @Override
  public MultiType deserialize(JsonParser p, DeserializationContext ctxt)
      throws IOException, JacksonException {
    final TreeNode treeNode = p.readValueAsTree();

    try {
      if (treeNode instanceof BooleanNode) {
        final Boolean b = ((BooleanNode) treeNode).booleanValue();
        return MultiType.fromBoolean(b);
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    try {
      if (treeNode instanceof NumericNode) {
        final Long l = ((NumericNode) treeNode).asLong();
        return MultiType.fromLong(l);
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    try {
      if (treeNode instanceof TextNode) {
        final String s = ((TextNode) treeNode).textValue();
        return MultiType.fromString(s);
      }
    } catch (Exception e) {
      System.err.println(e);
    }

    throw new IllegalArgumentException("Could not deserialize MultiType");
  }
}
