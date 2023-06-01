package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import lombok.Value;

@Value
public class MemberSchema {
  Name name;
  OpenApiSchema schema;

  public static MemberSchema fromEntry(Map.Entry<String, Schema> entry) {
    return new MemberSchema(
        Name.ofString(entry.getKey()), OpenApiSchema.wrapSchema(entry.getValue()));
  }
}
