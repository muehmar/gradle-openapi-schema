package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

public interface IncludeInBuilder<T extends SetterMember> {
  boolean includeInBuilder(T member);
}
