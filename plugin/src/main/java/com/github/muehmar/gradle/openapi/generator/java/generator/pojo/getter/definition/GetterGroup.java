package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@PojoBuilder
class GetterGroup {
  private final Predicate<JavaPojoMember> memberFilter;
  private final PList<GetterGenerator> generators;

  @FieldBuilder(fieldName = "generators")
  public static class GeneratorsFieldBuilder {

    static PList<GetterGenerator> generators(GetterGenerator... generators) {
      return PList.fromArray(generators);
    }
  }

  public Generator<JavaPojoMember, PojoSettings> generator() {
    return generators
        .map(generator -> generator.create(memberFilter))
        .foldLeft(Generator.emptyGen(), (gen1, gen2) -> gen1.append(gen2).appendSingleBlankLine());
  }

  public GetterGroup additionalMemberFilter(Predicate<JavaPojoMember> nestedMemberFilter) {
    return new GetterGroup(memberFilter.and(nestedMemberFilter), generators);
  }
}
