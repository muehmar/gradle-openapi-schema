package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Pojo;
import com.github.muehmar.gradle.openapi.generator.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Optional;

public class JavaPojo extends Pojo {
  public JavaPojo(
      String key, String description, String suffix, PList<PojoMember> members, boolean isArray) {
    super(key, description, suffix, members, isArray);
  }

  public static JavaPojo fromSchema(String key, Schema<?> schema, PojoSettings pojoSettings) {

    if (schema instanceof ArraySchema) {
      return fromArraySchema(key, (ArraySchema) schema, pojoSettings);
    }

    final Map<String, Schema> properties = schema.getProperties();
    if (properties != null) {
      final PList<PojoMember> members =
          properties.entrySet().stream()
              .map(
                  entry -> {
                    final Boolean required =
                        Optional.ofNullable(schema.getRequired())
                            .map(req -> req.stream().anyMatch(entry.getKey()::equals))
                            .orElse(false);
                    return JavaPojoMember.ofSchema(
                        pojoSettings, entry.getValue(), entry.getKey(), !required);
                  })
              .collect(PList.collector());

      return new JavaPojo(key, schema.getDescription(), pojoSettings.getSuffix(), members, false);
    }

    return new JavaPojo(
        key, schema.getDescription(), pojoSettings.getSuffix(), PList.empty(), false);
  }

  private static JavaPojo fromArraySchema(
      String key, ArraySchema schema, PojoSettings pojoSettings) {
    final JavaPojoMember member = JavaPojoMember.ofSchema(pojoSettings, schema, "value", false);
    return new JavaPojo(
        key, schema.getDescription(), pojoSettings.getSuffix(), PList.single(member), true);
  }
}
