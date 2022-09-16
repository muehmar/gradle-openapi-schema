package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.HashMap;
import org.junit.jupiter.api.Test;

class ObjectSchemaMapperTest extends BaseTypeMapperTest {
  @Test
  void mapSchema_when_uuidSchema_then_correctType() {
    final PojoName pojoName = PojoName.ofName(Name.ofString("Person"));
    final Name memberName = Name.ofString("Address");
    final Schema<?> schema = new ObjectSchema();
    final HashMap<String, Schema> properties = new HashMap<>();
    properties.put("street", new StringSchema());
    schema.setProperties(properties);

    final MemberSchemaMapResult result = run(pojoName, memberName, schema);

    final PojoName expectedPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
    assertEquals(ObjectType.ofName(expectedPojoName), result.getType());
    assertEquals(PList.single(new PojoSchema(expectedPojoName, schema)), result.getPojoSchemas());
  }
}
