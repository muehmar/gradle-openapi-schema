package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Predicate;
import lombok.AllArgsConstructor;

/** The getters generated for all members matching {@code memberFilter}, in the order given. */
@AllArgsConstructor
class GetterGroup {
  private final Predicate<JavaPojoMember> memberFilter;
  private final PList<GetterGenerator> generators;

  public Generator<JavaPojoMember, PojoSettings> generator() {
    return generators
        .map(generator -> generator.create(memberFilter))
        .foldLeft(
            Generator.<JavaPojoMember, PojoSettings>emptyGen(),
            (gen1, gen2) -> gen1.append(gen2).appendSingleBlankLine())
        .append(RefsGenerator.fieldRefs());
  }
}
