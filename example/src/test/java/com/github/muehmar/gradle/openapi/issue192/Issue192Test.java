package com.github.muehmar.gradle.openapi.issue192;

import static com.github.muehmar.gradle.openapi.util.MethodList.listMethodNames;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Issue192Test {
  @Test
  void rootObject1DtoBuilder_when_getMethodsOfStage_then_noSinglePropertySettersPresent() {

    final Class<?> builderStageClass = RootObject1Dto.rootObject1DtoBuilder().getClass();

    assertEquals("setAllOfObjectWithOneOfDto", listMethodNames(builderStageClass));
  }

  @Test
  void rootObject2DtoBuilder_when_getMethodsOfStage_then_noSinglePropertySettersPresent() {

    final Class<?> builderStageClass = RootObject2Dto.rootObject2DtoBuilder().getClass();

    assertEquals("setAllOfObjectWithAnyOfDto", listMethodNames(builderStageClass));
  }
}
