package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.BuildMethod;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.Value;

@PojoBuilder(builderName = "SetterBuilder")
@Value
public class SetterBuilderImpl<T extends SetterMember> {
  IncludeInBuilder<T> includeInBuilder;
  String typeFormat;
  AddRefs addRefs;

  @BuildMethod
  public static <T extends SetterMember> Setter<T> buildSetter(SetterBuilderImpl<T> setter) {
    return new Setter<T>() {
      @Override
      public boolean includeInBuilder(T member) {
        return setter.includeInBuilder.includeInBuilder(member);
      }

      @Override
      public String argumentFormat() {
        return setter.typeFormat;
      }

      @Override
      public Writer addRefs(Writer writer) {
        return setter.addRefs.addRefs(writer);
      }
    };
  }
}
