package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ANY_OF;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfWithMethod {
  private AnyOfWithMethod() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(witherMethod(), ComposedAndMemberPojo::fromJavaComposedPojo, newLine())
        .filter(p -> p.getCompositionType().equals(ANY_OF));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> witherMethod() {
    return MethodGenBuilder.<ComposedAndMemberPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(pojos -> pojos.composedPojo.getName().asString())
        .methodName(
            pojos -> String.format("with%s", pojos.memberPojo.getName().getName().startUpperCase()))
        .singleArgument(
            pojos -> String.format("%s dto", pojos.memberPojo.getName().startUppercase()))
        .content(witherMethodContent())
        .build();
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> witherMethodContent() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.composedPojo.getName()))
        .appendList(gen(), pojos -> pojos.composedPojo.getMembers().map(pojos::withMember))
        .append(w -> w.println(");"));
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> gen() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(requiredMember(), 1)
        .append(requiredMemberForOtherObject(), 1)
        .append(requiredNullableMember(), 1)
        .append(requiredNullableMemberForOtherObject(), 1)
        .append(optionalMember(), 1)
        .append(optionalMemberForOtherObject(), 1)
        .append(optionalNullableMember(), 1)
        .append(optionalNullableMemberForOtherObject(), 1);
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> requiredMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "dto.%s()%s", p.member.getGetterNameWithSuffix(s), p.commaAfterParameter()))
        .filter(pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s%s", p.member.getName(), p.commaAfterParameter()))
        .filter(
            pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> requiredNullableMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%sOr(null),", p.member.getGetterName()))
        .append((p, s, w) -> w.println("true%s", p.commaAfterParameter()))
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredNullableMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s,", p.member.getName()))
        .append(
            (p, s, w) ->
                w.println("%s%s", p.member.getIsPresentFlagName(), p.commaAfterParameter()))
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> optionalMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println("dto.%sOr(null)%s", p.member.getGetterName(), p.commaAfterParameter()))
        .filter(pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s%s", p.member.getName(), p.commaAfterParameter()))
        .filter(
            pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> optionalNullableMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "dto.%s().onValue(val -> val).onNull(() -> null).onAbsent(() -> null),",
                    p.member.getGetterNameWithSuffix(s)))
        .append(
            (p, s, w) ->
                w.println(
                    "dto.%s().onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false)%s",
                    p.member.getGetterNameWithSuffix(s), p.commaAfterParameter()))
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalNullableMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s,", p.member.getName()))
        .append(
            (p, s, w) -> w.println("%s%s", p.member.getIsNullFlagName(), p.commaAfterParameter()))
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  @Value
  private static class ComposedAndMemberPojo {
    JavaComposedPojo composedPojo;
    JavaPojo memberPojo;

    static PList<ComposedAndMemberPojo> fromJavaComposedPojo(JavaComposedPojo pojo) {
      return pojo.getJavaPojos().map(memberPojo -> new ComposedAndMemberPojo(pojo, memberPojo));
    }

    ComposedAndMemberPojoAndMember withMember(JavaPojoMember member) {
      return new ComposedAndMemberPojoAndMember(composedPojo, memberPojo, member);
    }
  }

  @Value
  private static class ComposedAndMemberPojoAndMember {
    JavaComposedPojo composedPojo;
    JavaPojo memberPojo;
    JavaPojoMember member;

    String commaAfterParameter() {
      return composedPojo.getMembersOrEmpty().indexOf(member, JavaPojoMember::equals)
              < composedPojo.getMembersOrEmpty().size() - 1
          ? ","
          : "";
    }

    boolean isDiscriminator() {
      return composedPojo
          .getDiscriminator()
          .map(d -> d.getPropertyName().equals(member.getName()))
          .orElse(false);
    }

    boolean isMemberOfMemberPojo() {
      return memberPojo.getMembersOrEmpty().exists(member::equals);
    }

    boolean isNotMemberOfMemberPojo() {
      return not(isMemberOfMemberPojo());
    }
  }
}
