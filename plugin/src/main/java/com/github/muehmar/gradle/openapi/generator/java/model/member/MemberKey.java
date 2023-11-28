package com.github.muehmar.gradle.openapi.generator.java.model.member;

import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import lombok.Value;

/**
 * Key of a member, i.e. if two members have the same key, they represent the same property. They
 * may not necessarily be fully interchangeable, as two members may have different constraints (this
 * also includes necessity and nullability). The two members can be represented by the same
 * technical member but the constraints need to be validated independently.
 */
@Value
public class MemberKey {
  JavaName name;
  JavaType javaType;
}
