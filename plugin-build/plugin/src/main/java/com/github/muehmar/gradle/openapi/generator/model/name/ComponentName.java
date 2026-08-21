package com.github.muehmar.gradle.openapi.generator.model.name;

import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import lombok.Value;

/** Container holding a {@link PojoName} and a {@link SchemaName}. */
@Value
public class ComponentName {
  PojoName pojoName;
  SchemaName schemaName;

  public static ComponentName fromSchemaStringAndSuffix(String schemaString, String suffix) {
    final PojoName pojoName = PojoName.ofNameAndSuffix(schemaString, suffix);
    final SchemaName schemaName = SchemaName.ofString(schemaString);
    return new ComponentName(pojoName, schemaName);
  }

  public ComponentName deriveMemberSchemaName(Name pojoMemberName) {
    final PojoName derivedPojoName = pojoName.deriveMemberSchemaName(pojoMemberName);
    final Name derivedSchemaName = schemaName.asName().append(".").append(pojoMemberName);
    return new ComponentName(derivedPojoName, SchemaName.ofName(derivedSchemaName));
  }

  public ComponentName applyPojoMapping(PojoNameMapping pojoNameMapping) {
    return new ComponentName(pojoNameMapping.map(pojoName), schemaName);
  }
}
