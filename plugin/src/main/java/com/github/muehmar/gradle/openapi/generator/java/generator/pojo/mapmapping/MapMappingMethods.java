package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class MapMappingMethods {
  private MapMappingMethods() {}

  public static Generator<JavaObjectPojo, PojoSettings> mapMappingMethods() {
    return UnmapMapMethod.<JavaObjectPojo, PojoSettings>unmapMapMethod()
        .appendSingleBlankLine()
        .append(MapMapMethod.mapMapMethod())
        .filter(MapMappingMethods::needsMapMappingMethods);
  }

  private static boolean needsMapMappingMethods(JavaObjectPojo pojo) {
    return pojo.getAllMembers().map(JavaPojoMember::getJavaType).exists(JavaType::isMapType);
  }
}
