package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
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

  private static Generator<TechnicalPojoMember, PojoSettings> singleMember() {
    return Generator.<TechnicalPojoMember, PojoSettings>emptyGen()
        .append(
            (field, settings, writer) ->
                writer.println(
                    "private final %s %s;",
                    field.getJavaType().getParameterizedClassName(), field.getName()))
        .append(RefsGenerator.javaTypeRefs(), TechnicalPojoMember::getJavaType);
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
    PList<TechnicalPojoMember> members;
    Optional<JavaAdditionalProperties> additionalProperties;
  }
}
