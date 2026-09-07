package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
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

  /**
   * Names of the accessors a composed dto reads on its member dtos. Both the declaration in the
   * member dto and the call site in the parent must derive the very same name, but they do so from
   * different {@link com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember}
   * instances which know nothing of each other's siblings. The names are therefore context-free:
   * they carry a fixed, improbable suffix instead of being resolved against a name scope, so that a
   * property of the member dto practically never collides with them.
   */
  public static class CrossDto {
    private CrossDto() {}

    /**
     * Frozen suffix of the accessors read across classes. Changing it changes the contract between
     * a composed dto and its member dtos, hence it must stay stable.
     */
    public static final String SUFFIX = "Internal2741988768";

    /**
     * Reads the value of a property in its internal representation, i.e. without api conversion.
     */
    public static JavaName valueAccessorName(JavaPojoMember member) {
      return member.getGetterName().append(SUFFIX);
    }

    /** Reads whether a property carrying a presence flag is set. */
    public static JavaName flagAccessorName(JavaPojoMember member) {
      return member.getGetterName().append(SUFFIX).append("Flag");
    }
  }

  public static class Composition {

    private Composition() {}

    public static JavaName isValidAgainstNoSchemaMethodName(
        DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("isValidAgainstNo%sSchema", type.getName()));
    }

    public static JavaName getValidCountMethodName(DiscriminatableJavaComposition.Type type) {
      return JavaName.fromString(String.format("%sValidCount", type.getName().startLowerCase()));
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
      return JavaName.fromString(String.format("%sValidCount", type.getName().startLowerCase()));
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
      return Name.ofString(String.format("validAgainst%s", pojo.getClassName()));
    }

    public static Name asConversionMethodName(JavaPojo pojo) {
      return Name.ofString(String.format("as%s", pojo.getClassName()));
    }

    public static Name dtoMappingArgumentName(JavaPojo pojo) {
      return Name.ofString(String.format("on%s", pojo.getClassName()));
    }
  }

  /**
   * The plain names of the methods every dto carries. Those which may be renamed carry none of the
   * prefixes a property produces ({@code get}, {@code with}, {@code is}), so no property can reach
   * them. See {@code doc/115_name_collisions.md}.
   */
  public static class Framework {
    private Framework() {}

    public static JavaName propertyCount() {
      // Carries @Min/@Max for minProperties/maxProperties, hence it must stay getter-shaped.
      return JavaName.fromString("getPropertyCount");
    }

    public static JavaName additionalProperties() {
      return JavaName.fromString("additionalProperties");
    }

    public static JavaName additionalProperty() {
      return JavaName.fromString("additionalProperty");
    }

    public static JavaName allAdditionalPropertiesHaveCorrectType() {
      // Carries @AssertTrue, hence it must stay getter-shaped.
      return JavaName.fromString("isAllAdditionalPropertiesHaveCorrectType");
    }
  }

  public static JavaName getIsMultipleOfValidMethodName(JavaName memberName) {
    return memberName.startUpperCase().prefix("is").append("MultipleOfValid");
  }
}
