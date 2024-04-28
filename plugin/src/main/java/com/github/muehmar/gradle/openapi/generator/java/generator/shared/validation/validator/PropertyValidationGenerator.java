package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.decimalMaxCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.decimalMinCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.deepValidationCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.emailCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.isSingleTrueCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.maxCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.maxSizeCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.minCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.minSizeCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.multipleOfCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.necessityAndNullabilityCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.patternCondition;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator.ConstraintConditions.uniqueArrayItemsCondition;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;

public class PropertyValidationGenerator {
  private PropertyValidationGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> memberValidationGenerator() {
    return propertyValueValidationGenerator().contraMap(PropertyValue::fromJavaMember);
  }

  public static Generator<JavaObjectPojo, PojoSettings> requiredAdditionalPropertyGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            propertyValueValidationGenerator(), PropertyValue::fromRequiredAdditionalProperties);
  }

  public static Generator<PropertyValue, PojoSettings> propertyValueValidationGenerator() {
    final MethodGen<PropertyValue, PojoSettings> method =
        JavaGenerators.<PropertyValue, PojoSettings>methodGen()
            .modifiers(PRIVATE)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(
                propertyValue ->
                    IsPropertyValidMethodName.fromName(propertyValue.getName()).asString())
            .arguments(
                pv ->
                    pv.isNested()
                        ? PList.single(
                            argument(
                                pv.getType().getInternalParameterizedClassName(), pv.getName()))
                        : PList.empty())
            .doesNotThrow()
            .content(singlePropertyValueValidationGenerator())
            .build();
    return (propertyValue, settings, writer) -> {
      final Writer validationMethodWriter = method.generate(propertyValue, settings, writer);

      return propertyValue
          .nestedPropertyValue()
          .map(
              nestedPropertyValue ->
                  propertyValueValidationGenerator()
                      .generate(nestedPropertyValue, settings, javaWriter()))
          .map(w -> validationMethodWriter.printSingleBlankLine().append(w))
          .orElse(validationMethodWriter);
    };
  }

  private static Generator<PropertyValue, PojoSettings> singlePropertyValueValidationGenerator() {
    final Generator<PropertyValue, PojoSettings> conditionsGenerator =
        conditionGenerator(
            minCondition(),
            maxCondition(),
            minSizeCondition(),
            maxSizeCondition(),
            decimalMinCondition(),
            decimalMaxCondition(),
            patternCondition(),
            emailCondition(),
            uniqueArrayItemsCondition(),
            multipleOfCondition(),
            deepValidationCondition());
    return wrapNotNullsafeGenerator(conditionsGenerator)
        .append(conditionGenerator(necessityAndNullabilityCondition(conditionsGenerator)));
  }

  private static Generator<PropertyValue, PojoSettings> conditionGenerator(
      ConstraintConditions.Condition... conditions) {
    return (propertyValue, settings, writer) -> {
      final PList<Writer> conditionWriters =
          PList.fromArray(conditions)
              .map(gen -> gen.generate(propertyValue, settings, javaWriter()));
      return writer.append(ConditionsWriter.andConditions(conditionWriters));
    };
  }

  private static Generator<PropertyValue, PojoSettings> wrapNotNullsafeGenerator(
      Generator<PropertyValue, PojoSettings> conditions) {
    return Generator.<PropertyValue, PojoSettings>emptyGen()
        .append((pv, s, w) -> w.println("if(%s != null) {", pv.getAccessor()))
        .append(conditions, 1)
        .append(Generator.constant("}"))
        .appendSingleBlankLine()
        .filter((pv, settings) -> not(isSingleTrueCondition(conditions, pv, settings)));
  }
}
