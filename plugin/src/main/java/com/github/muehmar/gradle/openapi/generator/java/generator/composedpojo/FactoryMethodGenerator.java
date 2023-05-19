package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class FactoryMethodGenerator {
  private FactoryMethodGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(fromMethods())
        .appendSingleBlankLine()
        .append(witherMethods());
  }

  private static Generator<JavaComposedPojo, PojoSettings> fromMethods() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(fromFactoryMethod(), ComposedAndMemberPojo::fromJavaComposedPojo, newLine());
  }

  private static Generator<JavaComposedPojo, PojoSettings> witherMethods() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(witherMethod(), ComposedAndMemberPojo::fromJavaComposedPojo, newLine())
        .filter(JavaComposedPojo::isAnyOf);
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> fromFactoryMethod() {
    final MethodGen<ComposedAndMemberPojo, PojoSettings> method =
        MethodGenBuilder.<ComposedAndMemberPojo, PojoSettings>create()
            .modifiers(PUBLIC, STATIC)
            .noGenericTypes()
            .returnType(pojos -> pojos.composedPojo.getClassName().asString())
            .methodName(
                pojos -> String.format("from%s", pojos.memberPojo.getSchemaName().asIdentifier()))
            .singleArgument(pojos -> String.format("%s dto", pojos.memberPojo.getClassName()))
            .content(fromMethodContent())
            .build();
    final Generator<ComposedAndMemberPojo, PojoSettings> javaDoc =
        JavaDocGenerator.javaDoc(
            (p, s) ->
                String.format(
                    "Creates an instance of {@link %s} from a {@link %s}.",
                    p.getComposedPojo().getClassName(), p.getMemberPojo().getClassName()));
    return javaDoc.append(method);
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> witherMethod() {
    final MethodGen<ComposedAndMemberPojo, PojoSettings> method =
        MethodGenBuilder.<ComposedAndMemberPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(pojos -> pojos.composedPojo.getClassName().asString())
            .methodName(
                pojos -> String.format("with%s", pojos.memberPojo.getSchemaName().asIdentifier()))
            .singleArgument(pojos -> String.format("%s dto", pojos.memberPojo.getClassName()))
            .content(withMethodContent())
            .build();
    final Generator<ComposedAndMemberPojo, PojoSettings> javaDoc =
        JavaDocGenerator.javaDoc(
            (p, s) ->
                String.format(
                    "Returns a new instance adding the supplied {@link %s}. This will overwrite any shared properties with other"
                        + " schemas to the value of the properties in the supplied {@link %s}.",
                    p.getMemberPojo().getClassName(), p.getMemberPojo().getClassName()));
    return javaDoc.append(method);
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> fromMethodContent() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.composedPojo.getClassName()))
        .appendList(
            fromMethodMemberGen(), pojo -> pojo.composedPojo.getMembers().map(pojo::withMember))
        .append(fromMethodAdditionalProperties(), 1)
        .append(w -> w.println(");"));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> withMethodContent() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.composedPojo.getClassName()))
        .appendList(
            withMethodMemberGen(), pojo -> pojo.composedPojo.getMembers().map(pojo::withMember))
        .append(withMethodAdditionalProperties())
        .append(w -> w.println(");"));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> fromMethodAdditionalProperties() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append(
            constant(
                "dto.getAdditionalProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))"))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_MAP))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_STREAM_COLLECTORS));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> withMethodAdditionalProperties() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append(constant("dto.getAdditionalProperties().entrySet().stream()"), 1)
        .append(
            (p, s, w) ->
                w.println(
                    ".collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (p1, p2) -> p1, () -> new HashMap<>(%s)))",
                    JavaAdditionalProperties.getPropertyName()),
            2)
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_MAP))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_STREAM_COLLECTORS));
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> fromMethodMemberGen() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(discriminator(), 1)
        .append(requiredMember(), 1)
        .append(requiredMemberForOtherObjectFromMethod(), 1)
        .append(requiredNullableMember(), 1)
        .append(requiredNullableMemberForOtherObjectFromMethod(), 1)
        .append(optionalMember(), 1)
        .append(optionalMemberForOtherObjectFromMethod(), 1)
        .append(optionalNullableMember(), 1)
        .append(optionalNullableMemberForOtherObjectFromMethod(), 1);
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> withMethodMemberGen() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(requiredMember(), 1)
        .append(requiredMemberForOtherObjectWithMethod(), 1)
        .append(requiredNullableMember(), 1)
        .append(requiredNullableMemberForOtherObjectWithMethod(), 1)
        .append(optionalMember(), 1)
        .append(optionalMemberForOtherObjectWithMethod(), 1)
        .append(optionalNullableMember(), 1)
        .append(optionalNullableMemberForOtherObjectWithMethod(), 1);
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> requiredMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%s(),", p.member.getGetterNameWithSuffix(s)))
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
                                "\"%s\",",
                                d.getValueForSchemaName(
                                    p.getMemberPojo().getSchemaName().asName())))
                    .orElse(w))
        .filter(ComposedAndMemberPojoAndMember::isDiscriminator);
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredMemberForOtherObjectFromMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(constant("null,"))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(
            pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredMemberForOtherObjectWithMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s,", p.member.getNameAsIdentifier()))
        .filter(
            pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> requiredNullableMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%sOr(null),", p.member.getGetterName()))
        .append((p, s, w) -> w.println("true,"))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredNullableMemberForOtherObjectFromMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(w -> w.println("null,"))
        .append(constant("false,"))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      requiredNullableMemberForOtherObjectWithMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s,", p.member.getNameAsIdentifier()))
        .append((p, s, w) -> w.println("%s,", p.member.getIsPresentFlagName()))
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> optionalMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%sOr(null),", p.member.getGetterName()))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalMemberForOtherObjectFromMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(constant("null,"))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(
            pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalMemberForOtherObjectWithMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s,", p.member.getNameAsIdentifier()))
        .filter(
            pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings> optionalNullableMember() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "dto.%s().%s,",
                    p.member.getGetterNameWithSuffix(s), p.member.tristateToProperty()))
        .append(
            (p, s, w) ->
                w.println(
                    "dto.%s().%s,",
                    p.member.getGetterNameWithSuffix(s), p.member.tristateToIsNullFlag()))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalNullableMemberForOtherObjectFromMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append(w -> w.println("null,"))
        .append(constant("false,"))
        .filter(ComposedAndMemberPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<ComposedAndMemberPojoAndMember, PojoSettings>
      optionalNullableMemberForOtherObjectWithMethod() {
    return Generator.<ComposedAndMemberPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("%s,", p.member.getNameAsIdentifier()))
        .append((p, s, w) -> w.println("%s,", p.member.getIsNullFlagName()))
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

    boolean isDiscriminator() {
      return composedPojo
          .getDiscriminator()
          .map(d -> d.getPropertyName().equals(member.getName().asName()))
          .orElse(false);
    }

    boolean isNotDiscriminator() {
      return not(isDiscriminator());
    }

    boolean isMemberOfMemberPojo() {
      return memberPojo
          .getMembersOrEmpty()
          .exists(pojoMember -> pojoMember.getName().equals(member.getName()));
    }

    boolean isNotMemberOfMemberPojo() {
      return not(isMemberOfMemberPojo());
    }
  }
}
