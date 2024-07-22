package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping;

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

@PojoBuilder
@AllArgsConstructor
public class MemberMapWriter {
  private final JavaPojoMember member;
  private final String prefix;
  private final Writer mapListItemType;
  private final Writer wrapListItem;
  private final Writer mapListType;
  private final Function<JavaPojoMember, Writer> wrapList;
  private final boolean trailingSemicolon;

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
    static Writer mapListItemTypeNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer mapListItemType(JavaArrayType javaArrayType) {
      return javaArrayType
          .getItemType()
          .getApiType()
          .map(itemApiType -> conversionWriter(itemApiType, "i"))
          .map(writer -> javaWriter().print("i -> %s", writer.asString()).refs(writer.getRefs()))
          .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }
  }

  @FieldBuilder(fieldName = "wrapListItem", disableDefaultMethods = true)
  public static class WrapListItemFieldBuilder {
    static Writer wrapListItemNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer wrapOptionalListItem() {
      return javaWriter().print("Optional::ofNullable").ref(JavaRefs.JAVA_UTIL_OPTIONAL);
    }
  }

  @FieldBuilder(fieldName = "mapListType", disableDefaultMethods = true)
  public static class MapListTypeFieldBuilder {
    static Writer mapListTypeNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer mapListType(JavaArrayType javaArrayType) {
      return javaArrayType
          .getApiType()
          .map(listApiType -> conversionWriter(listApiType, "l"))
          .map(writer -> javaWriter().print("l -> %s", writer.asString()).refs(writer.getRefs()))
          .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
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
  public static Writer build(MemberMapWriter memberMapWriter) {
    return javaWriter()
        .println("%s%s(", memberMapWriter.prefix, MapListMethod.METHOD_NAME)
        .tab(2)
        .println("%s,", memberMapWriter.member.getName())
        .append(2, memberMapWriter.mapListItemType.println(","))
        .append(2, memberMapWriter.wrapListItem.println(","))
        .append(2, memberMapWriter.mapListType.println(","))
        .append(2, memberMapWriter.wrapList.apply(memberMapWriter.member))
        .print(")%s", memberMapWriter.trailingSemicolon ? ";" : "");
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return ToApiTypeConversion.toApiTypeConversion(
        apiType, variableName, ConversionGenerationMode.NO_NULL_CHECK);
  }
}
