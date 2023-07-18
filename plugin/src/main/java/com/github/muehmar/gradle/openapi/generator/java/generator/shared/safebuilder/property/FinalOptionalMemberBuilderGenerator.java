package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class FinalOptionalMemberBuilderGenerator {
  private FinalOptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> finalOptionalMemberBuilderGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("public static final class %s {", builderName(p)))
        .append(constant("private final Builder builder;"), 1)
        .appendNewLine()
        .append((p, s, w) -> w.println("private %s(Builder builder) {", builderName(p)), 1)
        .append(constant("this.builder = builder;"), 2)
        .append(constant("}"), 1)
        .appendSingleBlankLine()
        .append(additionalPropertiesSetters().indent(1))
        .appendSingleBlankLine()
        .append((p, s, w) -> w.println("public %s build(){", p.getClassName()), 1)
        .append(constant("return builder.build();"), 2)
        .append(constant("}"), 1)
        .append(constant("}"));
  }

  private static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesSetters() {
    return singleAdditionalPropertySetter()
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter())
        .append(RefsGenerator.javaTypeRefs(), p -> p.getAdditionalProperties().getType())
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .filter(p -> p.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaObjectPojo, PojoSettings> singleAdditionalPropertySetter() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(FinalOptionalMemberBuilderGenerator::builderName)
        .methodName("addAdditionalProperty")
        .arguments(
            p ->
                PList.of(
                    "String key",
                    String.format(
                        "%s value", p.getAdditionalProperties().getType().getFullClassName())))
        .content(
            p ->
                String.format(
                    "return new %s(builder.addAdditionalProperty(key, value));", builderName(p)))
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> allAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(FinalOptionalMemberBuilderGenerator::builderName)
        .methodName("setAdditionalProperties")
        .singleArgument(
            p ->
                String.format(
                    "Map<String, %s> %s",
                    p.getAdditionalProperties().getType().getFullClassName(),
                    JavaAdditionalProperties.getPropertyName()))
        .content(
            p ->
                String.format(
                    "return new %s(builder.setAdditionalProperties(%s));",
                    builderName(p), JavaAdditionalProperties.getPropertyName()))
        .build();
  }

  private static String builderName(JavaObjectPojo pojo) {
    return OptionalPropertyBuilderName.from(
            pojo, pojo.getMembers().filter(JavaPojoMember::isOptional).size())
        .currentName();
  }
}
