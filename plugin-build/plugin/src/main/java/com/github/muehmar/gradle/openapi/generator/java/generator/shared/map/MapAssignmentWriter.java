package com.github.muehmar.gradle.openapi.generator.java.generator.shared.map;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.UnmapMapMethod;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.LocalVariableName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.BuildMethod;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.Function;
import lombok.AllArgsConstructor;

@PojoBuilder
@AllArgsConstructor
public class MapAssignmentWriter {
  private final JavaPojoMember member;
  private final Mode mode;
  private final Function<JavaPojoMember, Writer> unwrapMap;
  private final Function<JavaPojoMember, Writer> unmapMapType;
  private final Function<JavaPojoMember, Writer> unwrapMapItem;
  private final Function<JavaPojoMember, Writer> unmapMapItemType;

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
    static Function<JavaPojoMember, Writer> unwrapMapNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unwrapOptionalMap() {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("m").withPojoMemberAsMethodArgument(member);
        return javaWriter().print("%s -> %s.orElse(null)", variableName, variableName);
      };
    }

    static Function<JavaPojoMember, Writer> unwrapTristateMap() {
      return member -> {
        final LocalVariableName tristateVariableName =
            LocalVariableName.of("t").withPojoMemberAsMethodArgument(member);
        final LocalVariableName mapVariableName =
            LocalVariableName.of("m").withPojoMemberAsMethodArgument(member);

        return javaWriter()
            .print(
                "%s -> %s.onValue(%s -> %s).onNull(() -> null).onAbsent(() -> null)",
                tristateVariableName, tristateVariableName, mapVariableName, mapVariableName);
      };
    }

    static Function<JavaPojoMember, Writer> unwrapMap(UnwrapMapFunction function) {
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

    static Function<JavaPojoMember, Writer> autoUnwrapMap(JavaPojoMember member) {
      if (member.isRequiredAndNotNullable()) {
        return unwrapMapNotNecessary();
      } else if (member.isRequiredAndNullable() || member.isOptionalAndNotNullable()) {
        return unwrapOptionalMap();
      } else {
        return unwrapTristateMap();
      }
    }
  }

  @FieldBuilder(fieldName = "unmapMapType", disableDefaultMethods = true)
  public static class UnmapMapTypeFieldBuilder {
    static Function<JavaPojoMember, Writer> unmapMapTypeNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unmapMapType(JavaMapType javaMapType) {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("m").withPojoMemberAsMethodArgument(member);
        return javaMapType
            .getApiType()
            .map(mapApiType -> conversionWriter(mapApiType, variableName.asString()))
            .map(
                writer ->
                    javaWriter()
                        .print("%s -> %s", variableName, writer.asString())
                        .refs(writer.getRefs()))
            .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
      };
    }

    static Function<JavaPojoMember, Writer> autoUnmapMapType(JavaPojoMember member) {
      return member
          .getJavaType()
          .onMapType()
          .map(UnmapMapTypeFieldBuilder::unmapMapType)
          .orElseGet(UnmapMapTypeFieldBuilder::unmapMapTypeNotNecessary);
    }
  }

  @FieldBuilder(fieldName = "unwrapMapItem", disableDefaultMethods = true)
  public static class UnwrapMapItemFieldBuilder {
    static Function<JavaPojoMember, Writer> unwrapMapItemNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unwrapOptionalMapItem() {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("i").withPojoMemberAsMethodArgument(member);
        return javaWriter().print("%s -> i.orElse(null)", variableName);
      };
    }

    static Function<JavaPojoMember, Writer> unwrapMapItem(UnwrapMapItemFunction function) {
      switch (function) {
        case IDENTITY:
          return unwrapMapItemNotNecessary();
        case UNWRAP_OPTIONAL:
          return unwrapOptionalMapItem();
        default:
          throw new IllegalArgumentException("Unknown unwrap list item function: " + function);
      }
    }

    static Function<JavaPojoMember, Writer> autoUnwrapMapItem(JavaPojoMember member) {
      return member.getJavaType().isNullableValuesMapType()
          ? unwrapOptionalMapItem()
          : unwrapMapItemNotNecessary();
    }
  }

  @FieldBuilder(fieldName = "unmapMapItemType", disableDefaultMethods = true)
  public static class UnmapMapItemTypeFieldBuilder {
    static Function<JavaPojoMember, Writer> unmapMapItemTypeNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unmapMapItemType(JavaMapType javaMapType) {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("i").withPojoMemberAsMethodArgument(member);
        return javaMapType
            .getValue()
            .getApiType()
            .map(itemApiType -> conversionWriter(itemApiType, variableName.asString()))
            .map(
                writer ->
                    javaWriter()
                        .print("%s -> %s", variableName, writer.asString())
                        .refs(writer.getRefs()))
            .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
      };
    }

    static Function<JavaPojoMember, Writer> autoUnmapMapItemType(JavaPojoMember member) {
      return member
          .getJavaType()
          .onMapType()
          .map(UnmapMapItemTypeFieldBuilder::unmapMapItemType)
          .orElseGet(UnmapMapItemTypeFieldBuilder::unmapMapItemTypeNotNecessary);
    }
  }

  @BuildMethod
  public static Writer build(MapAssignmentWriter maw) {
    final Mode mode = maw.mode;

    if (maw.isAllFunctionIdentityWriter()) {
      return mode.initialWriter(maw.member, false)
          .tab(mode.tabOffset())
          .println("%s%s", maw.member.getName(), mode.trailingComma());
    }

    if (maw.isOnlyUnwrapOptionalMapWriter()) {
      return mode.initialWriter(maw.member, false)
          .tab(mode.tabOffset())
          .println("%s.orElse(null)%s", maw.member.getName(), mode.trailingComma());
    }

    if (maw.isOnlyUnwrapTristateMapWriter()) {
      return mode.initialWriter(maw.member, false)
          .tab(mode.tabOffset())
          .println(
              "%s.%s%s",
              maw.member.getName(), maw.member.tristateToProperty(), mode.trailingComma());
    }

    return mode.initialWriter(maw.member, true)
        .tab(mode.tabOffset())
        .println("%s(", UnmapMapMethod.METHOD_NAME)
        .tab(mode.tabOffset() + 2)
        .println("%s,", maw.member.getName())
        .append(mode.tabOffset() + 2, maw.unwrapMapWriter().println(","))
        .append(mode.tabOffset() + 2, maw.unmapMapTypeWriter().println(","))
        .append(mode.tabOffset() + 2, maw.unwrapMapItemWriter().println(","))
        .append(mode.tabOffset() + 2, maw.unmapMapItemTypeWriter())
        .tab(mode.tabOffset())
        .println(")%s", mode.trailingComma());
  }

  private Writer unwrapMapWriter() {
    return unwrapMap.apply(member);
  }

  private Writer unmapMapTypeWriter() {
    return unmapMapType.apply(member);
  }

  private Writer unwrapMapItemWriter() {
    return unwrapMapItem.apply(member);
  }

  private Writer unmapMapItemTypeWriter() {
    return unmapMapItemType.apply(member);
  }

  private boolean isAllFunctionIdentityWriter() {
    return isIdentityWriter(unwrapMapWriter())
        && isIdentityWriter(unmapMapTypeWriter())
        && isIdentityWriter(unwrapMapItemWriter())
        && isIdentityWriter(unmapMapItemTypeWriter());
  }

  private boolean isOnlyUnwrapOptionalMapWriter() {
    return unwrapMapWriter()
            .asString()
            .equals(UnwrapMapFieldBuilder.unwrapOptionalMap().apply(member).asString())
        && isIdentityWriter(unmapMapTypeWriter())
        && isIdentityWriter(unwrapMapItemWriter())
        && isIdentityWriter(unmapMapItemTypeWriter());
  }

  private boolean isOnlyUnwrapTristateMapWriter() {
    return unwrapMapWriter()
            .asString()
            .equals(UnwrapMapFieldBuilder.unwrapTristateMap().apply(member).asString())
        && isIdentityWriter(unmapMapTypeWriter())
        && isIdentityWriter(unwrapMapItemWriter())
        && isIdentityWriter(unmapMapItemTypeWriter());
  }

  private static boolean isIdentityWriter(Writer writer) {
    return writer.asString().equals("Function.identity()");
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return FromApiTypeConversionRenderer.fromApiTypeConversion(
        apiType, variableName, ConversionGenerationMode.NO_NULL_CHECK);
  }

  enum Mode {
    EXPRESSION_ONLY {
      @Override
      Writer initialWriter(JavaPojoMember member, boolean newLine) {
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
      Writer initialWriter(JavaPojoMember member, boolean newLine) {
        final Writer writer = javaWriter().print("this.%s =", member.getName());
        return newLine ? writer.println() : writer.print(" ");
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

    abstract Writer initialWriter(JavaPojoMember member, boolean newLine);

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
