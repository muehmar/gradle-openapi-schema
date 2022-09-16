package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ArrayPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.CompletePojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ComposedPojoResolver;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ComposedPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.EnumPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.MemberPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ObjectPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.PojoSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import java.util.Optional;

class PojoMapperImpl implements PojoMapper {

  private static final CompletePojoSchemaMapper COMPLETE_POJO_SCHEMA_MAPPER =
      new ArrayPojoSchemaMapper()
          .or(new ObjectPojoSchemaMapper())
          .or(new ComposedPojoSchemaMapper())
          .or(new EnumPojoSchemaMapper())
          .orLast(new MemberPojoSchemaMapper());

  private PojoMapperImpl() {}

  public static PojoMapper create() {
    return new PojoMapperImpl();
  }

  @Override
  public PList<Pojo> fromSchemas(PList<PojoSchema> openApiPojos) {
    final PList<PojoSchemaMapResult> processResults =
        openApiPojos.map(COMPLETE_POJO_SCHEMA_MAPPER::process);

    final PList<Pojo> pojos = processResults.flatMap(PojoSchemaMapResult::getPojos);
    final PList<ComposedPojo> composedPojos =
        processResults.flatMap(PojoSchemaMapResult::getComposedPojos);
    final PList<PojoMemberReference> pojoMemberReferences =
        processResults.flatMap(PojoSchemaMapResult::getPojoMemberReferences);

    return Optional.of(pojos)
        .map(p -> ComposedPojoResolver.resolve(composedPojos, pojos))
        .map(p -> inlineMemberReferences(p, pojoMemberReferences))
        .map(this::addEnumDescription)
        .orElse(PList.empty());
  }

  private PList<Pojo> inlineMemberReferences(
      PList<Pojo> inputPojos, PList<PojoMemberReference> pojoMemberReferences) {
    return pojoMemberReferences.foldLeft(
        inputPojos,
        (pojos, memberReference) ->
            pojos.map(
                pojo ->
                    pojo.inlineObjectReference(
                        memberReference.getName(),
                        memberReference.getDescription(),
                        memberReference.getType())));
  }

  private PList<Pojo> addEnumDescription(PList<Pojo> inputPojos) {
    return inputPojos
        .flatMapOptional(Pojo::asEnumPojo)
        .foldLeft(
            inputPojos,
            (p, enumPojo) ->
                p.map(
                    pojo -> {
                      final PojoName enumName = enumPojo.getName();
                      return pojo.addObjectTypeDescription(enumName, enumPojo.getDescription());
                    }));
  }
}
