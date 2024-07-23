package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetterGroups {
  private final PList<GetterGroup> groups;

  public Generator<JavaPojoMember, PojoSettings> generator() {
    return groups
        .map(GetterGroup::generator)
        .foldLeft(Generator.emptyGen(), (gen1, gen2) -> gen1.append(gen2).appendSingleBlankLine());
  }
}
