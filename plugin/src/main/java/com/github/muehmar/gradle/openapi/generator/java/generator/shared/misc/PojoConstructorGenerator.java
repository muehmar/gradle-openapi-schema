package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ConstructorGeneratorBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.BiFunction;
import lombok.Value;

public class PojoConstructorGenerator {
  private PojoConstructorGenerator() {}

  public static Generator<ConstructorContent, PojoSettings> pojoConstructorGenerator() {
    final Generator<ConstructorContent, PojoSettings> method =
        ConstructorGeneratorBuilder.<ConstructorContent, PojoSettings>create()
            .modifiers(ConstructorContent::getModifiers)
            .javaClassName(ConstructorContent::getClassName)
            .arguments(constructorArguments())
            .content(constructorContent())
            .build()
            .append(additionalPropertiesImports());
    return JacksonAnnotationGenerator.<ConstructorContent>jsonCreator()
        .filter(ConstructorContent::isArray)
        .append(method);
  }

  private static Generator<ConstructorContent, PojoSettings> additionalPropertiesImports() {
    final Generator<JavaAdditionalProperties, PojoSettings> imports =
        Generator.<JavaType, PojoSettings>emptyGen()
            .append(w -> w.ref(JavaRefs.JAVA_UTIL_MAP))
            .append(w -> w.ref(JavaRefs.JAVA_UTIL_COLLECTIONS))
            .append(RefsGenerator.javaTypeRefs())
            .contraMap(JavaAdditionalProperties::getType);
    return Generator.<ConstructorContent, PojoSettings>emptyGen()
        .appendOptional(imports, ConstructorContent::getAdditionalProperties);
  }

  private static BiFunction<ConstructorContent, PojoSettings, PList<String>>
      constructorArguments() {
    return (pojo, pojoSettings) ->
        pojo.getMembers()
            .map(PojoConstructorGenerator::createArgument)
            .concat(createAdditionalPropertyArgument(pojo));
  }

  private static String createArgument(TechnicalPojoMember member) {
    return String.format(
        "%s %s", member.getJavaType().getParameterizedClassName(), member.getName());
  }

  private static PList<String> createAdditionalPropertyArgument(ConstructorContent content) {
    return PList.fromOptional(content.getAdditionalProperties())
        .map(props -> String.format("Map<String, Object> %s", additionalPropertiesName()));
  }

  private static Generator<ConstructorContent, PojoSettings> constructorContent() {
    return (content, settings, writer) -> {
      final PList<String> assignments =
          content
              .getMembers()
              .map(PojoConstructorGenerator::createMemberAssignment)
              .concat(createAdditionalPropertiesAssignment(content));
      return assignments.foldLeft(writer, Writer::println);
    };
  }

  private static String createMemberAssignment(TechnicalPojoMember member) {
    return String.format("this.%s = %s;", member.getName(), member.getName());
  }

  private static PList<String> createAdditionalPropertiesAssignment(ConstructorContent content) {
    return PList.fromOptional(content.getAdditionalProperties())
        .map(
            props ->
                String.format(
                    "this.%s = Collections.unmodifiableMap(%s);",
                    additionalPropertiesName(), additionalPropertiesName()));
  }

  @Value
  @PojoBuilder(builderName = "ConstructorContentBuilder")
  public static class ConstructorContent {
    Optional<JavaModifier> modifier;
    boolean isArray;
    JavaName className;
    PList<TechnicalPojoMember> members;
    Optional<JavaAdditionalProperties> additionalProperties;

    JavaModifiers getModifiers() {
      return JavaModifiers.of(PList.fromOptional(modifier));
    }
  }
}
