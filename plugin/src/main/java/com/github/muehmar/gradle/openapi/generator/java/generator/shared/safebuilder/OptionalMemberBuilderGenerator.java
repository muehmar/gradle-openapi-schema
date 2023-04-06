package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import lombok.Value;

class OptionalMemberBuilderGenerator {

  private static final SingleMemberBuilderGenerator.Setter<OptionalMember> NORMAL_SETTER =
      new SingleMemberBuilderGenerator.Setter<OptionalMember>() {

        @Override
        public boolean includeInBuilder(OptionalMember member) {
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
  private static final SingleMemberBuilderGenerator.Setter<OptionalMember> OPTIONAL_SETTER =
      new SingleMemberBuilderGenerator.Setter<OptionalMember>() {

        @Override
        public boolean includeInBuilder(OptionalMember member) {
          return member.isNotNullable();
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
  private static final SingleMemberBuilderGenerator.Setter<OptionalMember> TRISTATE_SETTER =
      new SingleMemberBuilderGenerator.Setter<OptionalMember>() {

        @Override
        public boolean includeInBuilder(OptionalMember member) {
          return member.isNullable();
        }

        @Override
        public String argumentFormat() {
          return "Tristate<%s> %s";
        }

        @Override
        public Writer addRefs(Writer writer) {
          return writer.ref(OpenApiUtilRefs.TRISTATE);
        }
      };

  private OptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    final PList<SingleMemberBuilderGenerator.Setter<OptionalMember>> setters =
        PList.of(NORMAL_SETTER, OPTIONAL_SETTER, TRISTATE_SETTER);
    final Generator<OptionalMember, PojoSettings> singelMemberGenerator =
        SingleMemberBuilderGenerator.generator(setters);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(singelMemberGenerator, OptionalMember::fromObjectPojo, Generator.newLine());
  }

  @Value
  private static class OptionalMember implements SingleMemberBuilderGenerator.Member {
    JavaPojoMember member;
    int idx;

    private static PList<OptionalMember> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getMembers()
          .filter(JavaPojoMember::isOptional)
          .zipWithIndex()
          .map(p -> new OptionalMember(p.first(), p.second()));
    }

    public String builderClassName() {
      return String.format("OptBuilder%d", idx);
    }

    public String nextBuilderClassName() {
      return String.format("OptBuilder%d", idx + 1);
    }

    public boolean isNullable() {
      return member.isNullable();
    }

    public boolean isNotNullable() {
      return member.isNotNullable();
    }
  }
}
