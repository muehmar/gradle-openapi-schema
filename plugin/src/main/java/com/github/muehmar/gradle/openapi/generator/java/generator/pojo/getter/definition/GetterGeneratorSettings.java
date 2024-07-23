package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonProperty;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator.validationAnnotationsForMember;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaDocGenerator.javaDoc;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Predicate;
import lombok.Value;

@Value
public class GetterGeneratorSettings {
  PList<GetterGeneratorSetting> settings;

  public static GetterGeneratorSettings empty() {
    return new GetterGeneratorSettings(PList.empty());
  }

  public static GetterGeneratorSettings getterGeneratorSettings(
      GetterGeneratorSetting... settings) {
    return new GetterGeneratorSettings(PList.fromArray(settings));
  }

  public <T> Predicate<T> validationFilter() {
    return ignore -> isValidation();
  }

  public boolean isValidation() {
    return settings.filter(GetterGeneratorSetting.NO_VALIDATION::equals).isEmpty();
  }

  public <T> Predicate<T> jsonFilter() {
    return ignore -> isJson();
  }

  public boolean isJson() {
    return settings.filter(GetterGeneratorSetting.NO_JSON::equals).isEmpty();
  }

  public boolean isPackagePrivate() {
    return settings.filter(GetterGeneratorSetting.PACKAGE_PRIVATE::equals).nonEmpty();
  }

  public boolean isJavaDoc() {
    return settings.filter(GetterGeneratorSetting.NO_JAVA_DOC::equals).isEmpty();
  }

  public Generator<JavaPojoMember, PojoSettings> jsonIgnoreGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonIgnore())
        .filter(ignore -> not(this.isJson()));
  }

  public Generator<JavaPojoMember, PojoSettings> jsonPropertyGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(jsonProperty())
        .filter(ignore -> this.isJson());
  }

  public Generator<JavaPojoMember, PojoSettings> javaDocGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .filter(ignore -> this.isJavaDoc());
  }

  public Generator<JavaPojoMember, PojoSettings> validationAnnotationGenerator() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(validationAnnotationsForMember())
        .filter(validationFilter());
  }
}
