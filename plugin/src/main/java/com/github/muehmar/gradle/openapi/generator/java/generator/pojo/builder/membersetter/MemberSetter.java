package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitype.ApiTypeMemberSetters;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypelist.ApiTypeListMemberSetters;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.apitypemap.ApiTypeMapMemberSetters;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.nullableitemslist.NullableItemsListMemberSetters;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter.standardsetters.StandardMemberSetters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

public interface MemberSetter {

  static PList<MemberSetter> fromMember(JavaPojoMember member) {
    return PList.<MemberSetter>single(new JacksonMemberSetter(member))
        .concat(StandardMemberSetters.fromMember(member))
        .concat(ApiTypeMemberSetters.fromMember(member))
        .concat(ApiTypeListMemberSetters.fromMember(member))
        .concat(NullableItemsListMemberSetters.fromMember(member))
        .concat(ApiTypeMapMemberSetters.fromMember(member));
  }

  boolean shouldBeUsed(PojoSettings settings);

  JavaPojoMember getMember();

  default Generator<MemberSetter, PojoSettings> annotationGenerator() {
    return JacksonAnnotationGenerator.jsonIgnore();
  }

  default String methodSuffix() {
    return "";
  }

  JavaModifier modifier(PojoSettings settings);

  String argumentType();

  default String memberValue() {
    return getMember().getName().asString();
  }

  default Writer memberAssigment() {
    return javaWriter().println("this.%s = %s;", getMember().getName(), memberValue());
  }

  Optional<String> flagAssignment();

  default PList<String> getRefs() {
    return PList.empty();
  }
}
