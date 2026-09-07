package com.github.muehmar.gradle.openapi.generator.java.model.name;

import ch.bluecare.commons.data.PList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The names already taken within a single generated class, used to keep the declaration-only
 * anchors of a property free of the names generated for its siblings (issue #438).
 *
 * <p>A name which is part of the api of the generated class - an api getter, a builder setter or a
 * framework method - is a <i>contract</i> name: it is referenced by hand-written or by generated
 * code and must never be renamed. The anchors, on the other hand, are only ever declared and never
 * referenced, hence a collision is resolved by renaming the anchor: the plain name is tried first,
 * then the counter {@code 1}, {@code 2}, ... is appended until the name is free. A specification
 * without a collision therefore keeps generating the plain name.
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

  /**
   * Resolves the name of an anchor: {@code plainName} if it is not taken by a contract name,
   * otherwise the name suffixed with the lowest free counter.
   */
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

  private boolean isFree(JavaName name) {
    return not(takenNames.exists(taken -> taken.equals(name.asString())));
  }

  private static boolean not(boolean value) {
    return !value;
  }
}
