package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.maplistitem;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class ListItemMappers {
  private ListItemMappers() {}

  public static Generator<JavaObjectPojo, PojoSettings> listItemMappers() {
    return Generator.emptyGen();
  }
}
