package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import lombok.Value;

@Value
public class MemberSchema {
  Name name;
  OpenApiSchema schema;
}
