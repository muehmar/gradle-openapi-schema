package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.AdditionalPropertiesCastMethod.additionalPropertiesCastMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.FrameworkAdditionalPropertiesGetter.frameworkAdditionalPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.GetAdditionalPropertiesList.getAdditionalPropertiesListGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.SingleAdditionalPropertyGetter.singleAdditionalPropertyGetterGenerator;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class AdditionalPropertiesGetter {
  private AdditionalPropertiesGetter() {}

  public static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesGetterGenerator() {
    return frameworkAdditionalPropertiesGetterGenerator()
        .appendSingleBlankLine()
        .append(getAdditionalPropertiesListGenerator())
        .appendSingleBlankLine()
        .append(singleAdditionalPropertyGetterGenerator())
        .appendSingleBlankLine()
        .append(additionalPropertiesCastMethodGenerator())
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }
}
