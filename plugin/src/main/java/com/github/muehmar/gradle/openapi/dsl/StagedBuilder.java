package com.github.muehmar.gradle.openapi.dsl;

import static com.github.muehmar.gradle.openapi.dsl.StagedBuilderBuilder.fullStagedBuilderBuilder;

import io.github.muehmar.pojobuilder.annotations.Nullable;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.Optional;
import lombok.Data;

@Data
@PojoBuilder
public class StagedBuilder implements Serializable {
  @Nullable private Boolean enabled;

  public StagedBuilder(Boolean enabled) {
    this.enabled = enabled;
  }

  public static StagedBuilder allUndefined() {
    return fullStagedBuilderBuilder().enabled(Optional.empty()).build();
  }

  public static StagedBuilder defaultStagedBuilder() {
    return fullStagedBuilderBuilder().enabled(true).build();
  }

  public Optional<Boolean> getEnabled() {
    return Optional.ofNullable(enabled);
  }

  public boolean getEnabledOrDefault() {
    return getEnabled().orElseGet(() -> defaultStagedBuilder().getEnabledOrDefault());
  }
}
