package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import java.util.function.Consumer;

public class SampleTypes {
  private SampleTypes() {}

  public static Type SampleType1 =
      new Type() {
        @Override
        public Name getFullName() {
          return Name.of("SampleType1");
        }

        @Override
        public boolean containsPojo() {
          return false;
        }

        @Override
        public boolean isEnum() {
          return false;
        }

        @Override
        public void onEnum(Consumer<PList<String>> code) {}

        @Override
        public PList<String> getEnumMembers() {
          return PList.empty();
        }

        @Override
        public PList<String> getImports() {
          return PList.empty();
        }

        @Override
        public Constraints getConstraints() {
          return Constraints.empty();
        }
      };

  public static Type SampleType2 =
      new Type() {
        @Override
        public Name getFullName() {
          return Name.of("SampleType2");
        }

        @Override
        public boolean containsPojo() {
          return false;
        }

        @Override
        public boolean isEnum() {
          return false;
        }

        @Override
        public void onEnum(Consumer<PList<String>> code) {}

        @Override
        public PList<String> getEnumMembers() {
          return PList.empty();
        }

        @Override
        public PList<String> getImports() {
          return PList.empty();
        }

        @Override
        public Constraints getConstraints() {
          return Constraints.empty();
        }
      };
}
