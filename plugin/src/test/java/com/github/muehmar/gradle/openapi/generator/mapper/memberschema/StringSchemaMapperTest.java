package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.TIME;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.URI;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
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
    final MemberSchemaMapResult mappedSchema = run(schema);
    assertEquals(StringType.ofFormat(URL), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_uriFormat_then_correctUriJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("uri");
    final MemberSchemaMapResult mappedSchema = run(schema);
    assertEquals(StringType.ofFormat(URI), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_partialTimeFormat_then_localTimeTypeReturned() {
    final Schema<?> schema = new StringSchema().format("partial-time");
    final MemberSchemaMapResult mappedSchema = run(schema);
    assertEquals(StringType.ofFormat(TIME), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_patternDefined_then_patternConstraint() {
    final Schema<?> schema = new StringSchema().pattern("[A-Z]");

    final MemberSchemaMapResult mappedSchema = run(schema);
    final StringType exptectedType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("[A-Z]")));
    assertEquals(exptectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_minLengthDefined_then_minSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10);

    final MemberSchemaMapResult mappedSchema = run(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_maxLengthDefined_then_maxSizeConstraint() {
    final Schema<?> schema = new StringSchema().maxLength(33);

    final MemberSchemaMapResult mappedSchema = run(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMax(33))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_minAndMaxLengthDefined_then_fullSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10).maxLength(33);

    final MemberSchemaMapResult mappedSchema = run(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.of(10, 33))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }

  @Test
  void mapThrowing_when_enumItems_then_enumType() {
    final Schema<?> schema = new StringSchema()._enum(Arrays.asList("User", "Visitor"));

    final MemberSchemaMapResult mappedSchema = run(schema);
    assertEquals(
        EnumType.ofNameAndMembers(Name.ofString("PojoMemberNameEnum"), PList.of("User", "Visitor")),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getPojoSchemas());
  }
}
