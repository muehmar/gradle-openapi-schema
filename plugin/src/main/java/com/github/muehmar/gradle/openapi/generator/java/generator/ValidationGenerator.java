package com.github.muehmar.gradle.openapi.generator.java.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ValidationGenerator {
  private ValidationGenerator() {}

  public static Generator<PojoMember, PojoSettings> assertTrue(
      Function<PojoMember, String> message) {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(
            (field, settings, writer) ->
                writer.println(String.format("@AssertTrue(\"%s\")", message.apply(field))))
        .append(w -> w.ref(JavaValidationRefs.ASSERT_TRUE))
        .filter(isValidationEnabled());
  }

  public static Generator<PojoMember, PojoSettings> validationAnnotations() {
    return Generator.<PojoMember, PojoSettings>emptyGen()
        .append(validAnnotation())
        .append(notNullAnnotation())
        .append(emailAnnotation())
        .append(minAnnotation())
        .append(maxAnnotation())
        .append(decimalMinAnnotation())
        .append(decimalMaxAnnotation())
        .append(sizeAnnotation())
        .append(patternAnnotation())
        .filter(isValidationEnabled());
  }

  private static Generator<PojoMember, PojoSettings> validAnnotation() {
    return Generator.<PojoMember, PojoSettings>ofWriterFunction(w -> w.println("@Valid"))
        .append(w -> w.ref(JavaValidationRefs.VALID))
        .filter((field, settings) -> field.getType().containsPojo());
  }

  private static Generator<PojoMember, PojoSettings> notNullAnnotation() {
    return Generator.<PojoMember, PojoSettings>ofWriterFunction(w -> w.println("@NotNull"))
        .append(w -> w.ref(JavaValidationRefs.NOT_NULL))
        .filter((field, settings) -> field.isRequired() && field.isNotNullable());
  }

  private static Generator<PojoMember, PojoSettings> emailAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onEmailFn(
                        ignore -> w.println("@Email").ref(JavaValidationRefs.EMAIL))))
        .contraMap(field -> field.getType().getConstraints());
  }

  private static Generator<PojoMember, PojoSettings> minAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onMinFn(
                        min ->
                            w.println("@Min(value = %d)", min.getValue())
                                .ref(JavaValidationRefs.MIN))))
        .contraMap(field -> field.getType().getConstraints());
  }

  private static Generator<PojoMember, PojoSettings> maxAnnotation() {
    return Generator.<Constraints, PojoSettings>emptyGen()
        .append(
            wrap(
                (constraints, w) ->
                    constraints.onMaxFn(
                        max ->
                            w.println("@Max(value = %d)", max.getValue())
                                .ref(JavaValidationRefs.MAX))))
        .contraMap(field -> field.getType().getConstraints());
  }

  private static Generator<PojoMember, PojoSettings> decimalMinAnnotation() {
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
        .contraMap(field -> field.getType().getConstraints());
  }

  private static Generator<PojoMember, PojoSettings> decimalMaxAnnotation() {
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
        .contraMap(field -> field.getType().getConstraints());
  }

  private static Generator<PojoMember, PojoSettings> sizeAnnotation() {
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
        .contraMap(field -> field.getType().getConstraints());
  }

  private static Generator<PojoMember, PojoSettings> patternAnnotation() {
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
        .contraMap(field -> field.getType().getConstraints());
  }

  private static Generator<Constraints, PojoSettings> wrap(
      BiFunction<Constraints, Writer, Optional<Writer>> writeConstraints) {
    return (constraints, settings, writer) ->
        writeConstraints.apply(constraints, writer).orElse(writer);
  }

  private static BiPredicate<PojoMember, PojoSettings> isValidationEnabled() {
    return (field, settings) -> settings.isEnableConstraints();
  }
}
