package com.github.muehmar.gradle.openapi.generator.java.model.name;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.warnings.Warning;
import com.github.muehmar.gradle.openapi.warnings.WarningsContext;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The names of the methods every dto carries, resolved against its properties. Only the methods
 * carrying a constraint annotation can collide: they keep their {@code get}/{@code is} prefix for
 * bean validation to discover them, whereas the other framework methods carry no prefix a property
 * can produce. See {@code doc/115_name_collisions.md}.
 */
@EqualsAndHashCode
@ToString
public class FrameworkMethodNames {
  private final JavaPojoName pojoName;
  private final MemberNameScope apiGetterNames;
  private final PList<JavaPojoMember> members;

  private FrameworkMethodNames(
      JavaPojoName pojoName, MemberNameScope apiGetterNames, PList<JavaPojoMember> members) {
    this.pojoName = pojoName;
    this.apiGetterNames = apiGetterNames;
    this.members = members;
  }

  public static FrameworkMethodNames of(
      JavaPojoName pojoName, PList<JavaPojoMember> members, PojoSettings settings) {
    final PList<JavaName> takenNames =
        members.flatMap(member -> apiNamesOf(member, settings)).distinct(JavaName::asString);
    return new FrameworkMethodNames(pojoName, MemberNameScope.ofTakenNames(takenNames), members);
  }

  /** Every name a property may produce which could equal a framework method. */
  private static PList<JavaName> apiNamesOf(JavaPojoMember member, PojoSettings settings) {
    return PList.of(
        member.getGetterName(),
        member.getGetterNameWithSuffix(settings),
        member.getIsPresentFlagName(),
        member.getIsNotNullFlagName(),
        member.getIsNullFlagName());
  }

  /** Public, carries the {@code @Min}/{@code @Max} constraints of min/maxProperties. */
  public JavaName propertyCount(PojoSettings settings) {
    return resolvePublic(MethodNames.Framework.propertyCount(), settings);
  }

  /** Private, carries {@code @AssertTrue}. */
  public JavaName allAdditionalPropertiesHaveCorrectType() {
    return resolvePrivate(MethodNames.Framework.allAdditionalPropertiesHaveCorrectType());
  }

  /** Resolved first and handed to the anchor scope, which has to avoid them. */
  public PList<JavaName> allNames(PojoSettings settings) {
    return PList.of(propertyCount(settings), allAdditionalPropertiesHaveCorrectType());
  }

  private JavaName resolvePublic(JavaName plainName, PojoSettings settings) {
    final JavaName resolved = apiGetterNames.resolveFieldName(plainName);
    if (!resolved.equals(plainName)) {
      collidingProperty(plainName, settings)
          .forEach(
              propertyName ->
                  WarningsContext.addWarningForTask(
                      settings.getTaskIdentifier(),
                      Warning.nameCollision(pojoName, propertyName, plainName, resolved)));
    }
    return resolved;
  }

  private JavaName resolvePrivate(JavaName plainName) {
    return apiGetterNames.resolveFieldName(plainName);
  }

  private PList<JavaName> collidingProperty(JavaName plainName, PojoSettings settings) {
    return members
        .filter(member -> apiNamesOf(member, settings).exists(plainName::equals))
        .map(JavaPojoMember::getName);
  }
}
