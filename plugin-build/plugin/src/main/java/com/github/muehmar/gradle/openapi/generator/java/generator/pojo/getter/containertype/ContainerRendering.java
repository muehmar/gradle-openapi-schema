package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.containertype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.ListMemberMappingWriter.fullAutoListMemberMappingWriter;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.MapMemberMappingWriter.fullAutoMapMemberMappingWriter;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import io.github.muehmar.codegenerator.writer.Writer;

class ContainerRendering {
  private ContainerRendering() {}

  static String returnType(JavaPojoMember member) {
    return member
        .getJavaType()
        .getWriteableParameterizedClassName()
        .asStringWrappingNullableValueType();
  }

  static Writer returnMappedContainer(JavaPojoMember member) {
    return member.getJavaType().isArrayType()
        ? fullAutoListMemberMappingWriter(member, "return ")
        : fullAutoMapMemberMappingWriter(member, "return ");
  }
}
