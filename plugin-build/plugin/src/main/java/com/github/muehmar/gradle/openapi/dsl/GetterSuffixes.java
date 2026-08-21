package com.github.muehmar.gradle.openapi.dsl;

import io.github.muehmar.pojobuilder.annotations.Nullable;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.Optional;
import lombok.Data;
import lombok.With;

@Data
@With
@PojoBuilder
public class GetterSuffixes implements Serializable {
  private static final String DEFAULT_REQUIRED = "";
  private static final String DEFAULT_OPTIONAL = "Opt";
  private static final String DEFAULT_REQUIRED_NULLABLE = "Opt";
  private static final String DEFAULT_OPTIONAL_NULLABLE = "Tristate";

  @Nullable private String requiredSuffix;
  @Nullable private String requiredNullableSuffix;
  @Nullable private String optionalSuffix;
  @Nullable private String optionalNullableSuffix;

  public GetterSuffixes() {
    this(null, null, null, null);
  }

  public GetterSuffixes(
      String requiredSuffix,
      String requiredNullableSuffix,
      String optionalSuffix,
      String optionalNullableSuffix) {
    this.requiredSuffix = requiredSuffix;
    this.requiredNullableSuffix = requiredNullableSuffix;
    this.optionalSuffix = optionalSuffix;
    this.optionalNullableSuffix = optionalNullableSuffix;
  }

  public static GetterSuffixes allUndefined() {
    return new GetterSuffixes(null, null, null, null);
  }

  public GetterSuffixes withCommonSuffixes(GetterSuffixes commonSuffixes) {
    return new GetterSuffixes(
        getRequiredSuffix().orElse(commonSuffixes.getRequiredSuffix().orElse(null)),
        getRequiredNullableSuffix().orElse(commonSuffixes.getRequiredNullableSuffix().orElse(null)),
        getOptionalSuffix().orElse(commonSuffixes.getOptionalSuffix().orElse(null)),
        getOptionalNullableSuffix()
            .orElse(commonSuffixes.getOptionalNullableSuffix().orElse(null)));
  }

  public Optional<String> getRequiredSuffix() {
    return Optional.ofNullable(requiredSuffix);
  }

  public String getRequiredSuffixOrDefault() {
    return getRequiredSuffix().orElse(DEFAULT_REQUIRED);
  }

  public Optional<String> getRequiredNullableSuffix() {
    return Optional.ofNullable(requiredNullableSuffix);
  }

  public String getRequiredNullableSuffixOrDefault() {
    return getRequiredNullableSuffix().orElse(DEFAULT_REQUIRED_NULLABLE);
  }

  public Optional<String> getOptionalSuffix() {
    return Optional.ofNullable(optionalSuffix);
  }

  public String getOptionalSuffixOrDefault() {
    return getOptionalSuffix().orElse(DEFAULT_OPTIONAL);
  }

  public Optional<String> getOptionalNullableSuffix() {
    return Optional.ofNullable(optionalNullableSuffix);
  }

  public String getOptionalNullableSuffixOrDefault() {
    return getOptionalNullableSuffix().orElse(DEFAULT_OPTIONAL_NULLABLE);
  }
}
