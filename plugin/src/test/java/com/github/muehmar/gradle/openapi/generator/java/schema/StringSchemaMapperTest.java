package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class StringSchemaMapperTest {

  private static final StringSchemaMapper stringSchemaMapper = new StringSchemaMapper(null);

  @Test
  void mapSchema_when_urlFormat_then_correctUrlJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("url");
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, null, null);
    assertEquals(JavaTypes.URL, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_uriFormat_then_correctUriJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("uri");
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, null, null);
    assertEquals(JavaTypes.URI, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_partialTimeFormat_then_localTimeTypeReturned() {
    final Schema<?> schema = new StringSchema().format("partial-time");
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, null, null);
    assertEquals(JavaTypes.LOCAL_TIME, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_patternDefined_then_patternConstraint() {
    final Schema<?> schema = new StringSchema().pattern("[A-Z]");
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, null, false, false, PList.empty(), PList.empty());
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, pojoSettings, null);
    assertEquals(
        JavaTypes.STRING.withConstraints(Constraints.ofPattern(new Pattern("[A-Z]"))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_minLengthDefined_then_minSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10);
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, null, false, false, PList.empty(), PList.empty());
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, pojoSettings, null);
    assertEquals(
        JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_maxLengthDefined_then_maxSizeConstraint() {
    final Schema<?> schema = new StringSchema().maxLength(33);
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, null, false, false, PList.empty(), PList.empty());
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, pojoSettings, null);
    assertEquals(
        JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMax(33))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_minAndMaxLengthDefined_then_fullSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10).maxLength(33);
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, null, false, false, PList.empty(), PList.empty());
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, pojoSettings, null);
    assertEquals(
        JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.of(10, 33))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_enumItems_then_enumType() {
    final Schema<?> schema = new StringSchema()._enum(Arrays.asList("User", "Visitor"));
    final PojoSettings pojoSettings =
        new PojoSettings(null, null, null, false, false, PList.empty(), PList.empty());
    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, pojoSettings, null);
    assertEquals(JavaType.javaEnum(PList.of("User", "Visitor")), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapSchema_when_formatTypeMappingMatches_then_useUserDefinedType() {
    final Schema<?> schema = new StringSchema().format("userType");

    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping("userType", "UserType", "ch.user.type.package");
    final PojoSettings pojoSettings =
        new PojoSettings(
            null, null, null, false, false, PList.empty(), PList.single(formatTypeMapping));

    final MappedSchema<JavaType> mappedSchema =
        stringSchemaMapper.mapSchema(
            Name.of("pojoName"), Name.of("pojoMemberName"), schema, pojoSettings, null);
    assertEquals(
        JavaType.ofUserDefinedAndImport("UserType", "ch.user.type.package"),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
