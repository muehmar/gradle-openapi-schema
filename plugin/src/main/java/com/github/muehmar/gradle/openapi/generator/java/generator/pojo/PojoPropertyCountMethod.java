package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class PojoPropertyCountMethod {
  private PojoPropertyCountMethod() {}

  private static final String JAVA_DOC = "Returns the number of present properties of this object.";

  public static Generator<JavaObjectPojo, PojoSettings> propertyCountMethod() {
    final MethodGen<JavaObjectPojo, PojoSettings> method =
        MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName("getPropertyCount")
            .noArguments()
            .content(propertyCountMethodContent())
            .build();
    return JavaDocGenerator.<JavaObjectPojo, PojoSettings>ofJavaDocString(JAVA_DOC)
        .append(ValidationGenerator.minAnnotationForPropertyCount(), JavaObjectPojo::getConstraints)
        .append(ValidationGenerator.maxAnnotationForPropertyCount(), JavaObjectPojo::getConstraints)
        .append(method);
  }

  private static Generator<JavaObjectPojo, PojoSettings> propertyCountMethodContent() {
    final Generator<PojoAndMember, PojoSettings> propertyCount =
        requiredNotNullablePropertyCount()
            .append(requiredNullablePropertyCount())
            .append(optionalNotNullablePropertyCount())
            .append(optionalNullablePropertyCount());
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(w -> w.println("return"))
        .appendList(propertyCount.indent(1), PojoAndMember::fromPojo);
  }

  private static Generator<PojoAndMember, PojoSettings> requiredNotNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append((pam, s, w) -> w.println("1%s", pam.plusOrSemicolon()))
        .filter(pam -> pam.getMember().isRequiredAndNotNullable());
  }

  private static Generator<PojoAndMember, PojoSettings> requiredNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "(%s ? 1 : 0)%s",
                    pam.getMember().getIsPresentFlagName(), pam.plusOrSemicolon()))
        .filter(pam -> pam.getMember().isRequiredAndNullable());
  }

  private static Generator<PojoAndMember, PojoSettings> optionalNotNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "(%s != null ? 1 : 0)%s", pam.getMember().getName(), pam.plusOrSemicolon()))
        .filter(pam -> pam.getMember().isOptionalAndNotNullable());
  }

  private static Generator<PojoAndMember, PojoSettings> optionalNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "((%s || %s != null) ? 1 : 0)%s",
                    pam.getMember().getIsNullFlagName(),
                    pam.getMember().getName(),
                    pam.plusOrSemicolon()))
        .filter(pam -> pam.getMember().isOptionalAndNullable());
  }

  @Value
  private static class PojoAndMember {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    private static PList<PojoAndMember> fromPojo(JavaObjectPojo pojo) {
      return pojo.getMembers().map(member -> new PojoAndMember(pojo, member));
    }

    private boolean isLast() {
      return pojo.getMembers().reverse().headOption().equals(Optional.of(member));
    }

    private String plusOrSemicolon() {
      return isLast() ? ";" : " +";
    }
  }
}
