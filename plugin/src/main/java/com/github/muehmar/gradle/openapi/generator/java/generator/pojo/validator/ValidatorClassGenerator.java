package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValidationGenerator.propertyValidationGenerator;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.IsPropertyValidMethodName;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ReturningAndConditions;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class ValidatorClassGenerator {
  private ValidatorClassGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> validationClassGenerator() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>classGen()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .noJavaDoc()
        .noAnnotations()
        .modifiers(PRIVATE)
        .className("Validator")
        .noSuperClass()
        .noInterfaces()
        .content(validationClassContent())
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> validationClassContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(propertyValidationGenerator(), JavaObjectPojo::getMembers, newLine())
        .appendSingleBlankLine()
        .append(isValidMethod());
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidMethod() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("isValid")
        .noArguments()
        .content(
            isValidMethodContent(
                PList.of(
                    methodContentOneOfCondition(),
                    methodContentOneOfDiscriminatorCondition(),
                    methodContentAnyOfCondition(),
                    additionalPropertiesTypeCondition(),
                    minPropertyCountCondition(),
                    maxPropertyCountCondition(),
                    noAdditionalPropertiesCondition())))
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidMethodContent(
      PList<Condition> dtoConditions) {
    return (pojo, settings, writer) -> {
      final PList<Condition> allConditions =
          createPropertyValidationConditions(pojo).concat(dtoConditions);
      final PList<Writer> conditionWriters =
          allConditions.map(gen -> gen.generate(pojo, settings, javaWriter()));
      final ReturningAndConditions returningAndConditions =
          ReturningAndConditions.forConditions(conditionWriters);
      return writer.append(returningAndConditions.getWriter());
    };
  }

  private static PList<Condition> createPropertyValidationConditions(JavaObjectPojo pojo) {
    return pojo.getMembers()
        .map(member -> (p, s, w) -> w.print("%s()", IsPropertyValidMethodName.fromMember(member)));
  }

  private static Condition methodContentOneOfCondition() {
    return Condition.constant("getOneOfValidCount() == 1")
        .filter(JavaObjectPojo::hasOneOfComposition);
  }

  private static Condition methodContentOneOfDiscriminatorCondition() {
    return Condition.constant("isValidAgainstTheCorrectSchema()")
        .filter(ValidatorClassGenerator::hasOneOfDiscriminator);
  }

  private static boolean hasOneOfDiscriminator(JavaObjectPojo pojo) {
    return pojo.getOneOfComposition().flatMap(JavaOneOfComposition::getDiscriminator).isPresent();
  }

  private static Condition methodContentAnyOfCondition() {
    return Condition.constant("getAnyOfValidCount() >= 1")
        .filter(JavaObjectPojo::hasAnyOfComposition);
  }

  private static Condition additionalPropertiesTypeCondition() {
    return Condition.constant("isAllAdditionalPropertiesHaveCorrectType()")
        .filter(pojo -> pojo.getAdditionalProperties().isNotValueAnyType());
  }

  private static Condition minPropertyCountCondition() {
    return Condition.optional(
        (p, w) ->
            p.getConstraints()
                .getPropertyCount()
                .flatMap(PropertyCount::getMinProperties)
                .map(min -> w.print("%d <= getPropertyCount()", min)));
  }

  private static Condition maxPropertyCountCondition() {
    return Condition.optional(
        (p, w) ->
            p.getConstraints()
                .getPropertyCount()
                .flatMap(PropertyCount::getMaxProperties)
                .map(max -> w.print("getPropertyCount() <= %d", max)));
  }

  private static Condition noAdditionalPropertiesCondition() {
    return Condition.constant("additionalProperties.isEmpty()")
        .filter(pojo -> pojo.getAdditionalProperties().isNotAllowed());
  }

  private interface Condition extends Generator<JavaObjectPojo, PojoSettings> {
    static Condition constant(String constant) {
      return (p, s, w) -> w.print(constant);
    }

    static Condition optional(BiFunction<JavaObjectPojo, Writer, Optional<Writer>> condition) {
      return (p, s, w) -> condition.apply(p, w).orElse(w);
    }

    @Override
    default Condition filter(Predicate<JavaObjectPojo> predicate) {
      final Generator<JavaObjectPojo, PojoSettings> self = this;
      return (p, s, w) -> predicate.test(p) ? self.generate(p, s, w) : w;
    }
  }
}
