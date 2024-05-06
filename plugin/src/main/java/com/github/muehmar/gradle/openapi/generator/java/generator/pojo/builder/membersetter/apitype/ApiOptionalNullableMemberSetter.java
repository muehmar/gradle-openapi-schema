package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
public class ApiOptionalNullableMemberSetter implements MemberSetter {
  JavaPojoMember member;
  ApiType apiType;

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .getApiType()
        .map(apiType -> new ApiOptionalNullableMemberSetter(member, apiType));
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return member.isOptionalAndNullable();
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterJavaType.API);
  }

  @Override
  public String argumentType() {
    return String.format("Tristate<%s>", apiType.getParameterizedClassName());
  }

  @Override
  public String memberValue() {
    final Writer writer = conversionWriter();
    return String.format(
        "%s.map(val -> %s).%s", member.getName(), writer.asString(), member.tristateToProperty());
  }

  @Override
  public Optional<String> flagAssignment() {
    return Optional.of(FlagAssignments.wrappedOptionalNullableFlagAssignment(member));
  }

  @Override
  public PList<String> getRefs() {
    final Writer conversionWriter = conversionWriter();
    return conversionWriter
        .getRefs()
        .cons(OpenApiUtilRefs.TRISTATE)
        .cons(apiType.getClassName().asString());
  }

  private Writer conversionWriter() {
    return FromApiTypeConversion.fromApiTypeConversion(apiType, "val", NO_NULL_CHECK);
  }
}
