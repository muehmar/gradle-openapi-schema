package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> generator(
      RequiredNullableGetterGen requiredNullableGetterGen,
      RequiredNotNullableGetterGen requiredNotNullableGetterGen,
      OptionalNullableGetterGen optionalNullableGetterGen,
      OptionalNotNullableGetterGen optionalNotNullableGetterGen) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendConditionally(JavaPojoMember::isRequiredAndNullable, requiredNullableGetterGen)
        .appendConditionally(JavaPojoMember::isRequiredAndNotNullable, requiredNotNullableGetterGen)
        .appendConditionally(JavaPojoMember::isOptionalAndNullable, optionalNullableGetterGen)
        .appendConditionally(
            JavaPojoMember::isOptionalAndNotNullable, optionalNotNullableGetterGen);
  }

  public interface RequiredNullableGetterGen extends Generator<JavaPojoMember, PojoSettings> {
    static RequiredNullableGetterGen wrap(Generator<JavaPojoMember, PojoSettings> gen) {
      return gen::generate;
    }
  }

  public interface RequiredNotNullableGetterGen extends Generator<JavaPojoMember, PojoSettings> {
    static RequiredNotNullableGetterGen wrap(Generator<JavaPojoMember, PojoSettings> gen) {
      return gen::generate;
    }
  }

  public interface OptionalNullableGetterGen extends Generator<JavaPojoMember, PojoSettings> {
    static OptionalNullableGetterGen wrap(Generator<JavaPojoMember, PojoSettings> gen) {
      return gen::generate;
    }
  }

  public interface OptionalNotNullableGetterGen extends Generator<JavaPojoMember, PojoSettings> {
    static OptionalNotNullableGetterGen wrap(Generator<JavaPojoMember, PojoSettings> gen) {
      return gen::generate;
    }
  }
}
