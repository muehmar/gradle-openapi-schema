package com.github.muehmar.gradle.openapi.generator.java.model.member;

import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import lombok.Value;

/**
 * This key represents the technical properties of a member, i.e. two members have the same key if
 * they are technically interchangeable. Two members have the same key if the name and the type with
 * the constraints are the same. This means, two members with the same {@link TechnicalMemberKey}
 * should also result in the exact same {@link TechnicalPojoMember}'s.
 */
@Value
public class TechnicalMemberKey {
  JavaName name;
  JavaType javaType;
  Necessity necessity;
  Nullability nullability;
}
