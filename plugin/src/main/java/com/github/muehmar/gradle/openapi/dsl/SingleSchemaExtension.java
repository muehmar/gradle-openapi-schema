package com.github.muehmar.gradle.openapi.dsl;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.Project;

@EqualsAndHashCode
@ToString
public class SingleSchemaExtension implements Serializable {
  private static final String DEFAULT_SOURCE_SET = "main";
  private static final boolean DEFAULT_RESOLVE_INPUT_SPECS = true;

  private final String name;

  private String sourceSet;
  private String inputSpec;
  private String outputDir;
  private Boolean resolveInputSpecs;
  private String suffix;
  private GetterSuffixes getterSuffixes;
  private ValidationMethods validationMethods;
  private String packageName;
  private String jsonSupport;
  private Boolean enableSafeBuilder;
  private String builderMethodPrefix;
  private Boolean enableValidation;
  private String validationApi;
  private EnumDescriptionExtension enumDescriptionExtension = null;
  private final List<ClassMapping> classMappings;
  private final List<FormatTypeMapping> formatTypeMappings;
  private final List<ConstantSchemaNameMapping> constantSchemaNameMappings;
  private List<String> excludeSchemas;

  @Inject
  public SingleSchemaExtension(String name) {
    this.name = name;
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
    this.getterSuffixes = GetterSuffixes.allUndefined();
    this.validationMethods = ValidationMethods.allUndefined();
    this.constantSchemaNameMappings = new ArrayList<>();
    this.excludeSchemas = new ArrayList<>();
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

  public boolean getResolveInputSpecs() {
    return Optional.ofNullable(resolveInputSpecs).orElse(DEFAULT_RESOLVE_INPUT_SPECS);
  }

  public void setResolveInputSpecs(Boolean resolveInputSpecs) {
    this.resolveInputSpecs = resolveInputSpecs;
  }

  public String getSuffix() {
    return Optional.ofNullable(suffix).orElse("");
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public void getterSuffixes(Action<GetterSuffixes> action) {
    action.execute(getterSuffixes);
  }

  public GetterSuffixes getGetterSuffixes() {
    return getterSuffixes;
  }

  public void validationMethods(Action<ValidationMethods> action) {
    action.execute(validationMethods);
  }

  public ValidationMethods getValidationMethods() {
    return validationMethods;
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

  public void setValidationApi(String validationApi) {
    this.validationApi = validationApi;
  }

  public ValidationApi getValidationApi() {
    final Supplier<IllegalArgumentException> unsupportedValueException =
        () ->
            new IllegalArgumentException(
                "Unsupported value for validationApi: '"
                    + validationApi
                    + "'. Supported values are ["
                    + PList.of(ValidationApi.values()).map(ValidationApi::getValue).mkString(", "));
    return Optional.ofNullable(validationApi)
        .map(support -> ValidationApi.fromString(support).orElseThrow(unsupportedValueException))
        .orElse(ValidationApi.JAKARTA_2_0);
  }

  public String getBuilderMethodPrefix() {
    return Optional.ofNullable(builderMethodPrefix).orElse("");
  }

  public void setBuilderMethodPrefix(String builderMethodPrefix) {
    this.builderMethodPrefix = builderMethodPrefix;
  }

  public void setEnableValidation(Boolean enableValidation) {
    this.enableValidation = enableValidation;
  }

  public void setExcludeSchemas(List<String> excludeSchemas) {
    this.excludeSchemas = excludeSchemas;
  }

  public List<String> getExcludeSchemas() {
    return excludeSchemas;
  }

  public void classMapping(Action<ClassMapping> action) {
    final ClassMapping classMapping = new ClassMapping();
    action.execute(classMapping);
    classMappings.add(classMapping);
  }

  public PList<ClassMapping> getClassMappings() {
    return PList.fromIter(classMappings);
  }

  public SingleSchemaExtension withCommonClassMappings(List<ClassMapping> other) {
    classMappings.addAll(other);
    return this;
  }

  public void formatTypeMapping(Action<FormatTypeMapping> action) {
    final FormatTypeMapping formatTypeMapping = new FormatTypeMapping();
    action.execute(formatTypeMapping);
    formatTypeMappings.add(formatTypeMapping);
  }

  public SingleSchemaExtension withCommonFormatTypeMappings(List<FormatTypeMapping> other) {
    formatTypeMappings.addAll(other);
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

  public SingleSchemaExtension withCommonGetterSuffixes(GetterSuffixes commonSuffixes) {
    this.getterSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);
    return this;
  }

  public SingleSchemaExtension withCommonValidationMethods(
      ValidationMethods commonValidationMethods) {
    this.validationMethods = this.validationMethods.withCommonRawGetter(commonValidationMethods);
    return this;
  }

  public void constantSchemaNameMapping(Action<ConstantSchemaNameMapping> action) {
    final ConstantSchemaNameMapping constantSchemaNameMapping = new ConstantSchemaNameMapping();
    action.execute(constantSchemaNameMapping);
    constantSchemaNameMappings.add(constantSchemaNameMapping);
  }

  public SingleSchemaExtension withCommonConstantSchemaNameMappings(
      List<ConstantSchemaNameMapping> other) {
    constantSchemaNameMappings.addAll(other);
    return this;
  }

  public PojoNameMappings getPojoNameMappings() {
    return new PojoNameMappings(
        PList.fromIter(constantSchemaNameMappings)
            .map(ConstantSchemaNameMapping::toConstantNameMapping)
            .toArrayList());
  }

  public PojoSettings toPojoSettings(Project project) {
    final com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes
        settingsGetterSuffixes =
            GetterSuffixesBuilder.create()
                .requiredSuffix(getterSuffixes.getRequiredSuffixOrDefault())
                .requiredNullableSuffix(getterSuffixes.getRequiredNullableSuffixOrDefault())
                .optionalSuffix(getterSuffixes.getOptionalSuffixOrDefault())
                .optionalNullableSuffix(getterSuffixes.getOptionalNullableSuffixOrDefault())
                .andAllOptionals()
                .build();

    final com.github.muehmar.gradle.openapi.generator.settings.ValidationMethods
        settingsValidationMethods =
            com.github.muehmar.gradle.openapi.generator.settings.ValidationMethodsBuilder.create()
                .modifier(validationMethods.getModifierOrDefault())
                .getterSuffix(validationMethods.getGetterSuffixOrDefault())
                .deprecatedAnnotation(validationMethods.getDeprecatedAnnotationOrDefault())
                .andAllOptionals()
                .build();

    return PojoSettingsBuilder.create()
        .jsonSupport(getJsonSupport())
        .packageName(getPackageName(project))
        .suffix(getSuffix())
        .enableSafeBuilder(getEnableSafeBuilder())
        .builderMethodPrefix(getBuilderMethodPrefix())
        .enableValidation(getEnableValidation())
        .validationApi(getValidationApi())
        .classTypeMappings(getClassMappings().map(ClassMapping::toSettingsClassMapping))
        .formatTypeMappings(
            getFormatTypeMappings().map(FormatTypeMapping::toSettingsFormatTypeMapping))
        .enumDescriptionSettings(
            getEnumDescriptionExtension()
                .map(EnumDescriptionExtension::toEnumDescriptionSettings)
                .orElse(EnumDescriptionSettings.disabled()))
        .getterSuffixes(settingsGetterSuffixes)
        .validationMethods(settingsValidationMethods)
        .excludeSchemas(getExcludeSchemas())
        .pojoNameMappings(getPojoNameMappings())
        .andAllOptionals()
        .build();
  }
}
