package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import io.github.muehmar.pojobuilder.annotations.Nullable;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@PojoBuilder
public class DtoMapping implements Serializable {
  String dtoName;
  String customType;
  @Nullable TypeConversion typeConversion;
  boolean disableMissingConversionWarning;

  public DtoMapping(String dtoName, String customType, Optional<TypeConversion> typeConversion) {
    this.dtoName = dtoName;
    this.customType = customType;
    this.typeConversion = typeConversion.orElse(null);
    this.disableMissingConversionWarning = false;
  }

  public Optional<TypeConversion> getTypeConversion() {
    return Optional.ofNullable(typeConversion);
  }

  public boolean isMissingConversionWarningEnabled() {
    return not(disableMissingConversionWarning);
  }
}
