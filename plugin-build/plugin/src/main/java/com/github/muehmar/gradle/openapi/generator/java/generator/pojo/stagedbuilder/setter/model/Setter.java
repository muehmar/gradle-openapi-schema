package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;

public interface Setter {

  Setter forType(SetterBuilderImpl.SetterType setterType);

  boolean includeInBuilder(SetterMember member);

  String methodName(SetterMember member, PojoSettings settings);

  String argumentType(SetterMember member);

  Writer addRefs(Writer writer);
}
