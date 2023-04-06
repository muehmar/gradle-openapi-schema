package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import lombok.Value;

class RequiredMemberBuilderGenerator {

  private static final SingleMemberBuilderGenerator.Setter<RequiredMember> NORMAL_SETTER =
      new SingleMemberBuilderGenerator.Setter<RequiredMember>() {

        @Override
        public boolean includeInBuilder(RequiredMember member) {
          return true;
        }

        @Override
        public String argumentFormat() {
          return "%s %s";
        }

        @Override
        public Writer addRefs(Writer writer) {
          return writer;
        }
      };
  private static final SingleMemberBuilderGenerator.Setter<RequiredMember> OPTIONAL_SETTER =
      new SingleMemberBuilderGenerator.Setter<RequiredMember>() {

        @Override
        public boolean includeInBuilder(RequiredMember member) {
          return member.isNullable();
        }

        @Override
        public String argumentFormat() {
          return "Optional<%s> %s";
        }

        @Override
        public Writer addRefs(Writer writer) {
          return writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL);
        }
      };

  private RequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    final PList<SingleMemberBuilderGenerator.Setter<RequiredMember>> setters =
        PList.of(NORMAL_SETTER, OPTIONAL_SETTER);
    final Generator<RequiredMember, PojoSettings> singleMemberGenerator =
        SingleMemberBuilderGenerator.generator(setters);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(singleMemberGenerator, RequiredMember::fromObjectPojo, Generator.newLine());
  }

  @Value
  private static class RequiredMember implements SingleMemberBuilderGenerator.Member {
    JavaPojoMember member;
    int idx;

    private static PList<RequiredMember> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getMembers()
          .filter(JavaPojoMember::isRequired)
          .zipWithIndex()
          .map(p -> new RequiredMember(p.first(), p.second()));
    }

    public String builderClassName() {
      return String.format("Builder%d", idx);
    }

    public String nextBuilderClassName() {
      return String.format("Builder%d", idx + 1);
    }

    public boolean isNullable() {
      return member.isNullable();
    }
  }
}
