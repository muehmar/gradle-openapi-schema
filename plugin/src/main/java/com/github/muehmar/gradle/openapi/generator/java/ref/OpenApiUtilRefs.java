package com.github.muehmar.gradle.openapi.generator.java.ref;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties.AdditionalPropertyClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties.NullableAdditionalPropertyClassGenerator;

public class OpenApiUtilRefs {
  private OpenApiUtilRefs() {}

  public static final String OPENAPI_UTIL_PACKAGE = "com.github.muehmar.openapi.util";

  public static final String TRISTATE = OPENAPI_UTIL_PACKAGE + ".Tristate";
  public static final String JACKSON_NULL_CONTAINER =
      OPENAPI_UTIL_PACKAGE + ".JacksonNullContainer";
  public static final String EMAIL_VALIDATOR = OPENAPI_UTIL_PACKAGE + ".EmailValidator";
  public static final String ADDITIONAL_PROPERTY =
      OPENAPI_UTIL_PACKAGE + "." + AdditionalPropertyClassGenerator.CLASSNAME;
  public static final String NULLABLE_ADDITIONAL_PROPERTY =
      OPENAPI_UTIL_PACKAGE + "." + NullableAdditionalPropertyClassGenerator.CLASSNAME;
}
