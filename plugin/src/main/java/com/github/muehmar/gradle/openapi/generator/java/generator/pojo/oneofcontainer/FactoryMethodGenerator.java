package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

class FactoryMethodGenerator {
  private FactoryMethodGenerator() {}

  public static Generator<OneOfContainer, PojoSettings> factoryMethodGenerator() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen().append(fromMethods());
  }

  private static Generator<OneOfContainer, PojoSettings> fromMethods() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen()
        .appendList(fromFactoryMethod(), OneOfContainerAndPojo::fromOneOfContainer, newLine());
  }

  private static Generator<OneOfContainerAndPojo, PojoSettings> fromFactoryMethod() {
    final MethodGen<OneOfContainerAndPojo, PojoSettings> method =
        MethodGenBuilder.<OneOfContainerAndPojo, PojoSettings>create()
            .modifiers(PUBLIC, STATIC)
            .noGenericTypes()
            .returnType(cap -> cap.getOneOfContainer().getContainerName().asString())
            .methodName(pojos -> String.format("from%s", pojos.pojo.getSchemaName().asIdentifier()))
            .singleArgument(pojos -> String.format("%s dto", pojos.pojo.getClassName()))
            .content(fromMethodContent())
            .build();
    final Generator<OneOfContainerAndPojo, PojoSettings> javaDoc =
        JavaDocGenerator.javaDoc(
            (cap, s) ->
                String.format(
                    "Creates an instance of {@link %s} from a {@link %s}.",
                    cap.getOneOfContainer().getContainerName(), cap.getPojo().getClassName()));
    return javaDoc.append(method);
  }

  private static Generator<OneOfContainerAndPojo, PojoSettings> fromMethodContent() {
    return Generator.<OneOfContainerAndPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("return new %s(", p.oneOfContainer.getContainerName()))
        .appendList(
            fromMethodMemberGen(),
            pojo -> pojo.oneOfContainer.getComposition().getMembers().map(pojo::withMember))
        .append(fromMethodAdditionalPropertiesNotAnyType(), 1)
        .append(fromMethodAdditionalPropertiesAnyType(), 1)
        .append(w -> w.println(");"));
  }

  private static Generator<OneOfContainerAndPojo, PojoSettings>
      fromMethodAdditionalPropertiesNotAnyType() {
    return Generator.<OneOfContainerAndPojo, PojoSettings>emptyGen()
        .append(
            constant(
                "dto.getAdditionalProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))"))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_MAP))
        .append(RefsGenerator.ref(JavaRefs.JAVA_UTIL_STREAM_COLLECTORS))
        .filter(container -> container.getPojo().getAdditionalProperties().isNotValueAnyType());
  }

  private static Generator<OneOfContainerAndPojo, PojoSettings>
      fromMethodAdditionalPropertiesAnyType() {
    return Generator.<OneOfContainerAndPojo, PojoSettings>emptyGen()
        .append(constant("dto.getAdditionalProperties()"))
        .filter(container -> container.getPojo().getAdditionalProperties().isValueAnyType());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings> fromMethodMemberGen() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
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

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings> requiredMember() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%s(),", p.member.getGetterNameWithSuffix(s)))
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings> discriminator() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                p.getOneOfContainer()
                    .getComposition()
                    .getDiscriminator()
                    .map(
                        d ->
                            w.println(
                                "\"%s\",",
                                d.getValueForSchemaName(p.getPojo().getSchemaName().asName())))
                    .orElse(w))
        .filter(OneOfContainerAndPojoAndMember::isDiscriminator);
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings>
      requiredMemberForOtherObjectFromMethod() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append(constant("null,"))
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(
            pojos -> pojos.member.isRequiredAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings> requiredNullableMember() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%sOr(null),", p.member.getGetterName()))
        .append((p, s, w) -> w.println("true,"))
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings>
      requiredNullableMemberForOtherObjectFromMethod() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append(w -> w.println("null,"))
        .append(constant("false,"))
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isRequiredAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings> optionalMember() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("dto.%sOr(null),", p.member.getGetterName()))
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings>
      optionalMemberForOtherObjectFromMethod() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append(constant("null,"))
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(
            pojos -> pojos.member.isOptionalAndNotNullable() && pojos.isNotMemberOfMemberPojo());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings> optionalNullableMember() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
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
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isMemberOfMemberPojo());
  }

  private static Generator<OneOfContainerAndPojoAndMember, PojoSettings>
      optionalNullableMemberForOtherObjectFromMethod() {
    return Generator.<OneOfContainerAndPojoAndMember, PojoSettings>emptyGen()
        .append(w -> w.println("null,"))
        .append(constant("false,"))
        .filter(OneOfContainerAndPojoAndMember::isNotDiscriminator)
        .filter(pojos -> pojos.member.isOptionalAndNullable() && pojos.isNotMemberOfMemberPojo());
  }

  @Value
  private static class OneOfContainerAndPojo {
    OneOfContainer oneOfContainer;
    JavaObjectPojo pojo;

    static PList<OneOfContainerAndPojo> fromOneOfContainer(OneOfContainer container) {
      return container
          .getComposition()
          .getPojos()
          .map(memberPojo -> new OneOfContainerAndPojo(container, memberPojo))
          .toPList();
    }

    OneOfContainerAndPojoAndMember withMember(JavaPojoMember member) {
      return new OneOfContainerAndPojoAndMember(oneOfContainer, pojo, member);
    }
  }

  @Value
  private static class OneOfContainerAndPojoAndMember {
    OneOfContainer oneOfContainer;
    JavaObjectPojo pojo;
    JavaPojoMember member;

    boolean isDiscriminator() {
      return oneOfContainer
          .getComposition()
          .getDiscriminator()
          .map(d -> d.getPropertyName().equals(member.getName().asName()))
          .orElse(false);
    }

    boolean isNotDiscriminator() {
      return not(isDiscriminator());
    }

    boolean isMemberOfMemberPojo() {
      return pojo.getAllMembers()
          .exists(pojoMember -> pojoMember.getName().equals(member.getName()));
    }

    boolean isNotMemberOfMemberPojo() {
      return not(isMemberOfMemberPojo());
    }
  }
}
