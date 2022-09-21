package com.github.muehmar.gradle.openapi.dsl;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettingsBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.RawGetterBuilder;
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
  private RawGetter rawGetter;
  private String packageName;
  private String jsonSupport;
  private Boolean enableSafeBuilder;
  private String builderMethodPrefix;
  private Boolean enableValidation;
  private EnumDescriptionExtension enumDescriptionExtension = null;
  private final List<ClassMapping> classMappings;
  private final List<FormatTypeMapping> formatTypeMappings;

  @Inject
  public SingleSchemaExtension(String name) {
    this.name = name;
    this.classMappings = new ArrayList<>();
    this.formatTypeMappings = new ArrayList<>();
    this.getterSuffixes = GetterSuffixes.allUndefined();
    this.rawGetter = RawGetter.allUndefined();
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

  public void rawGetter(Action<RawGetter> action) {
    action.execute(rawGetter);
    System.out.println("Specific validation getter configured...");
  }

  public RawGetter getRawGetter() {
    return rawGetter;
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

  public String getBuilderMethodPrefix() {
    return Optional.ofNullable(builderMethodPrefix).orElse("");
  }

  public void setBuilderMethodPrefix(String builderMethodPrefix) {
    this.builderMethodPrefix = builderMethodPrefix;
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

  public SingleSchemaExtension withCommonGetterSuffixes(GetterSuffixes commonSuffixes) {
    this.getterSuffixes = getterSuffixes.withCommonSuffixes(commonSuffixes);
    return this;
  }

  public SingleSchemaExtension withCommonRawGetter(RawGetter commonRawGetter) {
    this.rawGetter = this.rawGetter.withCommonRawGetter(commonRawGetter);
    return this;
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

    final com.github.muehmar.gradle.openapi.generator.settings.RawGetter settingsRawGetter =
        RawGetterBuilder.create()
            .modifier(rawGetter.getModifierOrDefault())
            .suffix(rawGetter.getSuffixOrDefault())
            .deprecatedAnnotation(rawGetter.getDeprecatedAnnotationOrDefault())
            .andAllOptionals()
            .build();

    return PojoSettingsBuilder.create()
        .jsonSupport(getJsonSupport())
        .packageName(getPackageName(project))
        .suffix(getSuffix())
        .enableSafeBuilder(getEnableSafeBuilder())
        .builderMethodPrefix(getBuilderMethodPrefix())
        .enableConstraints(getEnableValidation())
        .classTypeMappings(getClassMappings().map(ClassMapping::toSettingsClassMapping))
        .formatTypeMappings(
            getFormatTypeMappings().map(FormatTypeMapping::toSettingsFormatTypeMapping))
        .enumDescriptionSettings(
            getEnumDescriptionExtension()
                .map(EnumDescriptionExtension::toEnumDescriptionSettings)
                .orElse(EnumDescriptionSettings.disabled()))
        .getterSuffixes(settingsGetterSuffixes)
        .rawGetter(settingsRawGetter)
        .andAllOptionals()
        .build();
  }
}
