package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.ComposedPropertiesGetter.composedPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.NO_VALIDATION;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator.GeneratorOption.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ALL_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ANY_OF_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ARRAY_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.ONE_OF_MEMBER;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Predicate;

public class GetterGenerator {
  private GetterGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> getterGenerator() {
    return membersGenerator().append(allOfGenerator()).append(oneOfAndAnyOfGenerator());
  }

  private static Generator<JavaPojoMember, PojoSettings> membersGenerator() {
    return singleGetterGenerator(STANDARD)
        .filter(m -> m.getType().equals(OBJECT_MEMBER) || m.getType().equals(ARRAY_VALUE));
  }

  private static Generator<JavaPojoMember, PojoSettings> allOfGenerator() {
    return singleGetterGenerator(NO_VALIDATION).filter(m -> m.getType().equals(ALL_OF_MEMBER));
  }

  private static Generator<JavaPojoMember, PojoSettings> oneOfAndAnyOfGenerator() {
    return composedPropertiesGetterGenerator()
        .filter(m -> m.getType().equals(ONE_OF_MEMBER) || m.getType().equals(ANY_OF_MEMBER));
  }

  public static Generator<JavaPojoMember, PojoSettings> singleGetterGenerator(
      GeneratorOption option) {
    return RequiredNotNullableGetter.requiredNotNullableGetterGenerator(option)
        .append(RequiredNullableGetter.requiredNullableGetterGenerator(option))
        .append(OptionalNotNullableGetter.optionalNotNullableGetterGenerator(option))
        .append(OptionalNullableGetter.optionalNullableGetterGenerator(option));
  }

  public enum GeneratorOption {
    STANDARD,
    NO_VALIDATION;

    public <T> Predicate<T> validationFilter() {
      return ignore -> this.equals(STANDARD);
    }
  }
}
