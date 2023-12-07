package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validation;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.ConstraintType;
import com.github.muehmar.gradle.openapi.generator.java.model.validation.JavaConstraints;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import com.github.muehmar.gradle.openapi.warnings.Warning;
import com.github.muehmar.gradle.openapi.warnings.WarningsContext;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class MultipleOfValidationMethodGenerator {
  private MultipleOfValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> multipleOfValidationMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(memberGenerator().appendSingleBlankLine(), MemberAndConstraint::fromPojo);
  }

  private static Generator<MemberAndConstraint, PojoSettings> memberGenerator() {
    final Generator<MemberAndConstraint, PojoSettings> method =
        MethodGenBuilder.<MemberAndConstraint, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(MemberAndConstraint::getIsMultipleOfValidMethodName)
            .noArguments()
            .doesNotThrow()
            .content(content())
            .build();

    final Generator<MemberAndConstraint, PojoSettings> assertTrueAnnotation =
        ValidationAnnotationGenerator.assertTrue(
            mc ->
                String.format(
                    "%s is not a multiple of %s",
                    mc.member.getName(),
                    mc.member
                        .getJavaType()
                        .getConstraints()
                        .getMultipleOf()
                        .map(MultipleOf::asString)
                        .orElse("0")));
    return Generator.<MemberAndConstraint, PojoSettings>emptyGen()
        .append(assertTrueAnnotation)
        .append(method)
        .filter(MultipleOfValidationMethodGenerator::isSupportedConstraint);
  }

  private static boolean isSupportedConstraint(
      MemberAndConstraint memberAndConstraint, PojoSettings settings) {
    final JavaPojoMember member = memberAndConstraint.getMember();
    final ConstraintType constraintType = ConstraintType.MULTIPLE_OF;
    if (JavaConstraints.isSupported(member.getJavaType(), constraintType)) {
      return true;
    } else {
      final TaskIdentifier taskIdentifier = settings.getTaskIdentifier();
      final Warning warning =
          Warning.unsupportedValidation(
              member.getPropertyInfoName(), member.getJavaType(), constraintType);
      WarningsContext.addWarningForTask(taskIdentifier, warning);
      return false;
    }
  }

  private static Generator<MemberAndConstraint, PojoSettings> content() {
    return Generator.<MemberAndConstraint, PojoSettings>emptyGen()
        .append(integerAndLongContent())
        .append(floatAndDoubleContent());
  }

  private static Generator<MemberAndConstraint, PojoSettings> integerAndLongContent() {
    return Generator.<MemberAndConstraint, PojoSettings>emptyGen()
        .append(
            (mc, s, w) ->
                w.println(
                    "return %s == null || %s %% %sL == 0;",
                    mc.member.getName(), mc.member.getName(), mc.multipleOf.asString()))
        .filter(MemberAndConstraint::isIntegerType);
  }

  private static Generator<MemberAndConstraint, PojoSettings> floatAndDoubleContent() {
    return Generator.<MemberAndConstraint, PojoSettings>emptyGen()
        .append(
            (mc, s, w) ->
                w.println(
                    "return %s == null || BigDecimal.valueOf(%s).divideAndRemainder(new BigDecimal(\"%s\"))[1].signum() == 0;",
                    mc.member.getName(), mc.member.getName(), mc.multipleOf.asString()))
        .append(w -> w.ref(JavaRefs.JAVA_MATH_BIG_DECIMAL))
        .filter(MemberAndConstraint::isNumericType);
  }

  @Value
  private static class MemberAndConstraint {
    JavaPojoMember member;
    MultipleOf multipleOf;

    static PList<MemberAndConstraint> fromPojo(JavaObjectPojo pojo) {
      return pojo.getMembers()
          .flatMapOptional(
              member ->
                  member
                      .getJavaType()
                      .getConstraints()
                      .getMultipleOf()
                      .map(multipleOf -> new MemberAndConstraint(member, multipleOf)));
    }

    public String getIsMultipleOfValidMethodName() {
      return MethodNames.getIsMultipleOfValidMethodName(member.getName()).asString();
    }

    public boolean isIntegerType() {
      return member.getJavaType().getType().asIntegerType().isPresent();
    }

    public boolean isNumericType() {
      return member.getJavaType().getType().asNumericType().isPresent();
    }

    public boolean isIntegerOrNumericType() {
      return isIntegerType() || isNumericType();
    }
  }
}
