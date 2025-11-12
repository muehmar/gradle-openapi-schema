package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.mapmapping.MapMemberMappingWriterBuilder.fullMapMemberMappingWriterBuilder;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.BuildMethod;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.Function;
import lombok.AllArgsConstructor;

@PojoBuilder(enableStandardBuilder = false)
@AllArgsConstructor
public class MapMemberMappingWriter {
  private final JavaPojoMember member;
  private final String prefix;
  private final Function<JavaPojoMember, Writer> mapMapItemType;
  private final Function<JavaPojoMember, Writer> wrapMapItem;
  private final Function<JavaPojoMember, Writer> mapMapType;
  private final Function<JavaPojoMember, Writer> wrapMap;
  private final boolean trailingSemicolon;

  public static Writer fullAutoMapMemberMappingWriter(JavaPojoMember member, String prefix) {
    return fullMapMemberMappingWriterBuilder()
        .member(member)
        .prefix(prefix)
        .autoMapMapItemType()
        .autoWrapMapItem()
        .autoMapMapType()
        .autoWrapMap()
        .trailingSemicolon()
        .build();
  }

  @FieldBuilder(fieldName = "prefix", disableDefaultMethods = true)
  public static class PrefixFieldBuilder {
    static String prefix(String prefix) {
      return prefix;
    }

    static String noPrefix() {
      return "";
    }
  }

  @FieldBuilder(fieldName = "mapMapItemType", disableDefaultMethods = true)
  public static class MapMapItemTypeFieldBuilder {
    static Function<JavaPojoMember, Writer> mapMapItemTypeNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> mapMapItemType(JavaMapType javaMapType) {
      return member ->
          javaMapType
              .getValue()
              .getApiType()
              .map(itemApiType -> conversionWriter(itemApiType, "i"))
              .map(
                  writer -> javaWriter().print("i -> %s", writer.asString()).refs(writer.getRefs()))
              .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }

    static Function<JavaPojoMember, Writer> autoMapMapItemType() {
      return member ->
          member
              .getJavaType()
              .onMapType()
              .map(MapMapItemTypeFieldBuilder::mapMapItemType)
              .orElse(mapMapItemTypeNotNecessary())
              .apply(member);
    }
  }

  @FieldBuilder(fieldName = "wrapMapItem", disableDefaultMethods = true)
  public static class WrapMapItemFieldBuilder {
    static Function<JavaPojoMember, Writer> wrapMapItemNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> wrapOptionalMapItem() {
      return member -> javaWriter().print("Optional::ofNullable").ref(JavaRefs.JAVA_UTIL_OPTIONAL);
    }

    static Function<JavaPojoMember, Writer> autoWrapMapItem() {
      return member ->
          member.getJavaType().isNullableValuesMapType()
              ? wrapOptionalMapItem().apply(member)
              : wrapMapItemNotNecessary().apply(member);
    }
  }

  @FieldBuilder(fieldName = "mapMapType", disableDefaultMethods = true)
  public static class MapMapTypeFieldBuilder {
    static Function<JavaPojoMember, Writer> mapMapTypeNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> mapMapType(JavaMapType javaMapType) {
      return member ->
          javaMapType
              .getApiType()
              .map(mapApiType -> conversionWriter(mapApiType, "m"))
              .map(
                  writer -> javaWriter().print("m -> %s", writer.asString()).refs(writer.getRefs()))
              .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }

    static Function<JavaPojoMember, Writer> autoMapMapType() {
      return member ->
          member
              .getJavaType()
              .onMapType()
              .map(MapMapTypeFieldBuilder::mapMapType)
              .orElse(mapMapTypeNotNecessary())
              .apply(member);
    }
  }

  @FieldBuilder(fieldName = "wrapMap", disableDefaultMethods = true)
  public static class WrapMapFieldBuilder {
    static Function<JavaPojoMember, Writer> wrapMapNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> wrapOptionalMap() {
      return member -> javaWriter().print("Optional::ofNullable").ref(JavaRefs.JAVA_UTIL_OPTIONAL);
    }

    static Function<JavaPojoMember, Writer> wrapTristateMap() {
      return member ->
          javaWriter()
              .print("m -> Tristate.ofNullableAndNullFlag(m, %s)", member.getIsNullFlagName())
              .ref(OpenApiUtilRefs.TRISTATE);
    }

    static Function<JavaPojoMember, Writer> autoWrapMap() {
      return member -> {
        if (member.isRequiredAndNotNullable()) {
          return wrapMapNotNecessary().apply(member);
        } else if (member.isRequiredAndNullable() || member.isOptionalAndNotNullable()) {
          return wrapOptionalMap().apply(member);
        } else {
          return wrapTristateMap().apply(member);
        }
      };
    }
  }

  @FieldBuilder(fieldName = "trailingSemicolon", disableDefaultMethods = true)
  public static class TrailingSemicolonFieldBuilder {
    static boolean trailingSemicolon() {
      return true;
    }

    static boolean noTrailingSemicolon() {
      return false;
    }
  }

  @BuildMethod
  public static Writer build(MapMemberMappingWriter mappingWriter) {
    if (isIdentityWriter(mappingWriter.mapMapItemType.apply(mappingWriter.member))
        && isIdentityWriter(mappingWriter.wrapMapItem.apply(mappingWriter.member))
        && isIdentityWriter(mappingWriter.mapMapType.apply(mappingWriter.member))
        && isIdentityWriter(mappingWriter.wrapMap.apply(mappingWriter.member))) {
      return javaWriter()
          .println(
              "%s%s%s",
              mappingWriter.prefix,
              mappingWriter.member.getName(),
              mappingWriter.trailingSemicolon ? ";" : "");
    }

    return javaWriter()
        .println("%s%s(", mappingWriter.prefix, MapMapMethod.METHOD_NAME)
        .tab(2)
        .println("%s,", mappingWriter.member.getName())
        .append(2, mappingWriter.mapMapItemType.apply(mappingWriter.member).println(","))
        .append(2, mappingWriter.wrapMapItem.apply(mappingWriter.member).println(","))
        .append(2, mappingWriter.mapMapType.apply(mappingWriter.member).println(","))
        .append(2, mappingWriter.wrapMap.apply(mappingWriter.member))
        .print(")%s", mappingWriter.trailingSemicolon ? ";" : "");
  }

  private static boolean isIdentityWriter(Writer writer) {
    return writer.asString().equals("Function.identity()");
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return ToApiTypeConversionRenderer.toApiTypeConversion(
        apiType, variableName, ConversionGenerationMode.NO_NULL_CHECK);
  }
}
