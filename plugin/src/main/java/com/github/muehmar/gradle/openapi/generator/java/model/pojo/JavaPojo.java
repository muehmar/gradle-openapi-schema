package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
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
        enumPojo -> JavaPojoWrapResult.ofDefaultPojo(JavaEnumPojo.wrap(enumPojo)),
        composedPojo -> JavaComposedPojo.wrap(composedPojo, typeMappings));
  }

  JavaName getSchemaName();

  JavaIdentifier getClassName();

  String getDescription();

  PojoType getType();

  <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo);

  default PList<JavaPojoMember> getMembersOrEmpty() {
    return fold(
        javaArrayPojo -> PList.single(javaArrayPojo.getArrayPojoMember()),
        javaEnumPojo -> PList.empty(),
        JavaObjectPojo::getMembers,
        JavaComposedPojo::getMembers);
  }

  default boolean isEnum() {
    final Predicate<JavaPojo> isEnumPojo = JavaEnumPojo.class::isInstance;
    return fold(isEnumPojo::test, isEnumPojo::test, isEnumPojo::test, isEnumPojo::test);
  }

  default boolean isNotEnum() {
    return not(isEnum());
  }

  default boolean isArray() {
    final Predicate<JavaPojo> isArrayPojo = JavaArrayPojo.class::isInstance;
    return fold(isArrayPojo::test, isArrayPojo::test, isArrayPojo::test, isArrayPojo::test);
  }

  default boolean isObject() {
    return asObjectPojo().isPresent();
  }

  default Optional<JavaObjectPojo> asObjectPojo() {
    return fold(
        arrayPojo -> Optional.empty(),
        enumPojo -> Optional.empty(),
        Optional::of,
        composedPojo -> Optional.empty());
  }
}
