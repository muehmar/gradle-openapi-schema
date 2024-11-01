package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGroupBuilder.fullSetterGroupBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.function.Predicate;

class GroupsDefinitionBuilder {
  private GroupsDefinitionBuilder() {}

  static SetterGroup group(Predicate<JavaPojoMember> memberFilter, SetterGenerator... generators) {
    return fullSetterGroupBuilder().memberFilter(memberFilter).generators(generators).build();
  }

  @SafeVarargs
  static PList<SetterGroup> groups(PList<SetterGroup>... groups) {
    return PList.fromArray(groups).flatMap(g -> g);
  }

  static PList<SetterGroup> nested(
      Predicate<JavaPojoMember> nestedMemberFilter,
      PList<SetterGroup> nestedGroups,
      SetterGroup... nestedSingleGroups) {
    return PList.fromArray(nestedSingleGroups)
        .concat(nestedGroups)
        .map(group -> group.additionalMemberFilter(nestedMemberFilter));
  }

  static PList<SetterGroup> nested(
      Predicate<JavaPojoMember> nestedMemberFilter, SetterGroup... nestedSingleGroups) {
    return nested(nestedMemberFilter, PList.empty(), nestedSingleGroups);
  }

  static SetterGenerator generator(SetterMethod getterMethod) {
    return new SetterGenerator(getterMethod, SetterGeneratorSettings.empty());
  }

  static SetterGenerator generator(SetterMethod getterMethod, SetterGeneratorSetting... settings) {
    return new SetterGenerator(
        getterMethod, new SetterGeneratorSettings(PList.fromArray(settings)));
  }
}
