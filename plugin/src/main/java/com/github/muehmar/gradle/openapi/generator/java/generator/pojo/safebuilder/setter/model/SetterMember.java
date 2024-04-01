package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import lombok.Value;

@Value
public class SetterMember {
  BuilderStage nextStage;
  JavaPojoMember member;

  public String nextStageClassName() {
    return nextStage.getName();
  }
}
