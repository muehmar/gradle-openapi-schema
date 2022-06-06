package com.github.muehmar.gradle.openapi.dsl;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettingsBuilder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;

public class SingleSchemaExtension implements Serializable {
  private static final String DEFAULT_SOURCE_SET = "main";

  private final String name;

  private String sourceSet;
  private String inputSpec;
  private String outputDir;
  private String suffix;
  private String packageName;
  private String jsonSupport;
  private Boolean enableSafeBuilder;
  private Boolean enableValidation;
  private EnumDescriptionExtension enumDescriptionExtension = null;
  private final List<ClassMapping> classMappings;
  private final List<FormatTypeMapping> formatTypeMappings;

  @Inject
  public SingleSchemaExtension(String name) {
    this.name = name;
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
  }

  public String getName() {
    return name;
  }

  public String getSourceSet() {
    return Optional.ofNullable(sourceSet).orElse(DEFAULT_SOURCE_SET);
  }

  public void setSourceSet(String sourceSet) {
    this.sourceSet = sourceSet;
  }

  public String getInputSpec() {
    return Optional.ofNullable(inputSpec)
        .orElseThrow(
            () ->
                new InvalidUserDataException(
                    "Could not generate schema, no input spec defined: Declare a correct path to a valid openapi spec."));
  }

  public void setInputSpec(String inputSpec) {
    this.inputSpec = inputSpec;
  }

  public String getOutputDir(Project project) {
    return Optional.ofNullable(outputDir)
        .orElseGet(() -> String.format("%s/generated/openapi", project.getBuildDir().toString()));
  }

  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  public String getSuffix() {
    return Optional.ofNullable(suffix).orElse("");
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public String getPackageName(Project project) {
    return Optional.ofNullable(packageName)
        .orElseGet(() -> String.format("%s.%s.api.model", project.getGroup(), project.getName()))
        .replace("-", "");
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  private JsonSupport getJsonSupport() {
    final Supplier<IllegalArgumentException> unsupportedValueException =
        () ->
            new IllegalArgumentException(
                "Unsupported value for jsonSupport: '"
                    + jsonSupport
                    + "'. Supported values are ["
                    + PList.of(JsonSupport.values()).map(JsonSupport::getValue).mkString(", "));
    return Optional.ofNullable(jsonSupport)
        .map(support -> JsonSupport.fromString(support).orElseThrow(unsupportedValueException))
        .orElse(JsonSupport.NONE);
  }

  public void setJsonSupport(String jsonSupport) {
    this.jsonSupport = jsonSupport;
  }

  public boolean getEnableSafeBuilder() {
    return Optional.ofNullable(enableSafeBuilder).orElse(true);
  }

  public void setEnableSafeBuilder(Boolean enableSafeBuilder) {
    this.enableSafeBuilder = enableSafeBuilder;
  }

  public boolean getEnableValidation() {
    return Optional.ofNullable(enableValidation).orElse(false);
  }

  public void setEnableValidation(Boolean enableValidation) {
    this.enableValidation = enableValidation;
  }

  public void classMapping(Action<ClassMapping> action) {
    final ClassMapping classMapping = new ClassMapping();
    action.execute(classMapping);
    classMappings.add(classMapping);
  }

  public PList<ClassMapping> getClassMappings() {
    return PList.fromIter(classMappings);
  }

  public SingleSchemaExtension withCommonClassMappings(PList<ClassMapping> other) {
    classMappings.addAll(other.toArrayList());
    return this;
  }

  public void formatTypeMapping(Action<FormatTypeMapping> action) {
    final FormatTypeMapping formatTypeMapping = new FormatTypeMapping();
    action.execute(formatTypeMapping);
    formatTypeMappings.add(formatTypeMapping);
  }

  public SingleSchemaExtension withCommonFormatTypeMappings(PList<FormatTypeMapping> other) {
    formatTypeMappings.addAll(other.toArrayList());
    return this;
  }

  public PList<FormatTypeMapping> getFormatTypeMappings() {
    return PList.fromIter(formatTypeMappings);
  }

  public void enumDescriptionExtraction(Action<EnumDescriptionExtension> action) {
    enumDescriptionExtension = new EnumDescriptionExtension();
    action.execute(enumDescriptionExtension);
  }

  public Optional<EnumDescriptionExtension> getEnumDescriptionExtension() {
    return Optional.ofNullable(enumDescriptionExtension);
  }

  public SingleSchemaExtension withCommonEnumDescription(
      Optional<EnumDescriptionExtension> enumDescriptionExtension) {
    if (this.enumDescriptionExtension == null) {
      this.enumDescriptionExtension = enumDescriptionExtension.orElse(null);
    }
    return this;
  }

  public PojoSettings toPojoSettings(Project project) {
    return PojoSettingsBuilder.create()
        .jsonSupport(getJsonSupport())
        .packageName(getPackageName(project))
        .suffix(getSuffix())
        .enableSafeBuilder(getEnableSafeBuilder())
        .enableConstraints(getEnableValidation())
        .classTypeMappings(getClassMappings().map(ClassMapping::toSettingsClassMapping))
        .formatTypeMappings(
            getFormatTypeMappings().map(FormatTypeMapping::toSettingsFormatTypeMapping))
        .enumDescriptionSettings(
            getEnumDescriptionExtension()
                .map(EnumDescriptionExtension::toEnumDescriptionSettings)
                .orElse(EnumDescriptionSettings.disabled()))
        .andAllOptionals()
        .build();
  }

  @Override
  public String toString() {
    return "SchemaGenExtension{"
        + "name='"
        + name
        + '\''
        + ", sourceSet='"
        + sourceSet
        + '\''
        + ", inputSpec='"
        + inputSpec
        + '\''
        + ", outputDir='"
        + outputDir
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + ", packageName='"
        + packageName
        + '\''
        + ", jsonSupport='"
        + jsonSupport
        + '\''
        + ", enableSafeBuilder="
        + enableSafeBuilder
        + ", enableValidation="
        + enableValidation
        + ", enumDescriptionExtension="
        + enumDescriptionExtension
        + ", classMappingsList="
        + classMappings
        + ", formatTypeMappings="
        + formatTypeMappings
        + '}';
  }
}
