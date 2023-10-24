package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;

public class MethodNames {
  private MethodNames() {}

  public static class Composition {
    public enum CompositionType {
      ONE_OF("OneOf"),
      ANY_OF("AnyOf");

      private final String type;

      CompositionType(String type) {
        this.type = type;
      }

      public String asString() {
        return type;
      }

      @Override
      public String toString() {
        return asString();
      }
    }

    private Composition() {}

    public static JavaName isValidAgainstNoSchemaMethodName(CompositionType type) {
      return JavaName.fromString(String.format("isValidAgainstNo%sSchema", type.asString()));
    }

    public static JavaName getValidCountMethodName(CompositionType type) {
      return JavaName.fromString(String.format("get%sValidCount", type.asString()));
    }

    public static JavaName getInvalidCompositionDtosMethodName() {
      return JavaName.fromString("getInvalidCompositionDtos");
    }

    public static class OneOf {
      private OneOf() {}

      public static JavaName isValidAgainstTheCorrectSchemaMethodName() {
        return JavaName.fromString("isValidAgainstTheCorrectSchema");
      }

      public static JavaName getOneOfMethodName() {
        return JavaName.fromString("getOneOf");
      }

      public static JavaName foldOneOfMethodName() {
        return JavaName.fromString("foldOneOf");
      }

      public static JavaName getOneOfValidCountMethodName() {
        return JavaName.fromString("getOneOfValidCount");
      }

      public static JavaName isValidAgainstMoreThanOneSchemaMethodName() {
        return JavaName.fromString("isValidAgainstMoreThanOneSchema");
      }
    }

    public static class AnyOf {
      private AnyOf() {}

      public static JavaName foldAnyOfMethodName() {
        return JavaName.fromString("foldAnyOf");
      }

      public static JavaName getAnyOfValidCountMethodName() {
        return JavaName.fromString("getAnyOfValidCount");
      }

      public static JavaName getAnyOfMethodName() {
        return JavaName.fromString("getAnyOf");
      }
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
}
