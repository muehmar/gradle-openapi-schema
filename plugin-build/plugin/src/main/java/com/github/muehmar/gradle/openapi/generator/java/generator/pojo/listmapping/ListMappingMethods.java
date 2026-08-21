package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class ListMappingMethods {
  private ListMappingMethods() {}

  public static Generator<JavaObjectPojo, PojoSettings> unmapListMethods() {
    return UnmapListMethod.<JavaObjectPojo, PojoSettings>unmapListMethod()
        .appendSingleBlankLine()
        .append(MapListMethod.mapListMethod())
        .filter(ListMappingMethods::needsListMappingMethods);
  }

  private static boolean needsListMappingMethods(JavaObjectPojo pojo) {
    return pojo.getAllMembers().map(JavaPojoMember::getJavaType).exists(JavaType::isArrayType);
  }
}
