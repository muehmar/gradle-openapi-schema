package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NULL_SAFE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
public class ApiStandardMemberSetter implements MemberSetter {
  JavaPojoMember member;
  ApiType apiType;

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .getApiType()
        .map(apiType -> new ApiStandardMemberSetter(member, apiType));
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return true;
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterJavaType.API);
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
    return FlagAssignments.forStandardMemberSetter(member);
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
        apiType, member.getName().asString(), NULL_SAFE);
  }
}
