package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojoWrapResultBuilder.fullJavaPojoWrapResultBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PojoPromotionResult;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

@PojoBuilder
@Value
public class JavaObjectPojoWrapResult {
  JavaObjectPojo defaultPojo;
  Optional<JavaObjectPojo> requestPojo;
  Optional<JavaObjectPojo> responsePojo;

  public JavaPojoWrapResult promote() {
    final PojoPromotionResult defaultPojoPromotionResult = defaultPojo.promoteAsRoot();
    final Optional<PojoPromotionResult> requestPojoPromotionResult =
        requestPojo.map(JavaObjectPojo::promoteAsRoot);
    final Optional<PojoPromotionResult> responsePojoPromotionResult =
        responsePojo.map(JavaObjectPojo::promoteAsRoot);

    final PList<JavaObjectPojo> auxiliaryPojos =
        PList.of(
                requestPojoPromotionResult.map(
                    PojoPromotionResult::getNewPojosWithPromotedPojoExcluded),
                responsePojoPromotionResult.map(
                    PojoPromotionResult::getNewPojosWithPromotedPojoExcluded))
            .flatMapOptional(Function.identity())
            .flatMap(pojos -> pojos)
            .concat(defaultPojoPromotionResult.getNewPojosWithPromotedPojoExcluded());

    return fullJavaPojoWrapResultBuilder()
        .defaultPojo(defaultPojoPromotionResult.getPromotedPojo())
        .auxiliaryPojos(auxiliaryPojos.map(p -> p))
        .requestPojo(requestPojoPromotionResult.map(PojoPromotionResult::getPromotedPojo))
        .responsePojo(responsePojoPromotionResult.map(PojoPromotionResult::getPromotedPojo))
        .build();
  }
}
