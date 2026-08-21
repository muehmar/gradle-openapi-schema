package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public interface JavaPojo {

  static JavaPojoWrapResult wrap(Pojo pojo, TypeMappings typeMappings) {
    return pojo.fold(
        objectPojo -> JavaObjectPojo.wrap(objectPojo, typeMappings),
        arrayPojo -> JavaPojoWrapResult.ofDefaultPojo(JavaArrayPojo.wrap(arrayPojo, typeMappings)),
        enumPojo -> JavaPojoWrapResult.ofDefaultPojo(JavaEnumPojo.wrap(enumPojo)));
  }

  /**
   * Returns the schema name in the specification. Might be different from the pojo/class name due
   * to custom name mapping.
   */
  JavaName getSchemaName();

  JavaName getClassName();

  String getDescription();

  PojoType getType();

  <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo);

  default boolean isEnum() {
    final Predicate<JavaPojo> isEnumPojo = JavaEnumPojo.class::isInstance;
    return fold(isEnumPojo::test, isEnumPojo::test, isEnumPojo::test);
  }

  default boolean isNotEnum() {
    return not(isEnum());
  }

  default boolean isArray() {
    final Predicate<JavaPojo> isArrayPojo = JavaArrayPojo.class::isInstance;
    return fold(isArrayPojo::test, isArrayPojo::test, isArrayPojo::test);
  }

  default boolean isObject() {
    return asObjectPojo().isPresent();
  }

  default Optional<JavaObjectPojo> asObjectPojo() {
    return fold(arrayPojo -> Optional.empty(), enumPojo -> Optional.empty(), Optional::of);
  }
}
