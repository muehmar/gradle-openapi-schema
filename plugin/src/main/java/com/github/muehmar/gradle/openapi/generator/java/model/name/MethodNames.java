package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;

public class MethodNames {
  private MethodNames() {}

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
