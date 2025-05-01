package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.generator.settings.PojoSettingsBuilder.fullPojoSettingsBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import com.github.muehmar.gradle.openapi.util.Optionals;
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
import org.gradle.api.file.Directory;
import org.gradle.api.provider.Provider;

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
  private String xmlSupport;
  private StagedBuilder stagedBuilder;
  private String builderMethodPrefix;
  private Boolean enableValidation;
  private Boolean nonStrictOneOfValidation;
  private String validationApi;
  private EnumDescriptionExtension enumDescriptionExtension = null;
  private final List<ClassMapping> classMappings;
  private final List<FormatTypeMapping> formatTypeMappings;
  private final List<DtoMapping> dtoMappings;
  private final List<ConstantSchemaNameMapping> constantSchemaNameMappings;
  private List<String> excludeSchemas;
  private WarningsConfig warnings;

  @Inject
  public SingleSchemaExtension(String name) {
    this.name = name;
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
    this.dtoMappings = new ArrayList<>();
    this.getterSuffixes = GetterSuffixes.allUndefined();
    this.stagedBuilder = StagedBuilder.allUndefined();
    this.validationMethods = ValidationMethods.allUndefined();
    this.constantSchemaNameMappings = new ArrayList<>();
    this.excludeSchemas = new ArrayList<>();
    this.warnings = WarningsConfig.allUndefined();
  }

  public String getName() {
    return name;
  }

  public String getSourceSet() {
    return Optional.ofNullable(sourceSet).orElse(DEFAULT_SOURCE_SET);
  }

  // DSL API
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

  // DSL API
  public void setInputSpec(String inputSpec) {
    this.inputSpec = inputSpec;
  }

  public String getOutputDir(Project project) {
    return Optional.ofNullable(outputDir)
        .orElseGet(
            () ->
                project.getLayout().getBuildDirectory().dir("generated/openapi").get().toString());
  }

  // DSL API
  public void setOutputDir(String outputDir) {
    this.outputDir = outputDir;
  }

  // DSL API
  public void setOutputDir(Provider<Directory> outputDir) {
    this.outputDir =
        Optional.ofNullable(outputDir).map(Provider::get).map(Directory::toString).orElse(null);
  }

  public boolean getResolveInputSpecs() {
    return Optional.ofNullable(resolveInputSpecs).orElse(DEFAULT_RESOLVE_INPUT_SPECS);
  }

  // DSL API
  public void setResolveInputSpecs(Boolean resolveInputSpecs) {
    this.resolveInputSpecs = resolveInputSpecs;
  }

  public String getSuffix() {
    return Optional.ofNullable(suffix).orElse("");
  }

  // DSL API
  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  // DSL API
  public void getterSuffixes(Action<GetterSuffixes> action) {
    action.execute(getterSuffixes);
  }

  public GetterSuffixes getGetterSuffixes() {
    return getterSuffixes;
  }

  // DSL API
  public void validationMethods(Action<ValidationMethods> action) {
    action.execute(validationMethods);
  }

  public ValidationMethods getValidationMethods() {
    return validationMethods;
  }

  public PackageName getPackageName(Project project) {
    return PackageName.fromString(
        Optional.ofNullable(packageName)
            .orElseGet(
                () -> String.format("%s.%s.api.model", project.getGroup(), project.getName()))
            .replace("-", ""));
  }

  // DSL API
  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  JsonSupport getJsonSupport() {
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

  // DSL API
  public void setJsonSupport(String jsonSupport) {
    this.jsonSupport = jsonSupport;
  }

  XmlSupport getXmlSupport() {
    final Supplier<IllegalArgumentException> unsupportedValueException =
        () ->
            new IllegalArgumentException(
                "Unsupported value for xmlSupport: '"
                    + xmlSupport
                    + "'. Supported values are ["
                    + PList.of(XmlSupport.values()).map(XmlSupport::getValue).mkString(", "));
    return Optional.ofNullable(xmlSupport)
        .map(support -> XmlSupport.fromString(support).orElseThrow(unsupportedValueException))
        .orElse(XmlSupport.NONE);
  }

  // DSL API
  public void setXmlSupport(String xmlSupport) {
    this.xmlSupport = xmlSupport;
  }

  public StagedBuilder getStagedBuilder() {
    return stagedBuilder;
  }

  // DSL API
  public void stagedBuilder(Action<StagedBuilder> action) {
    action.execute(stagedBuilder);
  }

  public boolean getEnableValidation() {
    return Optional.ofNullable(enableValidation).orElse(false);
  }

  public boolean getNonStrictOneOfValidation() {
    return Optional.ofNullable(nonStrictOneOfValidation).orElse(false);
  }

  // DSL API
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

  // DSL API
  public void setBuilderMethodPrefix(String builderMethodPrefix) {
    this.builderMethodPrefix = builderMethodPrefix;
  }

  // DSL API
  public void setEnableValidation(Boolean enableValidation) {
    this.enableValidation = enableValidation;
  }

  // DSL API
  public void setNonStrictOneOfValidation(Boolean nonStrictOneOfValidation) {
    this.nonStrictOneOfValidation = nonStrictOneOfValidation;
  }

  // DSL API
  public void setExcludeSchemas(List<String> excludeSchemas) {
    this.excludeSchemas = excludeSchemas;
  }

  public List<String> getExcludeSchemas() {
    return excludeSchemas;
  }

  // DSL API
  public void warnings(Action<WarningsConfig> action) {
    action.execute(warnings);
  }

  public WarningsConfig getWarnings() {
    return warnings;
  }

  // DSL API
  public void classMapping(Action<ClassMapping> action) {
    final ClassMapping classMapping = new ClassMapping();
    action.execute(classMapping);
    classMapping.assertCompleteTypeConversion();
    classMappings.add(classMapping);
  }

  public PList<ClassMapping> getClassMappings() {
    return PList.fromIter(classMappings);
  }

  SingleSchemaExtension withCommonClassMappings(List<ClassMapping> other) {
    classMappings.addAll(other);
    return this;
  }

  // DSL API
  public void formatTypeMapping(Action<FormatTypeMapping> action) {
    final FormatTypeMapping formatTypeMapping = new FormatTypeMapping();
    action.execute(formatTypeMapping);
    formatTypeMapping.assertCompleteTypeConversion();
    formatTypeMappings.add(formatTypeMapping);
  }

  SingleSchemaExtension withCommonFormatTypeMappings(List<FormatTypeMapping> other) {
    formatTypeMappings.addAll(other);
    return this;
  }

  PList<FormatTypeMapping> getFormatTypeMappings() {
    return PList.fromIter(formatTypeMappings);
  }

  // DSL API
  public void dtoMapping(Action<DtoMapping> action) {
    final DtoMapping dtoMapping = new DtoMapping();
    action.execute(dtoMapping);
    dtoMapping.assertCompleteTypeConversion();
    dtoMappings.add(dtoMapping);
  }

  SingleSchemaExtension withCommonDtoMappings(List<DtoMapping> other) {
    dtoMappings.addAll(other);
    return this;
  }

  PList<DtoMapping> getDtoMappings() {
    return PList.fromIter(dtoMappings);
  }

  public void enumDescriptionExtraction(Action<EnumDescriptionExtension> action) {
    enumDescriptionExtension = new EnumDescriptionExtension();
    action.execute(enumDescriptionExtension);
  }

  Optional<EnumDescriptionExtension> getEnumDescriptionExtension() {
    return Optional.ofNullable(enumDescriptionExtension);
  }

  SingleSchemaExtension withCommonEnumDescription(
      Optional<EnumDescriptionExtension> enumDescriptionExtension) {
    if (this.enumDescriptionExtension == null) {
      this.enumDescriptionExtension = enumDescriptionExtension.orElse(null);
    }
    return this;
  }

  SingleSchemaExtension withCommonGetterSuffixes(GetterSuffixes commonSuffixes) {
    this.getterSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);
    return this;
  }

  SingleSchemaExtension withCommonValidationMethods(ValidationMethods commonValidationMethods) {
    this.validationMethods = this.validationMethods.withCommonRawGetter(commonValidationMethods);
    return this;
  }

  // DSL API
  public void constantSchemaNameMapping(Action<ConstantSchemaNameMapping> action) {
    final ConstantSchemaNameMapping constantSchemaNameMapping = new ConstantSchemaNameMapping();
    action.execute(constantSchemaNameMapping);
    constantSchemaNameMappings.add(constantSchemaNameMapping);
  }

  SingleSchemaExtension withCommonConstantSchemaNameMappings(
      List<ConstantSchemaNameMapping> other) {
    constantSchemaNameMappings.addAll(other);
    return this;
  }

  SingleSchemaExtension withCommonWarnings(WarningsConfig commonWarnings) {
    this.warnings = this.warnings.withCommonWarnings(commonWarnings);
    return this;
  }

  PojoNameMappings getPojoNameMappings() {
    return new PojoNameMappings(
        PList.fromIter(constantSchemaNameMappings)
            .map(ConstantSchemaNameMapping::toConstantNameMapping)
            .toArrayList());
  }

  public PojoSettings toPojoSettings(Project project, String taskName) {
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

    final StagedBuilderSettings stagedBuilderSettings =
        StagedBuilderSettingsBuilder.fullStagedBuilderSettingsBuilder()
            .enabled(stagedBuilder.getEnabledOrDefault())
            .build();

    return fullPojoSettingsBuilder()
        .jsonSupport(getJsonSupport())
        .xmlSupport(getXmlSupport())
        .packageName(getPackageName(project))
        .suffix(getSuffix())
        .stagedBuilder(stagedBuilderSettings)
        .builderMethodPrefix(getBuilderMethodPrefix())
        .enableValidation(getEnableValidation())
        .nonStrictOneOfValidation(getNonStrictOneOfValidation())
        .validationApi(getValidationApi())
        .classTypeMappings(getClassMappings().map(ClassMapping::toSettingsClassMapping))
        .formatTypeMappings(
            getFormatTypeMappings().map(FormatTypeMapping::toSettingsFormatTypeMapping))
        .dtoMappings(getDtoMappings().map(DtoMapping::toSettingsDtoMapping))
        .enumDescriptionSettings(
            getEnumDescriptionExtension()
                .map(EnumDescriptionExtension::toEnumDescriptionSettings)
                .orElse(EnumDescriptionSettings.disabled()))
        .getterSuffixes(settingsGetterSuffixes)
        .validationMethods(settingsValidationMethods)
        .excludeSchemas(getExcludeSchemas())
        .pojoNameMappings(getPojoNameMappings())
        .taskIdentifier(
            TaskIdentifier.fromString(String.format("%s-%s", project.getName(), taskName)))
        .build();
  }

  SingleSchemaExtension withCommonSourceSet(Optional<String> commonSourceSet) {
    if (sourceSet == null) {
      commonSourceSet.ifPresent(this::setSourceSet);
    }
    return this;
  }

  SingleSchemaExtension withCommonOutputDir(Optional<String> commonOutputDir) {
    if (outputDir == null) {
      commonOutputDir.ifPresent(this::setOutputDir);
    }
    return this;
  }

  SingleSchemaExtension withCommonSuffix(Optional<String> commonSuffix) {
    if (suffix == null) {
      commonSuffix.ifPresent(this::setSuffix);
    }
    return this;
  }

  SingleSchemaExtension withCommonJsonSupport(Optional<String> commonJsonSupport) {
    if (jsonSupport == null) {
      commonJsonSupport.ifPresent(this::setJsonSupport);
    }
    return this;
  }

  SingleSchemaExtension withCommonXmlSupport(Optional<String> commonXmlSupport) {
    if (xmlSupport == null) {
      commonXmlSupport.ifPresent(this::setXmlSupport);
    }
    return this;
  }

  SingleSchemaExtension withCommonStagedBuilder(StagedBuilder commonStagedBuilder) {
    stagedBuilder =
        StagedBuilderBuilder.fullStagedBuilderBuilder()
            .enabled(Optionals.or(stagedBuilder.getEnabled(), commonStagedBuilder.getEnabled()))
            .build();
    return this;
  }

  SingleSchemaExtension withCommonEnableValidation(Optional<Boolean> commonEnableValidation) {
    if (enableValidation == null) {
      commonEnableValidation.ifPresent(this::setEnableValidation);
    }
    return this;
  }

  SingleSchemaExtension withCommonNonStrictOneOfValidation(
      Optional<Boolean> commonNonStrictOneOfValidation) {
    if (nonStrictOneOfValidation == null) {
      commonNonStrictOneOfValidation.ifPresent(this::setNonStrictOneOfValidation);
    }
    return this;
  }

  SingleSchemaExtension withCommonValidationApi(Optional<String> commonValidationApi) {
    if (validationApi == null) {
      commonValidationApi.ifPresent(this::setValidationApi);
    }
    return this;
  }

  SingleSchemaExtension withCommonBuilderMethodPrefix(Optional<String> commonBuilderMethodPrefix) {
    if (builderMethodPrefix == null) {
      commonBuilderMethodPrefix.ifPresent(this::setBuilderMethodPrefix);
    }
    return this;
  }
}
