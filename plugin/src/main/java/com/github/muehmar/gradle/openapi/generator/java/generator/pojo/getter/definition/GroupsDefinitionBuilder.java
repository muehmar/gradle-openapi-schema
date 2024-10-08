package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupBuilder.fullGetterGroupBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

class GroupsDefinitionBuilder {
  private GroupsDefinitionBuilder() {}

  static GetterGroup group(Predicate<JavaPojoMember> memberFilter, GetterGenerator... generators) {
    return fullGetterGroupBuilder().memberFilter(memberFilter).generators(generators).build();
  }

  @SafeVarargs
  static PList<GetterGroup> groups(PList<GetterGroup>... groups) {
    return PList.fromArray(groups).flatMap(g -> g);
  }

  static PList<GetterGroup> nested(
      Predicate<JavaPojoMember> nestedMemberFilter,
      PList<GetterGroup> nestedGroups,
      GetterGroup... nestedSingleGroups) {
    return PList.fromArray(nestedSingleGroups)
        .concat(nestedGroups)
        .map(group -> group.additionalMemberFilter(nestedMemberFilter));
  }

  static PList<GetterGroup> nested(
      Predicate<JavaPojoMember> nestedMemberFilter, GetterGroup... nestedSingleGroups) {
    return nested(nestedMemberFilter, PList.empty(), nestedSingleGroups);
  }

  static GetterGenerator generator(GetterMethod getterMethod) {
    return new GetterGenerator(getterMethod, GetterGeneratorSettings.empty());
  }

  static GetterGenerator generator(GetterMethod getterMethod, GetterGeneratorSetting... settings) {
    return new GetterGenerator(
        getterMethod, new GetterGeneratorSettings(PList.fromArray(settings)));
  }
}
