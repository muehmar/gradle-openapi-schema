package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

import io.github.muehmar.codegenerator.writer.Writer;

public interface Setter<T extends SetterMember> {
  boolean includeInBuilder(T member);

  String argumentFormat();

  Writer addRefs(Writer writer);
}
