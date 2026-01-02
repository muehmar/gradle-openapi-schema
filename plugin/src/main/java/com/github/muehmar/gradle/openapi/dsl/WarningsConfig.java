package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.dsl.WarningsConfigBuilder.fullWarningsConfigBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.util.Optionals;
import com.github.muehmar.gradle.openapi.warnings.FailingWarningTypes;
import com.github.muehmar.gradle.openapi.warnings.WarningType;
import io.github.muehmar.pojobuilder.annotations.Nullable;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@PojoBuilder
@Setter
@EqualsAndHashCode
@ToString
public class WarningsConfig implements Serializable {
  @Nullable private Boolean disableWarnings;
  @Nullable private Boolean failOnWarnings;
  @Nullable private Boolean failOnUnsupportedValidation;
  @Nullable private Boolean failOnMissingMappingConversion;
  @Nullable private Boolean failOnUnusedMapping;

  public WarningsConfig(
      Boolean disableWarnings,
      Boolean failOnWarnings,
      Boolean failOnUnsupportedValidation,
      Boolean failOnMissingMappingConversion,
      Boolean failOnUnusedMapping) {
    this.disableWarnings = disableWarnings;
    this.failOnWarnings = failOnWarnings;
    this.failOnUnsupportedValidation = failOnUnsupportedValidation;
    this.failOnMissingMappingConversion = failOnMissingMappingConversion;
    this.failOnUnusedMapping = failOnUnusedMapping;
  }

  public static WarningsConfig allUndefined() {
    return WarningsConfigBuilder.createFull()
        .disableWarnings(Optional.empty())
        .failOnWarnings(Optional.empty())
        .failOnUnsupportedValidation(Optional.empty())
        .failOnMissingMappingConversion(Optional.empty())
        .failOnUnusedMapping(Optional.empty())
        .build();
  }

  public WarningsConfig withCommonWarnings(WarningsConfig commonWarnings) {
    return fullWarningsConfigBuilder()
        .disableWarnings(
            Optionals.or(
                Optional.ofNullable(disableWarnings),
                Optional.ofNullable(commonWarnings.disableWarnings)))
        .failOnWarnings(
            Optionals.or(
                Optional.ofNullable(failOnWarnings),
                Optional.ofNullable(commonWarnings.failOnWarnings)))
        .failOnUnsupportedValidation(
            Optionals.or(
                Optional.ofNullable(failOnUnsupportedValidation),
                Optional.ofNullable(commonWarnings.failOnUnsupportedValidation)))
        .failOnMissingMappingConversion(
            Optionals.or(
                Optional.ofNullable(failOnMissingMappingConversion),
                Optional.ofNullable(commonWarnings.failOnMissingMappingConversion)))
        .failOnUnusedMapping(
            Optionals.or(
                Optional.ofNullable(failOnUnusedMapping),
                Optional.ofNullable(commonWarnings.failOnUnusedMapping)))
        .build();
  }

  public boolean getDisableWarnings() {
    return Optional.ofNullable(disableWarnings).orElse(false);
  }

  public boolean getFailOnWarnings() {
    return Optional.ofNullable(failOnWarnings).orElse(false);
  }

  public boolean getFailOnUnsupportedValidation() {
    return Optional.ofNullable(failOnUnsupportedValidation).orElse(getFailOnWarnings());
  }

  public boolean getFailOnMissingMappingConversion() {
    return Optional.ofNullable(failOnMissingMappingConversion).orElse(getFailOnWarnings());
  }

  public boolean getFailOnUnusedMapping() {
    return Optional.ofNullable(failOnUnusedMapping).orElse(getFailOnWarnings());
  }

  public FailingWarningTypes getFailingWarningTypes() {
    final List<WarningType> types = new ArrayList<>();
    if (getFailOnUnsupportedValidation()) {
      types.add(WarningType.UNSUPPORTED_VALIDATION);
    }
    if (getFailOnMissingMappingConversion()) {
      types.add(WarningType.MISSING_MAPPING_CONVERSION);
    }
    if (getFailOnUnusedMapping()) {
      types.add(WarningType.UNUSED_MAPPING);
    }
    return new FailingWarningTypes(PList.fromIter(types));
  }
}
