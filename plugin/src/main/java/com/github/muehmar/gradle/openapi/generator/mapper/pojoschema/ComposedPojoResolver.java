package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import static com.github.muehmar.gradle.openapi.generator.model.ComposedPojo.CompositionType.ALL_OF;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;

public class ComposedPojoResolver {
  private ComposedPojoResolver() {}

  /**
   * Converts {@link ComposedPojo}'s to actual {@link Pojo}'s. The resulting list contains all
   * supplied pojo's as well as the converted ones.
   */
  public static PList<Pojo> resolve(PList<ComposedPojo> composedPojos, PList<Pojo> pojos) {

    final PList<PojoSchemaMapResult> conversionResult =
        composedPojos
            .filter(composedPojo -> composedPojo.getType().equals(ALL_OF))
            .map(composedPojo -> resolveAllOf(composedPojo, pojos));

    final PList<Pojo> newPojos = conversionResult.flatMap(PojoSchemaMapResult::getPojos);
    final PList<ComposedPojo> unresolvedComposedPojos =
        conversionResult.flatMap(PojoSchemaMapResult::getComposedPojos);
    if (newPojos.isEmpty() && unresolvedComposedPojos.nonEmpty()) {
      throw new IllegalStateException(
          "Unable to resolve schemas of composed schema: " + unresolvedComposedPojos);
    } else if (unresolvedComposedPojos.isEmpty()) {
      return pojos.concat(newPojos);
    } else {
      return resolve(unresolvedComposedPojos, pojos.concat(newPojos));
    }
  }

  private static PojoSchemaMapResult resolveAllOf(ComposedPojo composedPojo, PList<Pojo> pojos) {
    final PList<PojoName> pojoNames = composedPojo.getPojoNames();
    final PList<PojoName> openApiPojoNames =
        composedPojo.getPojoSchemas().map(PojoSchema::getPojoName);

    final PList<PojoName> allOfPojoNames = pojoNames.concat(openApiPojoNames);

    final PList<Pojo> allOfPojos =
        allOfPojoNames.flatMapOptional(
            allOfPojoName -> pojos.find(pojo -> pojo.getName().equalsIgnoreCase(allOfPojoName)));

    if (allOfPojos.size() != allOfPojoNames.size()) {
      return PojoSchemaMapResult.ofComposedPojo(composedPojo);
    }

    final PList<PojoMember> allPojoMembers =
        allOfPojos.flatMap(
            pojo ->
                pojo.fold(
                    ObjectPojo::getMembers, arrayPojo -> PList.empty(), enumPojo -> PList.empty()));

    final ObjectPojo objectPojo =
        ObjectPojo.of(composedPojo.getName(), composedPojo.getDescription(), allPojoMembers);
    return PojoSchemaMapResult.ofPojo(objectPojo);
  }
}
