package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberBuilder;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberXml;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.Value;

@Value
public class JavaRequiredAdditionalProperty {
  JavaName name;
  JavaType javaType;

  public static JavaRequiredAdditionalProperty fromNameAndType(Name name, JavaType javaType) {
    return new JavaRequiredAdditionalProperty(JavaName.fromName(name), javaType);
  }

  public String getDescription() {
    return String.format("Additional Property '%s'", name);
  }

  public JavaPojoMember asMember(JavaPojoName pojoName) {
    return JavaPojoMemberBuilder.fullJavaPojoMemberBuilder()
        .pojoName(pojoName)
        .name(name)
        .description(getDescription())
        .javaType(javaType)
        .necessity(Necessity.REQUIRED)
        .type(JavaPojoMember.MemberType.ALL_OF_MEMBER)
        .memberXml(JavaPojoMemberXml.noDefinition())
        .build();
  }

  public boolean isAnyType() {
    return javaType.isAnyType();
  }

  public boolean isNotAnyType() {
    return not(isAnyType());
  }
}
