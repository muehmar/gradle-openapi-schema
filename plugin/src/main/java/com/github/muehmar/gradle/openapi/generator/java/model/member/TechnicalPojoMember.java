package com.github.muehmar.gradle.openapi.generator.java.model.member;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;

import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaBooleanType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import lombok.Value;

/**
 * Represents a technical member of a pojo, i.e. a generated pojo contains this member as class
 * member. It may also be a helper flag to represent an additional state of the actual member
 * represented by {@link JavaPojoMember}. This means, an actual member {@link JavaPojoMember} may
 * result in more than one {@link TechnicalPojoMember} in a pojo to be able to fully represent the
 * state of a member (for example tristate members aka optional nullable members).
 */
@Value
public class TechnicalPojoMember {
  JavaName name;
  JavaType javaType;

  public static TechnicalPojoMember additionalProperties(JavaType valueType) {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), valueType.getType());
    final JavaMapType javaMapType = JavaMapType.wrap(mapType, TypeMappings.empty());
    return new TechnicalPojoMember(additionalPropertiesName(), javaMapType);
  }

  public static TechnicalPojoMember isPresentFlagMember(JavaName name) {
    return new TechnicalPojoMember(name, JavaBooleanType.createPrimitive());
  }

  public static TechnicalPojoMember isNullFlagMember(JavaName name) {
    return new TechnicalPojoMember(name, JavaBooleanType.createPrimitive());
  }

  public static TechnicalPojoMember isNotNullFlagMember(JavaName name) {
    return new TechnicalPojoMember(name, JavaBooleanType.createPrimitive());
  }

  public static TechnicalPojoMember wrapPojoMember(JavaPojoMember member) {
    return new TechnicalPojoMember(member.getName(), member.getJavaType());
  }

  public static TechnicalPojoMember wrapJavaObjectPojo(JavaObjectPojo pojo) {
    return new TechnicalPojoMember(
        pojo.getClassName().startLowerCase(), JavaObjectType.fromObjectPojo(pojo));
  }
}
