package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

public interface SetterMember {
  String stageClassName();

  String nextStageClassName();

  JavaPojoMember getMember();

  String builderMethodName(PojoSettings settings);

  String argumentType();
}
