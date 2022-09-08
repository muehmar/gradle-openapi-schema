package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.TIME;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.URI;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class StringSchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void mapThrowing_when_urlFormat_then_correctUrlJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("url");
    final TypeMapResult mappedSchema = run(schema);
    assertEquals(StringType.ofFormat(URL), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_uriFormat_then_correctUriJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("uri");
    final TypeMapResult mappedSchema = run(schema);
    assertEquals(StringType.ofFormat(URI), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_partialTimeFormat_then_localTimeTypeReturned() {
    final Schema<?> schema = new StringSchema().format("partial-time");
    final TypeMapResult mappedSchema = run(schema);
    assertEquals(StringType.ofFormat(TIME), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_patternDefined_then_patternConstraint() {
    final Schema<?> schema = new StringSchema().pattern("[A-Z]");

    final TypeMapResult mappedSchema = run(schema);
    final StringType exptectedType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("[A-Z]")));
    assertEquals(exptectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_minLengthDefined_then_minSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10);

    final TypeMapResult mappedSchema = run(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_maxLengthDefined_then_maxSizeConstraint() {
    final Schema<?> schema = new StringSchema().maxLength(33);

    final TypeMapResult mappedSchema = run(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMax(33))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_minAndMaxLengthDefined_then_fullSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10).maxLength(33);

    final TypeMapResult mappedSchema = run(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.of(10, 33))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void mapThrowing_when_enumItems_then_enumType() {
    final Schema<?> schema = new StringSchema()._enum(Arrays.asList("User", "Visitor"));

    final TypeMapResult mappedSchema = run(schema);
    assertEquals(
        EnumType.ofNameAndMembers(Name.ofString("PojoMemberNameEnum"), PList.of("User", "Visitor")),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
