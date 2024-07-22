package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.UnmapListMethod;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.BuildMethod;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.AllArgsConstructor;

@PojoBuilder
@AllArgsConstructor
public class ListAssigmentWriter {
  private final JavaPojoMember member;
  private final Writer unwrapList;
  private final Writer unmapListType;
  private final Writer unwrapListItem;
  private final Writer unmapListItemType;

  @FieldBuilder(fieldName = "unwrapList", disableDefaultMethods = true)
  public static class UnwrapListFieldBuilder {
    static Writer unwrapListNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unwrapOptionalList() {
      return javaWriter().print("l -> l.orElse(null)");
    }

    static Writer unwrapTristateList() {
      return javaWriter()
          .print("l -> l.onValue(val -> val).onNull(() -> null).onAbsent(() -> null)");
    }
  }

  @FieldBuilder(fieldName = "unmapListType", disableDefaultMethods = true)
  public static class UnmapListTypeFieldBuilder {
    static Writer unmapListTypeNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unmapListType(JavaArrayType javaArrayType) {
      return javaArrayType
          .getApiType()
          .map(listApiType -> conversionWriter(listApiType, "l"))
          .map(writer -> javaWriter().print("l -> %s", writer.asString()).refs(writer.getRefs()))
          .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }
  }

  @FieldBuilder(fieldName = "unwrapListItem", disableDefaultMethods = true)
  public static class UnwrapListItemFieldBuilder {
    static Writer unwrapListItemNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unwrapOptionalListItem() {
      return javaWriter().print("i -> i.orElse(null)");
    }
  }

  @FieldBuilder(fieldName = "unmapListItemType", disableDefaultMethods = true)
  public static class UnmapListItemTypeFieldBuilder {
    static Writer unmapListItemTypeNotNecessary() {
      return javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Writer unmapListItemType(JavaArrayType javaArrayType) {
      return javaArrayType
          .getItemType()
          .getApiType()
          .map(itemApiType -> conversionWriter(itemApiType, "i"))
          .map(writer -> javaWriter().print("i -> %s", writer.asString()).refs(writer.getRefs()))
          .orElse(javaWriter().print("Function.identity()").ref(JavaRefs.JAVA_UTIL_FUNCTION));
    }
  }

  @BuildMethod
  public static Writer build(ListAssigmentWriter listAssigmentWriter) {
    return javaWriter()
        .println("this.%s =", listAssigmentWriter.member.getName())
        .tab(2)
        .println("%s(", UnmapListMethod.METHOD_NAME)
        .tab(4)
        .println("%s,", listAssigmentWriter.member.getName())
        .append(4, listAssigmentWriter.unwrapList.println(","))
        .append(4, listAssigmentWriter.unmapListType.println(","))
        .append(4, listAssigmentWriter.unwrapListItem.println(","))
        .append(4, listAssigmentWriter.unmapListItemType)
        .tab(2)
        .println(");");
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return FromApiTypeConversion.fromApiTypeConversion(
        apiType, variableName, ConversionGenerationMode.NO_NULL_CHECK);
  }
}
