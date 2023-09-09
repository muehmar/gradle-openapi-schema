package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class FinalOptionalMemberBuilderGenerator {
  private FinalOptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> finalOptionalMemberBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println("public static final class %s {", builderName(builderVariant, p)))
        .append(constant("private final Builder builder;"), 1)
        .appendNewLine()
        .append(
            (p, s, w) -> w.println("private %s(Builder builder) {", builderName(builderVariant, p)),
            1)
        .append(constant("this.builder = builder;"), 2)
        .append(constant("}"), 1)
        .appendSingleBlankLine()
        .append(additionalPropertiesSetters(builderVariant).indent(1))
        .appendSingleBlankLine()
        .append((p, s, w) -> w.println("public %s build(){", p.getClassName()), 1)
        .append(constant("return builder.build();"), 2)
        .append(constant("}"), 1)
        .append(constant("}"));
  }

  private static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesSetters(
      SafeBuilderVariant builderVariant) {
    return singleAdditionalPropertySetter(builderVariant)
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter(builderVariant))
        .append(RefsGenerator.javaTypeRefs(), p -> p.getAdditionalProperties().getType())
        .filter(p -> p.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaObjectPojo, PojoSettings> singleAdditionalPropertySetter(
      SafeBuilderVariant builderVariant) {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(pojo -> builderName(builderVariant, pojo))
        .methodName("addAdditionalProperty")
        .arguments(
            p ->
                PList.of(
                    new Argument("String", "key"),
                    new Argument(
                        p.getAdditionalProperties().getType().getFullClassName().asString(),
                        "value")))
        .content(
            p ->
                String.format(
                    "return new %s(builder.addAdditionalProperty(key, value));",
                    builderName(builderVariant, p)))
        .build();
  }

  private static Generator<JavaObjectPojo, PojoSettings> allAdditionalPropertiesSetter(
      SafeBuilderVariant builderVariant) {
    return MethodGenBuilder.<JavaObjectPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(pojo -> builderName(builderVariant, pojo))
        .methodName("setAdditionalProperties")
        .singleArgument(
            p ->
                new Argument(
                    String.format(
                        "Map<String, %s>",
                        p.getAdditionalProperties().getType().getFullClassName()),
                    additionalPropertiesName().asString()))
        .content(
            p ->
                String.format(
                    "return new %s(builder.setAdditionalProperties(%s));",
                    builderName(builderVariant, p), additionalPropertiesName()))
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_MAP));
  }

  private static String builderName(SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
    return OptionalPropertyBuilderName.from(
            builderVariant, pojo, pojo.getMembers().filter(JavaPojoMember::isOptional).size())
        .currentName();
  }
}
