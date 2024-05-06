package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NULL_SAFE;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.MemberSetter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.SetterModifier.SetterJavaType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.maplistitem.MapListItemMethod;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

@Value
class ApiTypeListAllMemberSetter implements MemberSetter {
  JavaPojoMember member;
  JavaArrayType javaArrayType;

  public static Optional<MemberSetter> fromMember(JavaPojoMember member) {
    return member
        .getJavaType()
        .onArrayType()
        .map(javaArrayType -> new ApiTypeListAllMemberSetter(member, javaArrayType));
  }

  @Override
  public boolean shouldBeUsed(PojoSettings settings) {
    return ApiTypeListConditions.groupCondition().test(member);
  }

  @Override
  public JavaModifier modifier(PojoSettings settings) {
    return SetterModifier.forMember(member, settings, SetterJavaType.API);
  }

  @Override
  public String argumentType() {
    return member
        .getJavaType()
        .getApiType()
        .map(apiType -> apiType.getParameterizedClassName().asString())
        .orElseGet(() -> member.getJavaType().getParameterizedClassName().asString());
  }

  @Override
  public String memberValue() {
    return String.format(
        "%s(%s, %s)",
        MapListItemMethod.METHOD_NAME, listWriter().asString(), itemWriter().asString());
  }

  @Override
  public Optional<String> flagAssignment() {
    return FlagAssignments.forStandardMemberSetter(member);
  }

  @Override
  public PList<String> getRefs() {
    return listWriter().getRefs().concat(itemWriter().getRefs());
  }

  private Writer listWriter() {
    return javaArrayType
        .getApiType()
        .map(listApiType -> conversionWriter(listApiType, "l"))
        .orElse(javaWriter().print("%s", member.getName()));
  }

  private Writer itemWriter() {
    return javaArrayType
        .getItemType()
        .getApiType()
        .map(itemApiType -> conversionWriter(itemApiType, "item"))
        .map(writer -> javaWriter().print("item -> %s", writer.asString()).refs(writer.getRefs()))
        .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return FromApiTypeConversion.fromApiTypeConversion(apiType, variableName, NULL_SAFE);
  }
}
