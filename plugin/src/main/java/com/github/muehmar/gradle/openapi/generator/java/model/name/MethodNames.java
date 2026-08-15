package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;

public class MethodNames {
  private MethodNames() {}

  /**
   * Names of the generated enum methods which are referenced both when generating the enum itself
   * ({@code EnumGenerator}) and when converting between the internal {@code String} representation
   * of an enum and the enum api type ({@code PluginApiType#useEnumAsApiType}). Both sides must use
   * these constants.
   */
  public static class Enum {
    private Enum() {}

    /** Converts a {@code String} value to the enum constant, throwing for unknown values. */
    public static Name fromValue() {
      return Name.ofString("fromValue");
    }

    /** Returns the string value of the enum constant. */
    public static Name getValue() {
      return Name.ofString("getValue");
    }
  }

  public static class RequiredAdditionalProperty {
    private RequiredAdditionalProperty() {}

    /**
     * Name of the generated private getter reading the internal value of the required additional
     * property from the properties map. The public getter converts this value to the api type,
     * while all validation runs directly against it.
     */
    public static JavaName internalValueGetterName(
        JavaRequiredAdditionalProperty additionalProperty) {
      return additionalProperty.getName().startUpperCase().prefix("get").append("Internal");
    }
  }

  public static class Composition {

    private Composition() {}

    public static JavaName isValidAgainstNoSchemaMethodName(
        DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("isValidAgainstNo%sSchema", type.getName()));
    }

    public static JavaName getValidCountMethodName(DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("get%sValidCount", type.getName()));
    }

    public static JavaName getInvalidCompositionMethodName(
        DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("getInvalid%s", type.getName()));
    }

    public static JavaName isValidAgainstTheCorrectSchemaMethodName(
        DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("isValidAgainstTheCorrect%sSchema", type.getName()));
    }

    public static JavaName foldCompositionMethodName(DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("fold%s", type.getName()));
    }

    public static JavaName getCompositionValidCountMethodName(
        DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("get%sValidCount", type.getName()));
    }

    public static JavaName getCompositionMethodName(DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("get%s", type.getName()));
    }

    public static class OneOf {
      private OneOf() {}

      public static JavaName isValidAgainstMoreThanOneSchemaMethodName() {
        return JavaName.fromString("isValidAgainstMoreThanOneSchema");
      }
    }

    public static class AnyOf {
      private AnyOf() {}
    }

    public static Name isValidAgainstMethodName(JavaPojo pojo) {
      return Name.ofString(String.format("isValidAgainst%s", pojo.getClassName()));
    }

    public static Name asConversionMethodName(JavaPojo pojo) {
      return Name.ofString(String.format("as%s", pojo.getClassName()));
    }

    public static Name dtoMappingArgumentName(JavaPojo pojo) {
      return Name.ofString(String.format("on%s", pojo.getClassName()));
    }
  }

  public static JavaName getPropertyCountMethodName() {
    return JavaName.fromString("getPropertyCount");
  }

  public static JavaName getIsMultipleOfValidMethodName(JavaName memberName) {
    return memberName.startUpperCase().prefix("is").append("MultipleOfValid");
  }
}
