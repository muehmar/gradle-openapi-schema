package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator.singleMemberSetterGenerator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SetterBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberBuilder;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import lombok.Value;

public class RequiredMemberBuilderGenerator {

  private static final SingleMemberSetterGenerator.Setter<RequiredMember> NORMAL_SETTER =
      SetterBuilder.<RequiredMember>create()
          .includeInBuilder(ignore -> true)
          .typeFormat("%s")
          .addRefs(writer -> writer)
          .build();
  private static final SingleMemberSetterGenerator.Setter<RequiredMember> OPTIONAL_SETTER =
      SetterBuilder.<RequiredMember>create()
          .includeInBuilder(RequiredMember::isNullable)
          .typeFormat("Optional<%s>")
          .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
          .build();

  private RequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> requiredMemberBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    final Generator<RequiredMember, PojoSettings> singleMemberSetterMethods =
        singleMemberSetterMethods();
    final Generator<RequiredMember, PojoSettings> singleBuilderClass =
        singleBuilderClassGenerator(RequiredMember::builderClassName, singleMemberSetterMethods);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            singleBuilderClass,
            pojo -> RequiredMember.fromObjectPojo(builderVariant, pojo),
            Generator.newLine());
  }

  public static Generator<JavaObjectPojo, PojoSettings>
      builderMethodsOfFirstRequiredMemberGenerator(SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(
            singleMemberSetterMethods(),
            pojo -> RequiredMember.fromObjectPojo(builderVariant, pojo).headOption());
  }

  private static Generator<RequiredMember, PojoSettings> singleMemberSetterMethods() {
    final PList<SingleMemberSetterGenerator.Setter<RequiredMember>> setters =
        PList.of(NORMAL_SETTER, OPTIONAL_SETTER);
    return singleMemberSetterGenerator(setters);
  }

  @Value
  private static class RequiredMember implements SingleMemberSetterGenerator.Member {
    RequiredPropertyBuilderName builderName;
    JavaPojoMember member;
    int idx;

    private static PList<RequiredMember> fromObjectPojo(
        SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
      final PList<JavaPojoMember> requiredAdditionalPropertiesAsMember =
          pojo.getRequiredAdditionalProperties()
              .map(
                  rp ->
                      JavaPojoMemberBuilder.create()
                          .name(rp.getName())
                          .description(rp.getDescription())
                          .javaType(rp.getJavaType())
                          .necessity(Necessity.REQUIRED)
                          .nullability(Nullability.NOT_NULLABLE)
                          .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
                          .andAllOptionals()
                          .build());
      return pojo.getMembers()
          .filter(JavaPojoMember::isRequired)
          .concat(requiredAdditionalPropertiesAsMember)
          .zipWithIndex()
          .map(
              p ->
                  new RequiredMember(
                      RequiredPropertyBuilderName.from(builderVariant, pojo, p.second()),
                      p.first(),
                      p.second()));
    }

    @Override
    public String builderClassName() {
      return builderName.currentName();
    }

    @Override
    public String nextBuilderClassName() {
      return builderName.nextBuilderName();
    }

    public boolean isNullable() {
      return member.isNullable();
    }
  }
}
