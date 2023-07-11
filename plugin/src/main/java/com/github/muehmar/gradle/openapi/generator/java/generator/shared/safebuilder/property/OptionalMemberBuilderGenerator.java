package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleMemberSetterGenerator.singleMemberSetterGenerator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SetterBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleMemberSetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import lombok.Value;

public class OptionalMemberBuilderGenerator {

  private static final SingleMemberSetterGenerator.Setter<OptionalMember> NORMAL_SETTER =
      SetterBuilder.<OptionalMember>create()
          .includeInBuilder(ignore -> true)
          .argumentFormat("%s %s")
          .addRefs(writer -> writer)
          .build();
  private static final SingleMemberSetterGenerator.Setter<OptionalMember> OPTIONAL_SETTER =
      SetterBuilder.<OptionalMember>create()
          .includeInBuilder(OptionalMember::isNotNullable)
          .argumentFormat("Optional<%s> %s")
          .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
          .build();
  private static final SingleMemberSetterGenerator.Setter<OptionalMember> TRISTATE_SETTER =
      SetterBuilder.<OptionalMember>create()
          .includeInBuilder(OptionalMember::isNullable)
          .argumentFormat("Tristate<%s> %s")
          .addRefs(writer -> writer.ref(OpenApiUtilRefs.TRISTATE))
          .build();

  private OptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    final PList<SingleMemberSetterGenerator.Setter<OptionalMember>> setters =
        PList.of(NORMAL_SETTER, OPTIONAL_SETTER, TRISTATE_SETTER);
    final Generator<OptionalMember, PojoSettings> singleMemberSetterGenerator =
        singleMemberSetterGenerator(setters);
    final Generator<OptionalMember, PojoSettings> singleBuilderClassGenerator =
        singleBuilderClassGenerator(OptionalMember::builderClassName, singleMemberSetterGenerator);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            singleBuilderClassGenerator, OptionalMember::fromObjectPojo, Generator.newLine());
  }

  @Value
  private static class OptionalMember implements SingleMemberSetterGenerator.Member {
    OptionalPropertyBuilderName builderName;
    JavaPojoMember member;
    int idx;

    private static PList<OptionalMember> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getMembers()
          .filter(JavaPojoMember::isOptional)
          .zipWithIndex()
          .map(
              p ->
                  new OptionalMember(
                      OptionalPropertyBuilderName.from(pojo, p.second()), p.first(), p.second()));
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

    public boolean isNotNullable() {
      return member.isNotNullable();
    }
  }
}
