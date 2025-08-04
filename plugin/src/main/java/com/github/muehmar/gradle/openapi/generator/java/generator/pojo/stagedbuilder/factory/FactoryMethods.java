package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant.FULL;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory.SinglePropertyFactoryMethod.singlePropertyFactoryMethod;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory.StandardFactoryMethod.factoryMethodsForVariant;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class FactoryMethods {

  private FactoryMethods() {}

  public static Generator<JavaObjectPojo, PojoSettings> factoryMethods() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(factoryMethodsForVariant(FULL))
        .appendSingleBlankLine()
        .append(factoryMethodsForVariant(STANDARD))
        .appendSingleBlankLine()
        .append(singlePropertyFactoryMethod());
  }
}
