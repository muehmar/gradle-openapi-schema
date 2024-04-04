package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import java.util.Optional;

public class NullableRootPojoResolver {
  private NullableRootPojoResolver() {}

  public static PList<Pojo> resolve(PList<Pojo> pojos) {
    final PList<PojoName> nullablePojos =
        pojos.flatMapOptional(
            pojo ->
                pojo.fold(
                    objectPojo ->
                        Optional.of(objectPojo.getName().getPojoName())
                            .filter(ignore -> objectPojo.getNullability().isNullable()),
                    arrayPojo ->
                        Optional.of(arrayPojo.getName().getPojoName())
                            .filter(ignore -> arrayPojo.getNullability().isNullable()),
                    enumPojo -> Optional.empty()));

    return nullablePojos.foldLeft(
        pojos, (pojos_, nullablePojo) -> pojos_.map(pojo -> pojo.adjustNullablePojo(nullablePojo)));
  }
}
