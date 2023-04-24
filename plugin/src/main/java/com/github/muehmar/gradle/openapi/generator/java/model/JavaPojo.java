package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaFreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import java.util.function.Predicate;

public interface JavaPojo {

  static JavaPojo wrap(Pojo pojo, TypeMappings typeMappings) {
    return pojo.fold(
        objectPojo -> JavaObjectPojo.wrap(objectPojo, typeMappings),
        arrayPojo -> JavaArrayPojo.wrap(arrayPojo, typeMappings),
        JavaEnumPojo::wrap,
        composedPojo -> JavaComposedPojo.wrap(composedPojo, typeMappings),
        JavaFreeFormPojo::wrap);
  }

  JavaName getSchemaName();

  JavaIdentifier getClassName();

  String getDescription();

  <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaFreeFormPojo, T> onFreeFormPojo);

  default PList<JavaPojoMember> getMembersOrEmpty() {
    return fold(
        javaArrayPojo -> PList.single(javaArrayPojo.getArrayPojoMember()),
        javaEnumPojo -> PList.empty(),
        JavaObjectPojo::getMembers,
        JavaComposedPojo::getMembers,
        freeFormPojo -> PList.single(freeFormPojo.getMember()));
  }

  default boolean isEnum() {
    final Predicate<JavaPojo> isEnumPojo = JavaEnumPojo.class::isInstance;
    return fold(
        isEnumPojo::test, isEnumPojo::test, isEnumPojo::test, isEnumPojo::test, isEnumPojo::test);
  }

  default boolean isNotEnum() {
    return not(isEnum());
  }

  default boolean isArray() {
    final Predicate<JavaPojo> isArrayPojo = JavaArrayPojo.class::isInstance;
    return fold(
        isArrayPojo::test,
        isArrayPojo::test,
        isArrayPojo::test,
        isArrayPojo::test,
        isArrayPojo::test);
  }

  default boolean isObject() {
    final Predicate<JavaPojo> isObjectPojo = JavaObjectPojo.class::isInstance;
    return fold(
        isObjectPojo::test,
        isObjectPojo::test,
        isObjectPojo::test,
        isObjectPojo::test,
        isObjectPojo::test);
  }
}
