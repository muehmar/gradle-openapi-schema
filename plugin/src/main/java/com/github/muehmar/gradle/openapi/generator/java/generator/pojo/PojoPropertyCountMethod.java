package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.getPropertyCountMethodName;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class PojoPropertyCountMethod {
  private PojoPropertyCountMethod() {}

  private static final String JAVA_DOC = "Returns the number of present properties of this object.";

  public static Generator<JavaObjectPojo, PojoSettings> pojoPropertyCountMethoGenerator() {
    final MethodGen<JavaObjectPojo, PojoSettings> method =
        MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("int")
            .methodName(getPropertyCountMethodName().asString())
            .noArguments()
            .content(propertyCountMethodContent())
            .build();
    return JavaDocGenerator.<JavaObjectPojo, PojoSettings>ofJavaDocString(JAVA_DOC)
        .append(
            ValidationAnnotationGenerator.minAnnotationForPropertyCount(),
            JavaObjectPojo::getConstraints)
        .append(
            ValidationAnnotationGenerator.maxAnnotationForPropertyCount(),
            JavaObjectPojo::getConstraints)
        .append(JacksonAnnotationGenerator.jsonIgnore())
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
        .appendList(propertyCount.indent(1), PojoAndMember::fromPojo)
        .append(additionalPropertiesCount().indent(1));
  }

  private static Generator<PojoAndMember, PojoSettings> requiredNotNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append((pam, s, w) -> w.println("(%s != null ? 1 : 0) +", pam.getMember().getName()))
        .filter(pam -> pam.getMember().isRequiredAndNotNullable());
  }

  private static Generator<PojoAndMember, PojoSettings> requiredNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append((pam, s, w) -> w.println("(%s ? 1 : 0) +", pam.getMember().getIsPresentFlagName()))
        .filter(pam -> pam.getMember().isRequiredAndNullable());
  }

  private static Generator<PojoAndMember, PojoSettings> optionalNotNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append((pam, s, w) -> w.println("(%s != null ? 1 : 0) +", pam.getMember().getName()))
        .filter(pam -> pam.getMember().isOptionalAndNotNullable());
  }

  private static Generator<PojoAndMember, PojoSettings> optionalNullablePropertyCount() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "((%s || %s != null) ? 1 : 0) +",
                    pam.getMember().getIsNullFlagName(), pam.getMember().getName()))
        .filter(pam -> pam.getMember().isOptionalAndNullable());
  }

  private static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesCount() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(constant("%s.size();", additionalPropertiesName()));
  }

  @Value
  private static class PojoAndMember {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    private static PList<PojoAndMember> fromPojo(JavaObjectPojo pojo) {
      return pojo.getAllMembers().map(member -> new PojoAndMember(pojo, member));
    }
  }
}
