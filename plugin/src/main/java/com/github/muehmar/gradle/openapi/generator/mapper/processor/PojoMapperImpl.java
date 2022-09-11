package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.Optional;

public class PojoMapperImpl implements PojoMapper {

  private static final CompleteOpenApiProcessor COMPLETE_OPEN_API_PROCESSOR =
      new ArrayOpenApiProcessor()
          .or(new ObjectOpenApiProcessor())
          .or(new ComposedOpenApiProcessor())
          .or(new EnumOpenApiProcessor())
          .orLast(new MemberOpenApiProcessor());

  private PojoMapperImpl() {}

  public static PojoMapper create() {
    return new PojoMapperImpl();
  }

  @Override
  public PList<Pojo> fromSchemas(PList<OpenApiPojo> openApiPojos) {
    final PList<SchemaProcessResult> processResults =
        openApiPojos.map(COMPLETE_OPEN_API_PROCESSOR::process);

    final PList<Pojo> pojos = processResults.flatMap(SchemaProcessResult::getPojos);
    final PList<ComposedPojo> composedPojos =
        processResults.flatMap(SchemaProcessResult::getComposedPojos);
    final PList<PojoMemberReference> pojoMemberReferences =
        processResults.flatMap(SchemaProcessResult::getPojoMemberReferences);

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
