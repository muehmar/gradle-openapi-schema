package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
public class ApiRequiredNotNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;
  ApiType apiType;

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .getApiType()
        .map(apiType -> new ApiRequiredNotNullableMemberSetter(member, apiType));
  }

  @Override
  public boolean shouldBeUsed() {
    return member.isRequiredAndNotNullable();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return settings.isEnableStagedBuilder() ? JavaModifier.PRIVATE : JavaModifier.PUBLIC;
  }

  @Override
  public String argumentType() {
    return apiType.getParameterizedClassName().asString();
  }

  @Override
  public String memberValue() {
    return conversionWriter().asString();
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.empty();
  }

  @Override
  public Writer addRefs(Writer writer) {
    final Writer conversionWriter = conversionWriter();
    return conversionWriter
        .getRefs()
        .foldLeft(writer, Writer::ref)
        .ref(apiType.getClassName().asString());
  }

  private Writer conversionWriter() {
    return FromApiTypeConversion.fromApiTypeConversion(
        apiType, member.getName().asString(), NO_NULL_CHECK);
  }
}
