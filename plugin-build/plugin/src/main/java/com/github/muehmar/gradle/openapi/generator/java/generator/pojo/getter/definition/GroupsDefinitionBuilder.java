package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import ch.bluecare.commons.data.PList;

class GroupsDefinitionBuilder {
  private GroupsDefinitionBuilder() {}

  static GetterGenerator generator(GetterMethod getterMethod) {
    return new GetterGenerator(getterMethod, GetterGeneratorSettings.empty());
  }

  static GetterGenerator generator(GetterMethod getterMethod, GetterGeneratorSetting... settings) {
    return new GetterGenerator(
        getterMethod, new GetterGeneratorSettings(PList.fromArray(settings)));
  }
}
