package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

@Value
@PojoBuilder
public class JavaPojoWrapResult {
  JavaPojo defaultPojo;
  Optional<JavaPojo> requestPojo;
  Optional<JavaPojo> responsePojo;

  public static JavaPojoWrapResult ofDefaultPojo(JavaPojo defaultPojo) {
    return JavaPojoWrapResultBuilder.create().defaultPojo(defaultPojo).build();
  }

  public JavaPojo getTypeOrDefault(PojoType type) {
    return Optionals.or(
            requestPojo.filter(p -> p.getType().equals(type)),
            responsePojo.filter(p -> p.getType().equals(type)))
        .orElse(defaultPojo);
  }

  public NonEmptyList<JavaPojo> asList() {
    return NonEmptyList.single(defaultPojo)
        .concat(PList.fromOptional(requestPojo))
        .concat(PList.fromOptional(responsePojo));
  }
}
