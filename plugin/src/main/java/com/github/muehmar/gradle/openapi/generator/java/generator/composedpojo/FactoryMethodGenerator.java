package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class FactoryMethodGenerator {
  private FactoryMethodGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(
            fromFactoryMethod().prependNewLine(),
            composedPojo ->
                composedPojo
                    .getJavaPojos()
                    .map(memberPojo -> new ComposedAndMemberPojo(composedPojo, memberPojo)));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> fromFactoryMethod() {
    return MethodGenBuilder.<ComposedAndMemberPojo, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType(pojos -> pojos.composedPojo.getName().asString())
        .methodName(
            pojos -> String.format("from%s", pojos.memberPojo.getName().getName().startUpperCase()))
        .singleArgument(
            pojos -> String.format("%s dto", pojos.memberPojo.getName().startUppercase()))
        .content(fromFactoryMethodContent())
        .build();
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> fromFactoryMethodContent() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.composedPojo.getName()))
        .appendList(gen(), pojos -> pojos.composedPojo.getMembers().map(pojos::withMember))
        .append(w -> w.println(");"));
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> gen() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(discriminator(), 1)
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
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> discriminator() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                p.getComposedPojo()
                    .getDiscriminator()
                    .map(
                        d ->
                            w.println(
                                "\"%s\"%s",
                                d.getValueForPojoName(p.getMemberPojo().getName()),
                                p.commaAfterParameter()))
                    .orElse(w))
        .filter(ComposedAndMemberPojoAndMember::isDiscriminator);
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("null%s", p.commaAfterParameter()))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(
            pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> requiredNullableMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%sOr(null),", p.member.getGetterName()))
        .append((p, s, w) -> w.println("true%s", p.commaAfterParameter()))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredNullableMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(w -> w.println("null,"))
        .append((p, s, w) -> w.println("false%s", p.commaAfterParameter()))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> optionalMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%sOr(null),", p.member.getGetterName()))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(w -> w.println("null,"))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
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
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalNullableMemberForOtherObject() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(w -> w.println("null,"))
        .append((p, s, w) -> w.println("false%s", p.commaAfterParameter()))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  @Value
  private static class ComposedAndMemberPojo {
    JavaComposedPojo composedPojo;
    JavaPojo memberPojo;

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

    boolean isNotDiscriminator() {
      return not(isDiscriminator());
    }

    boolean isMemberOfMemberPojo() {
      return memberPojo.getMembersOrEmpty().exists(member::equals);
    }

    boolean isNotMemberOfMemberPojo() {
      return not(isMemberOfMemberPojo());
    }
  }
}
