package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.NewPojoMapper;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.Optional;

public class PojoMapperImpl implements NewPojoMapper {

  private static final NewCompleteOpenApiProcessor COMPLETE_OPEN_API_PROCESSOR =
      new ArrayOpenApiProcessor()
          .or(new ObjectOpenApiProcessor())
          .or(new ComposedOpenApiProcessor())
          .or(new EnumOpenApiProcessor())
          .orLast(new MemberOpenApiProcessor());

  private PojoMapperImpl() {}

  public static NewPojoMapper create() {
    return new PojoMapperImpl();
  }

  @Override
  public PList<NewPojo> fromSchemas(PList<OpenApiPojo> openApiPojos) {
    final PList<NewSchemaProcessResult> processResults =
        openApiPojos.map(COMPLETE_OPEN_API_PROCESSOR::process);

    final PList<NewPojo> pojos = processResults.flatMap(NewSchemaProcessResult::getPojos);
    final PList<ComposedPojo> composedPojos =
        processResults.flatMap(NewSchemaProcessResult::getComposedPojos);
    final PList<NewPojoMemberReference> pojoMemberReferences =
        processResults.flatMap(NewSchemaProcessResult::getPojoMemberReferences);

    return Optional.of(pojos)
        .map(p -> ComposedPojoResolver.resolve(composedPojos, pojos))
        .map(p -> inlineMemberReferences(p, pojoMemberReferences))
        .map(this::addEnumDescription)
        .orElse(PList.empty());
  }

  private PList<NewPojo> inlineMemberReferences(
      PList<NewPojo> inputPojos, PList<NewPojoMemberReference> pojoMemberReferences) {
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

  private PList<NewPojo> addEnumDescription(PList<NewPojo> inputPojos) {
    return inputPojos
        .flatMapOptional(NewPojo::asEnumPojo)
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
