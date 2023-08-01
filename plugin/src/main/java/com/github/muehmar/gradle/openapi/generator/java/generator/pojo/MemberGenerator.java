package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

public class MemberGenerator {
  private MemberGenerator() {}

  public static Generator<MemberContent, PojoSettings> memberGenerator() {
    return Generator.<MemberContent, PojoSettings>emptyGen()
        .append(jsonValueAnnotation())
        .appendList(singleMember(), MemberContent::getMembers)
        .appendOptional(additionalPropertiesMember(), MemberContent::getAdditionalProperties);
  }

  private static Generator<JavaPojoMember, PojoSettings> singleMember() {
    final Generator<JavaPojoMember, PojoSettings> fieldDeclaration =
        (field, settings, writer) ->
            writer.println(
                "private final %s %s;",
                field.getJavaType().getFullClassName(), field.getNameAsIdentifier());
    final Generator<JavaPojoMember, PojoSettings> requiredNullableFlag =
        (field, settings, writer) ->
            writer.println("private final boolean %s;", field.getIsPresentFlagName());
    final Generator<JavaPojoMember, PojoSettings> optionalNullableFlag =
        (field, settings, writer) ->
            writer.println("private final boolean %s;", field.getIsNullFlagName());
    return fieldDeclaration
        .appendConditionally(JavaPojoMember::isRequiredAndNullable, requiredNullableFlag)
        .appendConditionally(JavaPojoMember::isOptionalAndNullable, optionalNullableFlag)
        .append(
            (field, settings, writer) ->
                field.getJavaType().getImportsAsString().foldLeft(writer, Writer::ref));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertiesMember() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("private final Map<String, Object> %s;", additionalPropertiesName()))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_MAP));
  }

  private static Generator<MemberContent, PojoSettings> jsonValueAnnotation() {
    return JacksonAnnotationGenerator.<MemberContent>jsonValue().filter(MemberContent::isArrayPojo);
  }

  @Value
  @PojoBuilder(builderName = "MemberContentBuilder")
  public static class MemberContent {
    boolean isArrayPojo;
    PList<JavaPojoMember> members;
    Optional<JavaAdditionalProperties> additionalProperties;
  }
}
