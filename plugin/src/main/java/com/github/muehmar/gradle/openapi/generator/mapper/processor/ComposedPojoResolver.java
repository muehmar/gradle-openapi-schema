package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import static com.github.muehmar.gradle.openapi.generator.model.ComposedPojo.CompositionType.ALL_OF;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;

class ComposedPojoResolver {
  private ComposedPojoResolver() {}

  /**
   * Converts {@link ComposedPojo}'s to actual {@link Pojo}'s. The resulting list contains all
   * supplied pojo's as well as the converted ones.
   */
  public static PList<Pojo> resolve(PList<ComposedPojo> composedPojos, PList<Pojo> pojos) {

    final PList<SchemaProcessResult> conversionResult =
        composedPojos
            .filter(composedPojo -> composedPojo.getType().equals(ALL_OF))
            .map(composedPojo -> resolveAllOf(composedPojo, pojos));

    final PList<Pojo> newPojos = conversionResult.flatMap(SchemaProcessResult::getPojos);
    final PList<ComposedPojo> unresolvedComposedPojos =
        conversionResult.flatMap(SchemaProcessResult::getComposedPojos);
    if (newPojos.isEmpty() && unresolvedComposedPojos.nonEmpty()) {
      throw new IllegalStateException(
          "Unable to resolve schemas of composed schema: " + unresolvedComposedPojos);
    } else if (unresolvedComposedPojos.isEmpty()) {
      return pojos.concat(newPojos);
    } else {
      return resolve(unresolvedComposedPojos, pojos.concat(newPojos));
    }
  }

  private static SchemaProcessResult resolveAllOf(ComposedPojo composedPojo, PList<Pojo> pojos) {
    final PList<PojoName> pojoNames = composedPojo.getPojoNames();
    final PList<PojoName> openApiPojoNames =
        composedPojo.getOpenApiPojos().map(OpenApiPojo::getPojoName);

    final PList<PojoName> allOfPojoNames = pojoNames.concat(openApiPojoNames);

    final PList<Pojo> allOfPojos =
        allOfPojoNames.flatMapOptional(
            allOfPojoName -> pojos.find(pojo -> pojo.getName().equalsIgnoreCase(allOfPojoName)));

    if (allOfPojos.size() != allOfPojoNames.size()) {
      return SchemaProcessResult.ofComposedPojo(composedPojo);
    }

    final PList<PojoMember> allPojoMembers =
        allOfPojos.flatMap(
            pojo ->
                pojo.fold(
                    ObjectPojo::getMembers, arrayPojo -> PList.empty(), enumPojo -> PList.empty()));

    final ObjectPojo objectPojo =
        ObjectPojo.of(composedPojo.getName(), composedPojo.getDescription(), allPojoMembers);
    return SchemaProcessResult.ofPojo(objectPojo);
  }
}
