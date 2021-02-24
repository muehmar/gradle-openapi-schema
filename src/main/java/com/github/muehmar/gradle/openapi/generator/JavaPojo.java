package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.OpenApiSchemaGeneratorExtension;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaPojo extends Pojo {
  public JavaPojo(
      String key, String description, String suffix, List<PojoMember> members, boolean isArray) {
    super(key, description, suffix, members, isArray);
  }

  public static JavaPojo fromSchema(
      OpenApiSchemaGeneratorExtension config, String key, Schema<?> schema) {

    if (schema instanceof ArraySchema) {
      return fromArraySchema(config, key, (ArraySchema) schema);
    }

    final Map<String, Schema> properties = schema.getProperties();
    if (properties != null) {
      final List<PojoMember> members =
          properties.entrySet().stream()
              .map(
                  entry -> {
                    final Boolean required =
                        Optional.ofNullable(schema.getRequired())
                            .map(req -> req.stream().anyMatch(entry.getKey()::equals))
                            .orElse(false);
                    return JavaPojoMember.ofSchema(
                        config, entry.getValue(), entry.getKey(), !required);
                  })
              .collect(Collectors.toList());

      return new JavaPojo(key, schema.getDescription(), config.getSuffix(), members, false);
    }

    return new JavaPojo(
        key, schema.getDescription(), config.getSuffix(), Collections.emptyList(), false);
  }

  private static JavaPojo fromArraySchema(
      OpenApiSchemaGeneratorExtension config, String key, ArraySchema schema) {
    final JavaPojoMember member = JavaPojoMember.ofSchema(config, schema, "value", false);
    return new JavaPojo(
        key, schema.getDescription(), config.getSuffix(), Collections.singletonList(member), true);
  }
}
