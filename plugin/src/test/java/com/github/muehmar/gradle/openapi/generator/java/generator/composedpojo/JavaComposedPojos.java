package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ONE_OF;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class JavaComposedPojos {
  private JavaComposedPojos() {}

  public static Stream<Arguments> variants() {
    return PList.of(ComposedPojo.CompositionType.values())
        .map(JavaPojos::composedPojo)
        .cons(JavaPojos.composedPojoWithDiscriminator(ONE_OF))
        .cons(JavaPojos.composedPojoWithDiscriminatorMapping(ONE_OF))
        .map(pojo -> Arguments.arguments(pojo.getCompositionType(), pojo.getDiscriminator(), pojo))
        .toStream();
  }
}
