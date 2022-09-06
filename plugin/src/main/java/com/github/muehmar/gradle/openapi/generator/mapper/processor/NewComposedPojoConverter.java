package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Optional;
import java.util.function.Function;

class NewComposedPojoConverter {
  private NewComposedPojoConverter() {}

  /**
   * Converts {@link ComposedPojo}'s to actual {@link Pojo}'s. The resulting list contains all
   * supplied pojo's as well as the converted ones.
   */
  public static PList<NewPojo> convert(PList<ComposedPojo> composedPojos, PList<NewPojo> pojos) {

    final PList<NewSchemaProcessResult> conversionResult =
        composedPojos
            .filter(
                composedPojo -> composedPojo.getType().equals(ComposedPojo.CompositionType.ALL_OF))
            .map(
                composedPojo -> {
                  final PList<PojoName> pojoNames = composedPojo.getPojoNames();
                  final PList<PojoName> openApiPojoNames =
                      composedPojo.getOpenApiPojos().map(OpenApiPojo::getPojoName);

                  final PList<Optional<NewPojo>> foundPojos =
                      pojoNames
                          .concat(openApiPojoNames)
                          .map(
                              name ->
                                  pojos.find(
                                      pojo ->
                                          pojo.getName()
                                              .asString()
                                              .equalsIgnoreCase(name.asString())));
                  if (foundPojos.exists(p -> !p.isPresent())) {
                    return NewSchemaProcessResult.ofComposedPojo(composedPojo);
                  } else {
                    final PList<NewPojoMember> members =
                        foundPojos
                            .flatMapOptional(Function.identity())
                            .flatMap(
                                pojo ->
                                    pojo.fold(
                                        ObjectPojo::getMembers,
                                        arrayPojo -> PList.empty(),
                                        enumPojo -> PList.empty()));
                    final ObjectPojo objectPojo =
                        ObjectPojo.of(
                            composedPojo.getName(), composedPojo.getDescription(), members);
                    return NewSchemaProcessResult.ofPojo(objectPojo);
                  }
                });

    final PList<NewPojo> newPojos = conversionResult.flatMap(NewSchemaProcessResult::getPojos);
    final PList<ComposedPojo> unconvertedComposedPojos =
        conversionResult.flatMap(NewSchemaProcessResult::getComposedPojos);
    if (newPojos.isEmpty() && unconvertedComposedPojos.nonEmpty()) {
      throw new IllegalStateException(
          "Unable to resolve schemas of composed schema: " + unconvertedComposedPojos);
    } else if (unconvertedComposedPojos.isEmpty()) {
      return pojos.concat(newPojos);
    } else {
      return convert(unconvertedComposedPojos, pojos.concat(newPojos));
    }
  }
}
