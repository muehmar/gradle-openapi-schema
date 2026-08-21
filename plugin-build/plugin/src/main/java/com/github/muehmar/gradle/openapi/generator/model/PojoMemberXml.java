package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.schema.ArraySchema;
import com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrapper;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import java.util.Optional;
import lombok.Value;

@Value
public class PojoMemberXml {
  Optional<String> name;
  Optional<Boolean> isAttribute;
  Optional<ArrayXml> arrayXml;

  public static PojoMemberXml noDefinition() {
    return new PojoMemberXml(Optional.empty(), Optional.empty(), Optional.empty());
  }

  public static PojoMemberXml fromSchema(SchemaWrapper wrapper) {
    final XML xml = wrapper.getSchema().getXml();
    if (xml == null) {
      return PojoMemberXml.noDefinition();
    }
    final Optional<String> name = Optional.ofNullable(xml.getName());
    final Optional<Boolean> isAttribute = Optional.ofNullable(xml.getAttribute());
    return new PojoMemberXml(name, isAttribute, ArrayXml.fromSchema(wrapper));
  }

  @Value
  public static class ArrayXml {
    Optional<String> wrapperName;
    Optional<Boolean> wrapped;
    Optional<String> itemName;

    private static Optional<ArrayXml> fromSchema(SchemaWrapper wrapper) {
      return ArraySchema.wrap(wrapper)
          .flatMap(
              arraySchema -> {
                final XML xml = wrapper.getSchema().getXml();
                if (xml == null) {
                  return Optional.empty();
                }
                final Optional<String> wrapperName = Optional.ofNullable(xml.getName());
                final Optional<Boolean> wrapped = Optional.ofNullable(xml.getWrapped());
                final Schema<?> itemSchema = arraySchema.getItemSchema().getDelegateSchema();
                final Optional<String> itemName =
                    Optional.ofNullable(itemSchema.getXml()).map(XML::getName);
                return Optional.of(new ArrayXml(wrapperName, wrapped, itemName));
              })
          .filter(ArrayXml::hasDefinition);
    }

    public boolean hasDefinition() {
      return wrapperName.isPresent() || wrapped.isPresent() || itemName.isPresent();
    }
  }
}
