package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class MultipleOfValidationMethodGenerator {
  private MultipleOfValidationMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(memberGenerator().appendSingleBlankLine(), MemberAndConstraint::fromPojo)
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<MemberAndConstraint, PojoSettings> memberGenerator() {
    final MethodGen<MemberAndConstraint, PojoSettings> method =
        MethodGenBuilder.<MemberAndConstraint, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(MemberAndConstraint::getIsMultipleOfValidMethodName)
            .noArguments()
            .content(content())
            .build();

    final Generator<MemberAndConstraint, PojoSettings> assertTrueAnnotation =
        ValidationGenerator.assertTrue(
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
        .filter(MemberAndConstraint::isIntegerOrNumericType);
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
      return member
          .getName()
          .asJavaName()
          .startUpperCase()
          .prefix("is")
          .append("MultipleOfValid")
          .asIdentifier()
          .asString();
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
