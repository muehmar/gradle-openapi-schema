package com.github.muehmar.gradle.openapi.generator.java.model.name;

import ch.bluecare.commons.data.PList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The names already taken within a single generated class, used to rename the declaration-only
 * names of a property which would collide with them. See {@code doc/115_name_collisions.md}.
 *
 * <p>A scope holds one kind of name at a time - getters, setters or fields - as a method and a
 * field of the same name do not collide in Java.
 */
@EqualsAndHashCode
@ToString
public class MemberNameScope {
  private static final MemberNameScope EMPTY = new MemberNameScope(PList.empty());

  private final PList<String> takenNames;

  private MemberNameScope(PList<String> takenNames) {
    this.takenNames = takenNames;
  }

  /** A scope without any sibling, resolving every anchor to its plain name. */
  public static MemberNameScope empty() {
    return EMPTY;
  }

  public static MemberNameScope ofTakenNames(PList<JavaName> names) {
    return new MemberNameScope(names.map(JavaName::asString));
  }

  /** The same scope with the given names additionally taken. */
  public MemberNameScope add(PList<JavaName> names) {
    return new MemberNameScope(takenNames.concat(names.map(JavaName::asString)));
  }

  /** {@code plainName}, or the name suffixed with the lowest free counter if it is taken. */
  public JavaName resolveAnchorName(JavaName plainName) {
    if (isFree(plainName)) {
      return plainName;
    }
    for (int counter = 1; ; counter++) {
      final JavaName candidate = plainName.append(String.valueOf(counter));
      if (isFree(candidate)) {
        return candidate;
      }
    }
  }

  /**
   * {@code plainName}, or the name suffixed with an underscore and then the lowest free counter if
   * it is taken, e.g. {@code isNamePresent_} next to the property {@code isNamePresent}.
   */
  public JavaName resolveFieldName(JavaName plainName) {
    if (isFree(plainName)) {
      return plainName;
    }
    final JavaName underscored = plainName.append("_");
    if (isFree(underscored)) {
      return underscored;
    }
    for (int counter = 1; ; counter++) {
      final JavaName candidate = underscored.append(String.valueOf(counter));
      if (isFree(candidate)) {
        return candidate;
      }
    }
  }

  private boolean isFree(JavaName name) {
    return not(takenNames.exists(taken -> taken.equals(name.asString())));
  }

  private static boolean not(boolean value) {
    return !value;
  }
}
