package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.ListMemberMappingWriterBuilder.fullListMemberMappingWriterBuilder;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
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
public class ListMemberMappingWriter {
  private final JavaPojoMember member;
  private final String prefix;
  private final Function<JavaPojoMember, Writer> mapListItemType;
  private final Function<JavaPojoMember, Writer> wrapListItem;
  private final Function<JavaPojoMember, Writer> mapListType;
  private final Function<JavaPojoMember, Writer> wrapList;
  private final boolean trailingSemicolon;

  public static Writer fullAutoListMemberMappingWriter(JavaPojoMember member, String prefix) {
    return fullListMemberMappingWriterBuilder()
        .member(member)
        .prefix(prefix)
        .autoMapListItemType()
        .autoWrapListItem()
        .autoMapListType()
        .autoWrapList()
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

  @FieldBuilder(fieldName = "mapListItemType", disableDefaultMethods = true)
  public static class MapListItemTypeFieldBuilder {
    static Function<JavaPojoMember, Writer> mapListItemTypeNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> mapListItemType(JavaArrayType javaArrayType) {
      return member ->
          javaArrayType
              .getItemType()
              .getApiType()
              .map(itemApiType -> conversionWriter(itemApiType, "i"))
              .map(
                  writer -> javaWriter().print("i -> %s", writer.asString()).refs(writer.getRefs()))
              .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }

    static Function<JavaPojoMember, Writer> autoMapListItemType() {
      return member ->
          member
              .getJavaType()
              .onArrayType()
              .map(MapListItemTypeFieldBuilder::mapListItemType)
              .orElse(mapListItemTypeNotNecessary())
              .apply(member);
    }
  }

  @FieldBuilder(fieldName = "wrapListItem", disableDefaultMethods = true)
  public static class WrapListItemFieldBuilder {
    static Function<JavaPojoMember, Writer> wrapListItemNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> wrapOptionalListItem() {
      return member -> javaWriter().print("Optional::ofNullable").ref(JavaRefs.JAVA_UTIL_OPTIONAL);
    }

    static Function<JavaPojoMember, Writer> autoWrapListItem() {
      return member ->
          member.getJavaType().isNullableItemsArrayType()
              ? wrapOptionalListItem().apply(member)
              : wrapListItemNotNecessary().apply(member);
    }
  }

  @FieldBuilder(fieldName = "mapListType", disableDefaultMethods = true)
  public static class MapListTypeFieldBuilder {
    static Function<JavaPojoMember, Writer> mapListTypeNotNecessary() {
      return member -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> mapListType(JavaArrayType javaArrayType) {
      return member ->
          javaArrayType
              .getApiType()
              .map(listApiType -> conversionWriter(listApiType, "l"))
              .map(
                  writer -> javaWriter().print("l -> %s", writer.asString()).refs(writer.getRefs()))
              .orElse(mapListTypeNotNecessary().apply(member));
    }

    static Function<JavaPojoMember, Writer> autoMapListType() {
      return member ->
          member
              .getJavaType()
              .onArrayType()
              .map(MapListTypeFieldBuilder::mapListType)
              .orElse(mapListTypeNotNecessary())
              .apply(member);
    }
  }

  @FieldBuilder(fieldName = "wrapList", disableDefaultMethods = true)
  public static class WrapListFieldBuilder {
    static Function<JavaPojoMember, Writer> wrapListNotNecessary() {
      return ignore -> javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> wrapOptionalList() {
      return ignore -> javaWriter().print("Optional::ofNullable").ref(JavaRefs.JAVA_UTIL_OPTIONAL);
    }

    static Function<JavaPojoMember, Writer> wrapTristateList() {
      return member ->
          javaWriter()
              .print("l -> Tristate.ofNullableAndNullFlag(l, %s)", member.getIsNullFlagName())
              .ref(OpenApiUtilRefs.TRISTATE);
    }

    static Function<JavaPojoMember, Writer> autoWrapList() {
      return member -> {
        if (member.isRequiredAndNotNullable()) {
          return wrapListNotNecessary().apply(member);
        } else if (member.isRequiredAndNullable() || member.isOptionalAndNotNullable()) {
          return wrapOptionalList().apply(member);
        } else {
          return wrapTristateList().apply(member);
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
  public static Writer build(ListMemberMappingWriter mappingWriter) {
    return javaWriter()
        .println("%s%s(", mappingWriter.prefix, MapListMethod.METHOD_NAME)
        .tab(2)
        .println("%s,", mappingWriter.member.getName())
        .append(2, mappingWriter.mapListItemType.apply(mappingWriter.member).println(","))
        .append(2, mappingWriter.wrapListItem.apply(mappingWriter.member).println(","))
        .append(2, mappingWriter.mapListType.apply(mappingWriter.member).println(","))
        .append(2, mappingWriter.wrapList.apply(mappingWriter.member))
        .print(")%s", mappingWriter.trailingSemicolon ? ";" : "");
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return ToApiTypeConversion.toApiTypeConversion(
        apiType, variableName, ConversionGenerationMode.NO_NULL_CHECK);
  }
}
