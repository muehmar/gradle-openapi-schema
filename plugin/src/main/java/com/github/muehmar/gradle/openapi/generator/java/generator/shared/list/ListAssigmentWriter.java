package com.github.muehmar.gradle.openapi.generator.java.generator.shared.list;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
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
  private final Mode mode;
  private final Writer unwrapList;
  private final Writer unmapListType;
  private final Writer unwrapListItem;
  private final Writer unmapListItemType;

  public static Writer fullAutoListAssignmentWriter(JavaPojoMember member) {
    return fullListAssigmentWriterBuilder()
        .member(member)
        .fieldAssigment()
        .autoUnwrapList(member)
        .autoUnmapListType(member)
        .autoUnwrapListItem(member)
        .autoUnmapListItemType(member)
        .build();
  }

  @FieldBuilder(fieldName = "mode", disableDefaultMethods = true)
  public static class ModeFieldBuilder {
    static Mode fieldAssigment() {
      return Mode.FIELD_ASSIGNMENT;
    }

    static Mode expressionOnly() {
      return Mode.EXPRESSION_ONLY;
    }
  }

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

    static Writer unwrapList(UnwrapListFunction function) {
      switch (function) {
        case IDENTITY:
          return unwrapListNotNecessary();
        case UNWRAP_OPTIONAL:
          return unwrapOptionalList();
        case UNWRAP_TRISTATE:
          return unwrapTristateList();
        default:
          throw new IllegalArgumentException("Unknown unwrap list function: " + function);
      }
    }

    static Writer autoUnwrapList(JavaPojoMember member) {
      if (member.isRequiredAndNullable() || member.isOptionalAndNotNullable()) {
        return unwrapOptionalList();
      } else if (member.isOptionalAndNullable()) {
        return unwrapTristateList();
      } else {
        return unwrapListNotNecessary();
      }
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

    static Writer autoUnmapListType(JavaPojoMember member) {
      return member
          .getJavaType()
          .onArrayType()
          .map(UnmapListTypeFieldBuilder::unmapListType)
          .orElseGet(UnmapListTypeFieldBuilder::unmapListTypeNotNecessary);
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

    static Writer unwrapListItem(UnwrapListItemFunction function) {
      switch (function) {
        case IDENTITY:
          return unwrapListItemNotNecessary();
        case UNWRAP_OPTIONAL:
          return unwrapOptionalListItem();
        default:
          throw new IllegalArgumentException("Unknown unwrap list item function: " + function);
      }
    }

    static Writer autoUnwrapListItem(JavaPojoMember member) {
      return member.getJavaType().isNullableItemsArrayType()
          ? unwrapOptionalListItem()
          : unwrapListItemNotNecessary();
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

    static Writer autoUnmapListItemType(JavaPojoMember member) {
      return member
          .getJavaType()
          .onArrayType()
          .map(UnmapListItemTypeFieldBuilder::unmapListItemType)
          .orElseGet(UnmapListItemTypeFieldBuilder::unmapListItemTypeNotNecessary);
    }
  }

  @BuildMethod
  public static Writer build(ListAssigmentWriter listAssigmentWriter) {
    final Mode mode = listAssigmentWriter.mode;

    if (isIdentityWriter(listAssigmentWriter.unwrapList)
        && isIdentityWriter(listAssigmentWriter.unmapListType)
        && isIdentityWriter(listAssigmentWriter.unwrapListItem)
        && isIdentityWriter(listAssigmentWriter.unmapListItemType)) {
      return mode.initialWriter(listAssigmentWriter.member, false)
          .println("%s%s", listAssigmentWriter.member.getName(), mode.trailingComma());
    }

    return mode.initialWriter(listAssigmentWriter.member, true)
        .tab(mode.tabOffset())
        .println("%s(", UnmapListMethod.METHOD_NAME)
        .tab(mode.tabOffset() + 2)
        .println("%s,", listAssigmentWriter.member.getName())
        .append(mode.tabOffset() + 2, listAssigmentWriter.unwrapList.println(","))
        .append(mode.tabOffset() + 2, listAssigmentWriter.unmapListType.println(","))
        .append(mode.tabOffset() + 2, listAssigmentWriter.unwrapListItem.println(","))
        .append(mode.tabOffset() + 2, listAssigmentWriter.unmapListItemType)
        .tab(mode.tabOffset())
        .println(")%s", mode.trailingComma());
  }

  private static boolean isIdentityWriter(Writer writer) {
    return writer.asString().equals("Function.identity()");
  }

  private static Writer conversionWriter(ApiType apiType, String variableName) {
    return FromApiTypeConversion.fromApiTypeConversion(
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

  public enum UnwrapListFunction {
    IDENTITY,
    UNWRAP_OPTIONAL,
    UNWRAP_TRISTATE
  }

  public enum UnwrapListItemFunction {
    IDENTITY,
    UNWRAP_OPTIONAL
  }
}
