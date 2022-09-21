package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.Optional;

public class MapResultResolverImpl implements MapResultResolver {
  @Override
  public MapResult resolve(UnresolvedMapResult unresolvedMapResult) {
    final PList<Pojo> pojos = unresolvedMapResult.getPojos();
    final PList<ComposedPojo> composedPojos = unresolvedMapResult.getComposedPojos();
    final PList<PojoMemberReference> pojoMemberReferences =
        unresolvedMapResult.getPojoMemberReferences();

    final PList<Pojo> resolvedPojos =
        Optional.of(pojos)
            .map(p -> ComposedPojoResolver.resolve(composedPojos, pojos))
            .map(p -> inlineMemberReferences(p, pojoMemberReferences))
            .map(this::addEnumDescription)
            .orElse(PList.empty());
    return MapResult.of(resolvedPojos, unresolvedMapResult.getUsedSpecs());
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
