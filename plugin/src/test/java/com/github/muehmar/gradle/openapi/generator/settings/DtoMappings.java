package com.github.muehmar.gradle.openapi.generator.settings;

import java.util.Optional;

public class DtoMappings {
  private DtoMappings() {}

  public static final DtoMapping DTO_MAPPING_WITHOUT_CONVERSION =
      new DtoMapping("UserDto", "org.custom.CustomUserDto", Optional.empty());

  public static final TypeConversion DTO_CONVERSION =
      new TypeConversion("toDto", "org.custom.CustomUserDto#fromDto");
  public static final DtoMapping DTO_MAPPING_WITH_CONVERSION =
      new DtoMapping("UserDto", "org.custom.CustomUserDto", Optional.of(DTO_CONVERSION));
}
