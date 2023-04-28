package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.Jakarta2ValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.Jakarta3ValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.constraints.*;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationApi;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

public class ValidationGenerator {
  private ValidationGenerator() {}

  public static <T> Generator<T, PojoSettings> assertTrue(Function<T, String> message) {
    return Generator.<T, PojoSettings>emptyGen()
        .append((t, s, w) -> w.println("@AssertTrue(message = \"%s\")", message.apply(t)))
        .append(jakarta2Ref(Jakarta2ValidationRefs.ASSERT_TRUE))
        .append(jakarta3Ref(Jakarta3ValidationRefs.ASSERT_TRUE))
        .filter(Filters.isValidationEnabled());
  }

  public static <T> Generator<T, PojoSettings> assertFalse(Function<T, String> message) {
    return Generator.<T, PojoSettings>emptyGen()
        .append((t, s, w) -> w.println("@AssertFalse(message = \"%s\")", message.apply(t)))
        .append(jakarta2Ref(Jakarta2ValidationRefs.ASSERT_FALSE))
        .append(jakarta3Ref(Jakarta3ValidationRefs.ASSERT_FALSE))
        .filter(Filters.isValidationEnabled());
  }

  public static Generator<JavaPojoMember, PojoSettings> validationAnnotations() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(memberValidAnnotation())
        .append(notNullAnnotation())
        .append(emailAnnotation())
        .append(minAnnotation())
        .append(maxAnnotation())
        .append(decimalMinAnnotation())
        .append(decimalMaxAnnotation())
        .append(sizeAnnotationForMinAndMax())
        .append(sizeAnnotationForPropertyCount())
        .append(patternAnnotation())
        .filter(Filters.isValidationEnabled());
  }

  public static <T> Generator<T, PojoSettings> validAnnotation() {
    return Generator.<T, PojoSettings>ofWriterFunction(w -> w.println("@Valid"))
        .append(jakarta2Ref(Jakarta2ValidationRefs.VALID))
        .append(jakarta3Ref(Jakarta3ValidationRefs.VALID))
        .filter(Filters.isValidationEnabled());
  }

  public static Generator<JavaPojoMember, PojoSettings> memberValidAnnotation() {
    return ValidationGenerator.<JavaPojoMember>validAnnotation()
        .filter((field, settings) -> shouldValidateDeep(field.getJavaType()));
  }

  private static boolean shouldValidateDeep(JavaType javaType) {
    if (javaType instanceof JavaObjectType) {
      return true;
    } else if (javaType instanceof JavaArrayType) {
      return shouldValidateDeep(((JavaArrayType) javaType).getItemType());
    } else if (javaType instanceof JavaMapType) {
      return shouldValidateDeep(((JavaMapType) javaType).getKey())
          || shouldValidateDeep(((JavaMapType) javaType).getValue());
    }
    return false;
  }

  private static Generator<JavaPojoMember, PojoSettings> notNullAnnotation() {
    return Generator.<JavaPojoMember, PojoSettings>ofWriterFunction(w -> w.println("@NotNull"))
        .append(jakarta2Ref(Jakarta2ValidationRefs.NOT_NULL))
        .append(jakarta3Ref(Jakarta3ValidationRefs.NOT_NULL))
        .filter((field, settings) -> field.isRequired() && field.isNotNullable());
  }

  private static Generator<JavaPojoMember, PojoSettings> emailAnnotation() {
    final Generator<Email, PojoSettings> gen =
        Generator.<Email, PojoSettings>emptyGen()
            .append(w -> w.println("@Email"))
            .append(jakarta2Ref(Jakarta2ValidationRefs.EMAIL))
            .append(jakarta3Ref(Jakarta3ValidationRefs.EMAIL));
    return fromOptional(gen, Constraints::getEmail)
        .contraMap(JavaType::getConstraints)
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static Generator<JavaPojoMember, PojoSettings> minAnnotation() {
    final Generator<Min, PojoSettings> gen =
        Generator.<Min, PojoSettings>emptyGen()
            .append((min, s, w) -> w.println("@Min(value = %d)", min.getValue()))
            .append(jakarta2Ref(Jakarta2ValidationRefs.MIN))
            .append(jakarta3Ref(Jakarta3ValidationRefs.MIN));
    return fromOptional(gen, Constraints::getMin)
        .contraMap(JavaType::getConstraints)
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static Generator<JavaPojoMember, PojoSettings> maxAnnotation() {
    final Generator<Max, PojoSettings> gen =
        Generator.<Max, PojoSettings>emptyGen()
            .append((max, s, w) -> w.println("@Max(value = %d)", max.getValue()))
            .append(jakarta2Ref(Jakarta2ValidationRefs.MAX))
            .append(jakarta3Ref(Jakarta3ValidationRefs.MAX));
    return fromOptional(gen, Constraints::getMax)
        .contraMap(JavaType::getConstraints)
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static Generator<JavaPojoMember, PojoSettings> decimalMinAnnotation() {
    final Generator<DecimalMin, PojoSettings> gen =
        Generator.<DecimalMin, PojoSettings>emptyGen()
            .append(
                (decMin, s, w) ->
                    w.println(
                        "@DecimalMin(value = \"%s\", inclusive = %b)",
                        decMin.getValue(), decMin.isInclusiveMin()))
            .append(jakarta2Ref(Jakarta2ValidationRefs.DECIMAL_MIN))
            .append(jakarta3Ref(Jakarta3ValidationRefs.DECIMAL_MIN));
    return fromOptional(gen, Constraints::getDecimalMin)
        .contraMap(JavaType::getConstraints)
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static Generator<JavaPojoMember, PojoSettings> decimalMaxAnnotation() {
    final Generator<DecimalMax, PojoSettings> gen =
        Generator.<DecimalMax, PojoSettings>emptyGen()
            .append(
                (decMax, s, w) ->
                    w.println(
                        "@DecimalMax(value = \"%s\", inclusive = %b)",
                        decMax.getValue(), decMax.isInclusiveMax()))
            .append(jakarta2Ref(Jakarta2ValidationRefs.DECIMAL_MAX))
            .append(jakarta3Ref(Jakarta3ValidationRefs.DECIMAL_MAX));
    return fromOptional(gen, Constraints::getDecimalMax)
        .contraMap(JavaType::getConstraints)
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static Generator<JavaPojoMember, PojoSettings> sizeAnnotationForMinAndMax() {
    return fromOptional(
            sizeAnnotation().contraMap(SizeAnnotationValues::fromSize), Constraints::getSize)
        .contraMap(JavaType::getConstraints)
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static Generator<JavaPojoMember, PojoSettings> sizeAnnotationForPropertyCount() {
    return fromOptional(
            sizeAnnotation().contraMap(SizeAnnotationValues::fromPropertyCount),
            Constraints::getPropertyCount)
        .contraMap(JavaType::getConstraints)
        .filter(type -> type.getType().isMapType())
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static Generator<SizeAnnotationValues, PojoSettings> sizeAnnotation() {
    return Generator.<SizeAnnotationValues, PojoSettings>emptyGen()
        .append(
            (size, s, w) -> {
              final String minMax =
                  PList.of(
                          size.getMin().map(min -> String.format("min = %d", min)),
                          size.getMax().map(max -> String.format("max = %d", max)))
                      .flatMapOptional(Function.identity())
                      .mkString(", ");
              return w.println("@Size(%s)", minMax);
            })
        .append(jakarta2Ref(Jakarta2ValidationRefs.SIZE))
        .append(jakarta3Ref(Jakarta3ValidationRefs.SIZE));
  }

  public static Generator<Constraints, PojoSettings> minAnnotationForPropertyCount() {
    final Generator<Integer, PojoSettings> gen =
        Generator.<Integer, PojoSettings>emptyGen()
            .append((min, s, w) -> w.println("@Min(%s)", min))
            .append(jakarta2Ref(Jakarta2ValidationRefs.MIN))
            .append(jakarta3Ref(Jakarta3ValidationRefs.MIN));
    return fromOptional(
            gen, (Constraints c) -> c.getPropertyCount().flatMap(PropertyCount::getMinProperties))
        .filter(Filters.isValidationEnabled());
  }

  public static Generator<Constraints, PojoSettings> maxAnnotationForPropertyCount() {
    final Generator<Integer, PojoSettings> gen =
        Generator.<Integer, PojoSettings>emptyGen()
            .append((max, s, w) -> w.println("@Max(%s)", max))
            .append(jakarta2Ref(Jakarta2ValidationRefs.MAX))
            .append(jakarta3Ref(Jakarta3ValidationRefs.MAX));
    return fromOptional(
            gen, (Constraints c) -> c.getPropertyCount().flatMap(PropertyCount::getMaxProperties))
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojoMember, PojoSettings> patternAnnotation() {
    final Generator<Pattern, PojoSettings> gen =
        Generator.<Pattern, PojoSettings>emptyGen()
            .append(
                (pattern, s, w) ->
                    w.println(
                        "@Pattern(regexp=\"%s\")", pattern.getPatternEscaped(JavaEscaper::escape)))
            .append(jakarta2Ref(Jakarta2ValidationRefs.PATTERN))
            .append(jakarta3Ref(Jakarta3ValidationRefs.PATTERN));
    return fromOptional(gen, Constraints::getPattern)
        .contraMap(JavaType::getConstraints)
        .contraMap(JavaPojoMember::getJavaType);
  }

  private static <A> Generator<A, PojoSettings> jakarta2Ref(String ref) {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.ref(ref))
        .filter((ignore, s) -> s.getValidationApi().equals(ValidationApi.JAKARTA_2_0));
  }

  private static <A> Generator<A, PojoSettings> jakarta3Ref(String ref) {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.ref(ref))
        .filter((ignore, s) -> s.getValidationApi().equals(ValidationApi.JAKARTA_3_0));
  }

  private static <A, B, C> Generator<A, C> fromOptional(
      Generator<B, C> gen, Function<A, Optional<B>> getOptional) {
    return (a, c, w) -> getOptional.apply(a).map(b -> gen.generate(b, c, w)).orElse(w);
  }

  @Value
  private static class SizeAnnotationValues {
    Optional<Integer> min;
    Optional<Integer> max;

    static SizeAnnotationValues fromPropertyCount(PropertyCount propertyCount) {
      return new SizeAnnotationValues(
          propertyCount.getMinProperties(), propertyCount.getMaxProperties());
    }

    static SizeAnnotationValues fromSize(Size size) {
      return new SizeAnnotationValues(size.getMin(), size.getMax());
    }
  }
}
