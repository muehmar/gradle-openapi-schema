package com.github.muehmar.gradle.openapi.generator.java.generator.shared.map;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.UnmapMapMethod;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.BuildMethod;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.AllArgsConstructor;

@PojoBuilder
@AllArgsConstructor
public class MapAssignmentWriter {
  private final JavaPojoMember member;
  private final Mode mode;
  private final Writer unwrapMap;
  private final Writer unmapMapType;
  private final Writer unwrapMapItem;
  private final Writer unmapMapItemType;

  @FieldBuilder(fieldName = "mode", disableDefaultMethods = true)
  public static class ModeFieldBuilder {
    static Mode fieldAssigment() {
      return Mode.FIELD_ASSIGNMENT;
    }

    static Mode expressionOnly() {
      return Mode.EXPRESSION_ONLY;
    }
  }

  @FieldBuilder(fieldName = "unwrapMap", disableDefaultMethods = true)
  public static class UnwrapMapFieldBuilder {
    static Writer unwrapMapNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unwrapOptionalMap() {
      return javaWriter().print("l -> l.orElse(null)");
    }

    static Writer unwrapTristateMap() {
      return javaWriter()
          .print("l -> l.onValue(val -> val).onNull(() -> null).onAbsent(() -> null)");
    }

    static Writer unwrapMap(UnwrapMapFunction function) {
      switch (function) {
        case IDENTITY:
          return unwrapMapNotNecessary();
        case UNWRAP_OPTIONAL:
          return unwrapOptionalMap();
        case UNWRAP_TRISTATE:
          return unwrapTristateMap();
        default:
          throw new IllegalArgumentException("Unknown unwrap list function: " + function);
      }
    }
  }

  @FieldBuilder(fieldName = "unmapMapType", disableDefaultMethods = true)
  public static class UnmapMapTypeFieldBuilder {
    static Writer unmapMapTypeNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unmapMapType(JavaMapType javaMapType) {
      return javaMapType
          .getApiType()
          .map(mapApiType -> conversionWriter(mapApiType, "l"))
          .map(writer -> javaWriter().print("l -> %s", writer.asString()).refs(writer.getRefs()))
          .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }
  }

  @FieldBuilder(fieldName = "unwrapMapItem", disableDefaultMethods = true)
  public static class UnwrapMapItemFieldBuilder {
    static Writer unwrapMapItemNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unwrapOptionalMapItem() {
      return javaWriter().print("i -> i.orElse(null)");
    }

    static Writer unwrapMapItem(UnwrapMapItemFunction function) {
      switch (function) {
        case IDENTITY:
          return unwrapMapItemNotNecessary();
        case UNWRAP_OPTIONAL:
          return unwrapOptionalMapItem();
        default:
          throw new IllegalArgumentException("Unknown unwrap list item function: " + function);
      }
    }
  }

  @FieldBuilder(fieldName = "unmapMapItemType", disableDefaultMethods = true)
  public static class UnmapMapItemTypeFieldBuilder {
    static Writer unmapMapItemTypeNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unmapMapItemType(JavaMapType javaMapType) {
      return javaMapType
          .getValue()
          .getApiType()
          .map(itemApiType -> conversionWriter(itemApiType, "i"))
          .map(writer -> javaWriter().print("i -> %s", writer.asString()).refs(writer.getRefs()))
          .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }
  }

  @BuildMethod
  public static Writer build(MapAssignmentWriter listAssigmentWriter) {
    final Mode mode = listAssigmentWriter.mode;

    return mode.initialWriter(listAssigmentWriter.member)
        .tab(mode.tabOffset())
        .println("%s(", UnmapMapMethod.METHOD_NAME)
        .tab(mode.tabOffset() + 2)
        .println("%s,", listAssigmentWriter.member.getName())
        .append(mode.tabOffset() + 2, listAssigmentWriter.unwrapMap.println(","))
        .append(mode.tabOffset() + 2, listAssigmentWriter.unmapMapType.println(","))
        .append(mode.tabOffset() + 2, listAssigmentWriter.unwrapMapItem.println(","))
        .append(mode.tabOffset() + 2, listAssigmentWriter.unmapMapItemType)
        .tab(mode.tabOffset())
        .println(")%s", mode.trailingComma());
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return FromApiTypeConversion.fromApiTypeConversion(
        apiType, variableName, ConversionGenerationMode.NO_NULL_CHECK);
  }

  enum Mode {
    EXPRESSION_ONLY {
      @Override
      Writer initialWriter(JavaPojoMember member) {
        return javaWriter();
      }

      @Override
      int tabOffset() {
        return 0;
      }

      @Override
      String trailingComma() {
        return "";
      }
    },
    FIELD_ASSIGNMENT {
      @Override
      Writer initialWriter(JavaPojoMember member) {
        return javaWriter().println("this.%s =", member.getName());
      }

      @Override
      int tabOffset() {
        return 2;
      }

      @Override
      String trailingComma() {
        return ";";
      }
    };

    abstract Writer initialWriter(JavaPojoMember member);

    abstract int tabOffset();

    abstract String trailingComma();
  }

  public enum UnwrapMapFunction {
    IDENTITY,
    UNWRAP_OPTIONAL,
    UNWRAP_TRISTATE
  }

  public enum UnwrapMapItemFunction {
    IDENTITY,
    UNWRAP_OPTIONAL
  }
}
