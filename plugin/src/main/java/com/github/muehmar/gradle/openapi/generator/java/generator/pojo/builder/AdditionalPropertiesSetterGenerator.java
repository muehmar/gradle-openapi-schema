package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

class AdditionalPropertiesSetterGenerator {
  private AdditionalPropertiesSetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesSetterGenerator() {
    return singlePropSetter(true)
        .filter(pojo -> pojo.getAdditionalProperties().isNotValueAnyType())
        .appendSingleBlankLine()
        .append(singlePropSetter(false))
        .appendSingleBlankLine()
        .append(allPropsSetter(), JavaObjectPojo::getAdditionalProperties);
  }

  private static Generator<JavaObjectPojo, PojoSettings> singlePropSetter(boolean forObjectType) {
    final Generator<JavaObjectPojo, PojoSettings> method =
        MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .modifiers(
                pojo ->
                    createModifiersForSinglePropSetter(
                        pojo.getAdditionalProperties(), forObjectType))
            .noGenericTypes()
            .returnType("Builder")
            .methodName("addAdditionalProperty")
            .arguments(
                pojo ->
                    PList.of(
                        new Argument("String", "key"),
                        new Argument(
                            forObjectType
                                ? "Object"
                                : pojo.getAdditionalProperties()
                                    .getType()
                                    .getParameterizedClassName()
                                    .asString(),
                            "value")))
            .content(singlePropSetterContent())
            .build()
            .append(RefsGenerator.javaTypeRefs(), pojo -> pojo.getAdditionalProperties().getType());
    return JacksonAnnotationGenerator.<JavaObjectPojo>jsonAnySetter()
        .filter(ignore -> not(forObjectType))
        .append(method);
  }

  private static Generator<JavaObjectPojo, PojoSettings> singlePropSetterContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(singlePropSetterContentMemberCondition(), PojoAndMember::fromPojo)
        .append(
            Generator.<JavaObjectPojo, PojoSettings>newLine()
                .filter(pojo -> pojo.getAllMembers().nonEmpty()))
        .append((pam, s, w) -> w.println("this.%s.put(key, value);", additionalPropertiesName()))
        .append(constant("return this;"));
  }

  private static Generator<PojoAndMember, PojoSettings> singlePropSetterContentMemberCondition() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(singlePropSetterRequiredNotNullableMemberCondition())
        .append(singlePropSetterRequiredNullableMemberCondition())
        .append(singlePropSetterOptionalNotNullableMemberCondition())
        .append(singlePropSetterOptionalNullableMemberCondition());
  }

  private static Generator<PojoAndMember, PojoSettings>
      singlePropSetterRequiredNotNullableMemberCondition() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "if (\"%s\".equals(key) && %s != null) {",
                    pam.getMember().getName().getOriginalName(), pam.getMember().getName()))
        .append(constant("return this;"), 1)
        .append(constant("}"))
        .filter(pam -> pam.getMember().isRequiredAndNotNullable());
  }

  private static Generator<PojoAndMember, PojoSettings>
      singlePropSetterRequiredNullableMemberCondition() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "if (\"%s\".equals(key) && %s) {",
                    pam.getMember().getName().getOriginalName(),
                    pam.getMember().getIsPresentFlagName()))
        .append(constant("return this;"), 1)
        .append(constant("}"))
        .filter(pam -> pam.getMember().isRequiredAndNullable());
  }

  private static Generator<PojoAndMember, PojoSettings>
      singlePropSetterOptionalNotNullableMemberCondition() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "if (\"%s\".equals(key) && %s != null) {",
                    pam.getMember().getName().getOriginalName(), pam.getMember().getName()))
        .append(constant("return this;"), 1)
        .append(constant("}"))
        .filter(pam -> pam.getMember().isOptionalAndNotNullable());
  }

  private static Generator<PojoAndMember, PojoSettings>
      singlePropSetterOptionalNullableMemberCondition() {
    return Generator.<PojoAndMember, PojoSettings>emptyGen()
        .append(
            (pam, s, w) ->
                w.println(
                    "if (\"%s\".equals(key) && (%s || %s != null)) {",
                    pam.getMember().getName().getOriginalName(),
                    pam.getMember().getIsNullFlagName(),
                    pam.getMember().getName()))
        .append(constant("return this;"), 1)
        .append(constant("}"))
        .filter(pam -> pam.getMember().isOptionalAndNullable());
  }

  private static JavaModifiers createModifiersForSinglePropSetter(
      JavaAdditionalProperties props, boolean forObjectType) {
    final boolean privateMethod = forObjectType || not(props.isAllowed());
    return JavaModifiers.of(privateMethod ? PRIVATE : PUBLIC);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> allPropsSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("Builder")
        .methodName("setAdditionalProperties")
        .singleArgument(
            props ->
                argument(
                    props.getMapContainerType().getParameterizedClassName(),
                    additionalPropertiesName()))
        .content(
            (props, s, w) ->
                w.println("this.%s.clear();", additionalPropertiesName())
                    .println("%s.forEach(this::addAdditionalProperty);", additionalPropertiesName())
                    .println("return this;"))
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .filter(JavaAdditionalProperties::isAllowed);
  }

  @Value
  private static class PojoAndMember {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    static PList<PojoAndMember> fromPojo(JavaObjectPojo pojo) {
      return pojo.getAllMembers().map(member -> new PojoAndMember(pojo, member));
    }
  }
}
