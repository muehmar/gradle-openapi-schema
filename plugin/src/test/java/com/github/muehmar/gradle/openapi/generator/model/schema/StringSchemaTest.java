package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.schema.MapToMemberTypeTestUtil.mapToMemberType;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.BINARY;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.DATE_TIME;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.TIME;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.URI;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.URL;
import static com.github.muehmar.gradle.openapi.generator.model.type.StringType.Format.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.BinarySchema;
import io.swagger.v3.oas.models.media.DateSchema;
import io.swagger.v3.oas.models.media.DateTimeSchema;
import io.swagger.v3.oas.models.media.EmailSchema;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.PasswordSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.media.UUIDSchema;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class StringSchemaTest {

  @Test
  void mapToMemberType_when_urlFormat_then_correctUrlJavaTypeReturned() {
    final Schema<?> schema = new io.swagger.v3.oas.models.media.StringSchema().format("url");
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    assertEquals(StringType.ofFormat(URL), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_uriFormat_then_correctUriJavaTypeReturned() {
    final Schema<?> schema = new StringSchema().format("uri");
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    assertEquals(StringType.ofFormat(URI), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_partialTimeFormat_then_localTimeTypeReturned() {
    final Schema<?> schema = new StringSchema().format("partial-time");
    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    assertEquals(StringType.ofFormat(TIME), mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_patternDefined_then_patternConstraint() {
    final Schema<?> schema = new StringSchema().pattern("[A-Z]");

    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    final StringType exptectedType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("[A-Z]")));
    assertEquals(exptectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_minLengthDefined_then_minSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10);

    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(10))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_maxLengthDefined_then_maxSizeConstraint() {
    final Schema<?> schema = new StringSchema().maxLength(33);

    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMax(33))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_minAndMaxLengthDefined_then_fullSizeConstraint() {
    final Schema<?> schema = new StringSchema().minLength(10).maxLength(33);

    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    assertEquals(
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.of(10, 33))),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_enumItems_then_enumType() {
    final Schema<?> schema = new StringSchema()._enum(Arrays.asList("User", "Visitor"));

    final MemberSchemaMapResult mappedSchema = mapToMemberType(schema);
    assertEquals(
        EnumType.ofNameAndMembers(Name.ofString("PojoMemberNameEnum"), PList.of("User", "Visitor")),
        mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void
      mapToMemberType_when_passwordSchemaWithPatternAndMinMaxLengthConstraints_then_correctJavaTypeAndConstraints() {
    final PasswordSchema passwordSchema = new PasswordSchema();
    passwordSchema.pattern("pattern").minLength(5).maxLength(50);
    final MemberSchemaMapResult result = mapToMemberType(passwordSchema);

    final StringType exptectedType =
        StringType.ofFormat(StringType.Format.PASSWORD)
            .withConstraints(
                Constraints.ofSize(Size.of(5, 50))
                    .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern"))));
    assertEquals(exptectedType, result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_uuidSchema_then_correctType() {
    final Schema<?> schema = new UUIDSchema();

    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(StringType.ofFormat(UUID), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_fileSchema_then_correctType() {
    final FileSchema schema = new FileSchema();

    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(StringType.ofFormat(BINARY), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_emailSchema_then_correctType() {
    final EmailSchema emailSchema = new EmailSchema();
    final MemberSchemaMapResult mappedSchema = mapToMemberType(emailSchema);
    final StringType expectedType =
        StringType.ofFormat(StringType.Format.EMAIL).withConstraints(Constraints.ofEmail());
    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void
      mapToMemberType_when_emailSchemaWithPatternAndMinMaxLengthConstraints_then_correctTypeAndConstraints() {
    final EmailSchema emailSchema = new EmailSchema();
    emailSchema.pattern("pattern").minLength(5).maxLength(50);
    final MemberSchemaMapResult mappedSchema = mapToMemberType(emailSchema);
    final StringType expectedType =
        StringType.ofFormat(StringType.Format.EMAIL)
            .withConstraints(
                Constraints.ofEmail()
                    .and(Constraints.ofSize(Size.of(5, 50)))
                    .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern"))));
    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(UnmappedItems.empty(), mappedSchema.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_dateTimeSchema_then_correctType() {
    final DateTimeSchema schema = new DateTimeSchema();

    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(StringType.ofFormat(DATE_TIME), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_fateSchema_then_correctType() {
    final DateSchema schema = new DateSchema();

    final MemberSchemaMapResult result = mapToMemberType(schema);

    assertEquals(StringType.ofFormat(DATE), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToMemberType_when_binarySchema_then_correctType() {
    final BinarySchema binarySchema = new BinarySchema();

    final MemberSchemaMapResult result = mapToMemberType(binarySchema);

    assertEquals(StringType.ofFormat(BINARY), result.getType());
    assertEquals(UnmappedItems.empty(), result.getUnmappedItems());
  }

  @Test
  void mapToPojo_when_stringSchema_then_correctPojoMemberReferenceMapped() {
    final StringSchema stringSchema = new StringSchema();
    stringSchema.setDescription("Test description");

    final PojoSchema pojoSchema =
        new PojoSchema(PojoName.ofNameAndSuffix("Text", "Dto"), stringSchema);

    // method call
    final MapContext mapContext = pojoSchema.mapToPojo();

    final UnresolvedMapResult unresolvedMapResult = mapContext.getUnresolvedMapResult();
    assertEquals(0, unresolvedMapResult.getPojos().size());
    assertEquals(1, unresolvedMapResult.getPojoMemberReferences().size());

    final PojoMemberReference expectedPojoMemberReference =
        new PojoMemberReference(
            pojoSchema.getPojoName(), stringSchema.getDescription(), StringType.noFormat());
    assertEquals(
        expectedPojoMemberReference, unresolvedMapResult.getPojoMemberReferences().apply(0));
    assertEquals(UnmappedItems.empty(), mapContext.getUnmappedItems());
  }
}
