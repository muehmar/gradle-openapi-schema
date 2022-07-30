package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import java.util.Objects;
import java.util.function.Consumer;

// TODO: Type should be language agnostic, i.e. the should be mapped to a generic type. The type
// could still be an interface but with different implementations for enum/array etc.
public interface Type {
  /** Returns the full-name of this type, i.e. it includes also any generic type. */
  Name getFullName();

  /**
   * Returns true in case this type contains a pojo as generic type or is a pojo itself. False in
   * case its a simple type like {@link String} or similar. It may sometimes return true although
   * the type is not really a pojo, as the plugin allows to use user defined types for which we have
   * no info what kind of type it is.
   */
  boolean containsPojo();

  boolean isEnum();

  /**
   * The provided {@code code} is executed in case this type is an enum with the list of members in
   * the enum as arguments.
   */
  void onEnum(Consumer<PList<String>> code);

  PList<String> getEnumMembers();

  PList<String> getImports();

  Constraints getConstraints();

  static Type simpleOfName(Name name) {
    return new Type() {
      @Override
      public Name getFullName() {
        return name;
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

      @Override
      public boolean equals(Object o) {
        if (this == o) {
          return true;
        }
        if (o == null || getClass() != o.getClass()) {
          return false;
        }
        Type type = (Type) o;
        return containsPojo() == type.containsPojo()
            && Objects.equals(getFullName(), type.getFullName())
            && Objects.equals(getImports(), type.getImports())
            && Objects.equals(getEnumMembers(), type.getEnumMembers())
            && Objects.equals(getConstraints(), type.getConstraints());
      }

      @Override
      public int hashCode() {
        return Objects.hash(name, getImports(), getEnumMembers(), getConstraints(), containsPojo());
      }

      @Override
      public String toString() {
        return "Type{"
            + "name='"
            + name
            + '\''
            + ", imports="
            + getImports()
            + ", enumMembers="
            + getEnumMembers()
            + ", constraints="
            + getConstraints()
            + ", containsPojo="
            + containsPojo()
            + '}';
      }
    };
  }
}
