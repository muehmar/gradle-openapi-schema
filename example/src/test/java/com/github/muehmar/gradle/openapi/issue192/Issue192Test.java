package com.github.muehmar.gradle.openapi.issue192;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class Issue192Test {
  @Test
  void rootObject1DtoBuilder_when_getMethodsOfStage_then_noSinglePropertySettersPresent() {

    final Class<?> builderStageClass = RootObject1Dto.rootObject1DtoBuilder().getClass();

    final Set<String> methodNames =
        Arrays.stream(builderStageClass.getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());

    assertEquals(Collections.singleton("setAllOfObjectWithOneOfDto"), methodNames);
  }

  @Test
  void rootObject2DtoBuilder_when_getMethodsOfStage_then_noSinglePropertySettersPresent() {

    final Class<?> builderStageClass = RootObject2Dto.rootObject2DtoBuilder().getClass();

    final Set<String> methodNames =
        Arrays.stream(builderStageClass.getDeclaredMethods())
            .map(Method::getName)
            .collect(Collectors.toSet());

    assertEquals(Collections.singleton("setAllOfObjectWithAnyOfDto"), methodNames);
  }
}
