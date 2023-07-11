package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleMemberSetterGenerator.singleMemberSetterGenerator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SetterBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleMemberSetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import lombok.Value;

public class RequiredMemberBuilderGenerator {

  private static final SingleMemberSetterGenerator.Setter<RequiredMember> NORMAL_SETTER =
      SetterBuilder.<RequiredMember>create()
          .includeInBuilder(ignore -> true)
          .argumentFormat("%s %s")
          .addRefs(writer -> writer)
          .build();
  private static final SingleMemberSetterGenerator.Setter<RequiredMember> OPTIONAL_SETTER =
      SetterBuilder.<RequiredMember>create()
          .includeInBuilder(RequiredMember::isNullable)
          .argumentFormat("Optional<%s> %s")
          .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
          .build();

  private RequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    final PList<SingleMemberSetterGenerator.Setter<RequiredMember>> setters =
        PList.of(NORMAL_SETTER, OPTIONAL_SETTER);
    final Generator<RequiredMember, PojoSettings> singleMemberSetterGenerator =
        singleMemberSetterGenerator(setters);
    final Generator<RequiredMember, PojoSettings> singleBuilderClassGenerator =
        singleBuilderClassGenerator(RequiredMember::builderClassName, singleMemberSetterGenerator);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            singleBuilderClassGenerator, RequiredMember::fromObjectPojo, Generator.newLine());
  }

  @Value
  private static class RequiredMember implements SingleMemberSetterGenerator.Member {
    RequiredPropertyBuilderName builderName;
    JavaPojoMember member;
    int idx;

    private static PList<RequiredMember> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getMembers()
          .filter(JavaPojoMember::isRequired)
          .zipWithIndex()
          .map(
              p ->
                  new RequiredMember(
                      RequiredPropertyBuilderName.from(pojo, p.second()), p.first(), p.second()));
    }

    @Override
    public String builderClassName() {
      return builderName.currentName();
    }

    @Override
    public String nextBuilderClassName() {
      return builderName.incrementIndex().currentName();
    }

    public boolean isNullable() {
      return member.isNullable();
    }
  }
}
