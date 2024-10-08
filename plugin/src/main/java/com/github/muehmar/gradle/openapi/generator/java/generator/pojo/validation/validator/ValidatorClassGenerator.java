package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValidationGenerator.memberValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValidationGenerator.propertyValueValidationGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValidationGenerator.requiredAdditionalPropertyGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ANY_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ONE_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.isValidAgainstTheCorrectSchemaMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.getPropertyCountMethodName;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConditionsWriter;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.IsPropertyValidMethodName;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.PropertyValue;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
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
        .appendList(memberValidationGenerator(), JavaObjectPojo::getMembers, newLine())
        .appendSingleBlankLine()
        .append(requiredAdditionalPropertyGenerator())
        .appendSingleBlankLine()
        .append(additionalPropertiesValidationMethods())
        .appendSingleBlankLine()
        .append(isValidMethod());
  }

  private static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesValidationMethods() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            propertyValueValidationGenerator(),
            (Function<JavaObjectPojo, PropertyValue>) PropertyValue::fromAdditionalProperties)
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidMethod() {
    return JavaGenerators.<JavaObjectPojo, PojoSettings>methodGen()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("isValid")
        .noArguments()
        .doesNotThrow()
        .content(
            isValidMethodContent(
                PList.of(
                    methodContentOneOfCondition(),
                    methodContentOneOfDiscriminatorCondition(),
                    methodContentAnyOfCondition(),
                    methodContentAnyOfDiscriminatorCondition(),
                    additionalPropertiesTypeCondition(),
                    minPropertyCountCondition(),
                    maxPropertyCountCondition(),
                    noAdditionalPropertiesCondition(),
                    additionalPropertiesValidCondition())))
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> isValidMethodContent(
      PList<Condition> dtoConditions) {
    return (pojo, settings, writer) -> {
      final PList<Condition> allConditions =
          createPropertyValidationConditions(pojo)
              .concat(createAllOfDtoValidationConditions(pojo))
              .concat(dtoConditions);
      final PList<Writer> conditionWriters =
          allConditions.map(gen -> gen.generate(pojo, settings, javaWriter()));
      return writer.append(ConditionsWriter.andConditions(conditionWriters));
    };
  }

  private static PList<Condition> createPropertyValidationConditions(JavaObjectPojo pojo) {
    final PList<JavaName> memberNames = pojo.getMembers().map(JavaPojoMember::getName);
    final PList<JavaName> requiredAdditionalPropertiesNames =
        pojo.getRequiredAdditionalProperties().map(JavaRequiredAdditionalProperty::getName);
    return memberNames
        .concat(requiredAdditionalPropertiesNames)
        .map(name -> (p, s, w) -> w.print("%s()", IsPropertyValidMethodName.fromName(name)));
  }

  private static PList<Condition> createAllOfDtoValidationConditions(JavaObjectPojo pojo) {
    return pojo.getAllOfComposition()
        .map(JavaAllOfComposition::getPojos)
        .map(NonEmptyList::toPList)
        .orElseGet(PList::empty)
        .map(
            allOfPojo ->
                (p, s, w) ->
                    w.print(
                        "%s().isValid()",
                        MethodNames.Composition.asConversionMethodName(allOfPojo)));
  }

  private static Condition methodContentOneOfCondition() {
    return Condition.constant("getOneOfValidCount() == 1")
        .filter(
            (pojo, settings) ->
                pojo.getOneOfComposition()
                    .map(comp -> comp.validateExactlyOneMatch(settings))
                    .orElse(false));
  }

  private static Condition methodContentOneOfDiscriminatorCondition() {
    return Condition.constant(
            isValidAgainstTheCorrectSchemaMethodName(ONE_OF).asString().concat("()"))
        .filter(ValidatorClassGenerator::hasOneOfDiscriminator);
  }

  private static boolean hasOneOfDiscriminator(JavaObjectPojo pojo) {
    return pojo.getOneOfComposition().flatMap(JavaOneOfComposition::getDiscriminator).isPresent();
  }

  private static Condition methodContentAnyOfCondition() {
    return Condition.constant("getAnyOfValidCount() >= 1")
        .filter(JavaObjectPojo::hasAnyOfComposition);
  }

  private static Condition methodContentAnyOfDiscriminatorCondition() {
    return Condition.constant(
            isValidAgainstTheCorrectSchemaMethodName(ANY_OF).asString().concat("()"))
        .filter(ValidatorClassGenerator::hasAnyOfDiscriminator);
  }

  private static boolean hasAnyOfDiscriminator(JavaObjectPojo pojo) {
    return pojo.getAnyOfComposition().flatMap(JavaAnyOfComposition::getDiscriminator).isPresent();
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
                .map(min -> w.print("%d <= %s()", min, getPropertyCountMethodName())));
  }

  private static Condition maxPropertyCountCondition() {
    return Condition.optional(
        (p, w) ->
            p.getConstraints()
                .getPropertyCount()
                .flatMap(PropertyCount::getMaxProperties)
                .map(max -> w.print("%s() <= %d", getPropertyCountMethodName(), max)));
  }

  private static Condition noAdditionalPropertiesCondition() {
    return Condition.constant("additionalProperties.isEmpty()")
        .filter(pojo -> pojo.getAdditionalProperties().isNotAllowed());
  }

  private static Condition additionalPropertiesValidCondition() {
    return Condition.constant("isAdditionalPropertiesValid()")
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
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

    @Override
    default Condition filter(BiPredicate<JavaObjectPojo, PojoSettings> predicate) {
      final Generator<JavaObjectPojo, PojoSettings> self = this;
      return (p, s, w) -> predicate.test(p, s) ? self.generate(p, s, w) : w;
    }
  }
}
