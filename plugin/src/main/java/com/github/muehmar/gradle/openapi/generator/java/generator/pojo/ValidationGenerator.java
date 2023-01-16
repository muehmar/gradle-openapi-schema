package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ValidationGenerator {
  private ValidationGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> assertTrue(
      Function<JavaPojoMember, String> message) {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (field, settings, writer) ->
                writer.println(
                    String.format("@AssertTrue(message = \"%s\")", message.apply(field))))
        .append(w -> w.ref(JavaValidationRefs.ASSERT_TRUE))
        .filter(Filters.isValidationEnabled());
  }

  public static Generator<JavaPojoMember, PojoSettings> validationAnnotations() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(validAnnotation())
        .append(notNullAnnotation())
        .append(emailAnnotation())
        .append(minAnnotation())
        .append(maxAnnotation())
        .append(decimalMinAnnotation())
        .append(decimalMaxAnnotation())
        .append(sizeAnnotation())
        .append(patternAnnotation())
        .filter(Filters.isValidationEnabled());
  }

  public static Generator<JavaPojoMember, PojoSettings> validAnnotation() {
    return Generator.<JavaPojoMember, PojoSettings>ofWriterFunction(w -> w.println("@Valid"))
        .append(w -> w.ref(JavaValidationRefs.VALID))
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
        .append(w -> w.ref(JavaValidationRefs.NOT_NULL))
        .filter((field, settings) -> field.isRequired() && field.isNotNullable());
  }

  private static Generator<JavaPojoMember, PojoSettings> emailAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onEmailFn(
                        ignore -> w.println("@Email").ref(JavaValidationRefs.EMAIL))))
        .contraMap(field -> field.getJavaType().getConstraints());
  }

  private static Generator<JavaPojoMember, PojoSettings> minAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onMinFn(
                        min ->
                            w.println("@Min(value = %d)", min.getValue())
                                .ref(JavaValidationRefs.MIN))))
        .contraMap(field -> field.getJavaType().getConstraints());
  }

  private static Generator<JavaPojoMember, PojoSettings> maxAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onMaxFn(
                        max ->
                            w.println("@Max(value = %d)", max.getValue())
                                .ref(JavaValidationRefs.MAX))))
        .contraMap(field -> field.getJavaType().getConstraints());
  }

  private static Generator<JavaPojoMember, PojoSettings> decimalMinAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onDecimalMinFn(
                        decMin ->
                            w.println(
                                    "@DecimalMin(value = \"%s\", inclusive = %b)",
                                    decMin.getValue(), decMin.isInclusiveMin())
                                .ref(JavaValidationRefs.DECIMAL_MIN))))
        .contraMap(field -> field.getJavaType().getConstraints());
  }

  private static Generator<JavaPojoMember, PojoSettings> decimalMaxAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onDecimalMaxFn(
                        decMax ->
                            w.println(
                                    "@DecimalMax(value = \"%s\", inclusive = %b)",
                                    decMax.getValue(), decMax.isInclusiveMax())
                                .ref(JavaValidationRefs.DECIMAL_MAX))))
        .contraMap(field -> field.getJavaType().getConstraints());
  }

  private static Generator<JavaPojoMember, PojoSettings> sizeAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onSizeFn(
                        size -> {
                          final String minMax =
                              PList.of(
                                      size.getMin().map(min -> String.format("min = %d", min)),
                                      size.getMax().map(max -> String.format("max = %d", max)))
                                  .flatMapOptional(Function.identity())
                                  .mkString(", ");
                          return w.println("@Size(%s)", minMax).ref(JavaValidationRefs.SIZE);
                        })))
        .contraMap(field -> field.getJavaType().getConstraints());
  }

  public static Generator<Constraints, PojoSettings> minAnnotationForPropertyCount() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints
                        .getPropertyCount()
                        .flatMap(PropertyCount::getMinProperties)
                        .map(min -> w.println("@Min(%s)", min).ref(JavaValidationRefs.MIN))))
        .filter(Filters.isValidationEnabled());
  }

  public static Generator<Constraints, PojoSettings> maxAnnotationForPropertyCount() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints
                        .getPropertyCount()
                        .flatMap(PropertyCount::getMaxProperties)
                        .map(min -> w.println("@Max(%s)", min).ref(JavaValidationRefs.MAX))))
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaPojoMember, PojoSettings> patternAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onPatternFn(
                        pattern ->
                            w.println(
                                    "@Pattern(regexp=\"%s\")",
                                    pattern.getPatternEscaped(JavaEscaper::escape))
                                .ref(JavaValidationRefs.PATTERN))))
        .contraMap(field -> field.getJavaType().getConstraints());
  }

  private static Generator<Constraints, PojoSettings> wrap(
      BiFunction<Constraints, Writer, Optional<Writer>> writeConstraints) {
    return (constraints, settings, writer) ->
        writeConstraints.apply(constraints, writer).orElse(writer);
  }
}
