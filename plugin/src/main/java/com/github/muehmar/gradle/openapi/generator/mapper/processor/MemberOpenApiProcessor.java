package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.CompleteTypeMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.CompleteTypeMapperFactory;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;
import java.util.Optional;

public class MemberOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {
  private static final PList<String> SUPPORTED_MEMBER_SCHEMAS =
      PList.of("string", "integer", "number", "boolean");

  private static final CompleteTypeMapper COMPLETE_MAPPER = CompleteTypeMapperFactory.create();

  @Override
  public Optional<NewSchemaProcessResult> process(
      OpenApiPojo openApiPojo,
      PojoSettings pojoSettings,
      NewCompleteOpenApiProcessor completeOpenApiProcessor) {
    final String type = openApiPojo.getSchema().getType();
    if (Objects.nonNull(type) && SUPPORTED_MEMBER_SCHEMAS.exists(type::equals)) {
      return Optional.of(
          NewSchemaProcessResult.ofPojoMemberReference(
              processMemberSchema(openApiPojo.getPojoName(), openApiPojo.getSchema())));
    } else {
      return Optional.empty();
    }
  }

  private NewPojoMemberReference processMemberSchema(PojoName name, Schema<?> schema) {
    final TypeMapResult result =
        COMPLETE_MAPPER.map(PojoName.ofName(Name.of("Unused")), name.getName(), schema);

    return new NewPojoMemberReference(name, schema.getDescription(), result.getType());
  }
}
