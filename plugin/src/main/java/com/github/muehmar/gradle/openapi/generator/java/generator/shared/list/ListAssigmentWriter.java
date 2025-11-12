package com.github.muehmar.gradle.openapi.generator.java.generator.shared.list;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriter.UnwrapListFieldBuilder.unwrapOptionalList;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriter.UnwrapListFieldBuilder.unwrapTristateList;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.UnmapListMethod;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.LocalVariableName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
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
public class ListAssigmentWriter {
  private final JavaPojoMember member;
  private final Mode mode;
  private final Function<JavaPojoMember, Writer> unwrapList;
  private final Function<JavaPojoMember, Writer> unmapListType;
  private final Function<JavaPojoMember, Writer> unwrapListItem;
  private final Function<JavaPojoMember, Writer> unmapListItemType;

  private static final String FUNCTION_IDENTITY = "Function.identity()";

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
    static Function<JavaPojoMember, Writer> unwrapListNotNecessary() {
      return member -> javaWriter().print(FUNCTION_IDENTITY).ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unwrapOptionalList() {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("l").withPojoMemberAsMethodArgument(member);
        return javaWriter().print("%s -> %s.orElse(null)", variableName, variableName);
      };
    }

    static Function<JavaPojoMember, Writer> unwrapTristateList() {
      return member -> {
        final LocalVariableName tristateVariableName =
            LocalVariableName.of("t").withPojoMemberAsMethodArgument(member);
        final LocalVariableName listVariableName =
            LocalVariableName.of("l").withPojoMemberAsMethodArgument(member);
        return javaWriter()
            .print(
                "%s -> %s.onValue(%s -> %s).onNull(() -> null).onAbsent(() -> null)",
                tristateVariableName, tristateVariableName, listVariableName, listVariableName);
      };
    }

    static Function<JavaPojoMember, Writer> unwrapList(UnwrapListFunction function) {
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

    static Function<JavaPojoMember, Writer> autoUnwrapList(JavaPojoMember member) {
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
    static Function<JavaPojoMember, Writer> unmapListTypeNotNecessary() {
      return member -> javaWriter().print(FUNCTION_IDENTITY).ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unmapListType(JavaArrayType javaArrayType) {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("l").withPojoMemberAsMethodArgument(member);
        return javaArrayType
            .getApiType()
            .map(listApiType -> conversionWriter(listApiType, variableName.asString()))
            .map(
                writer ->
                    javaWriter()
                        .print("%s -> %s", variableName, writer.asString())
                        .refs(writer.getRefs()))
            .orElse(javaWriter().print(FUNCTION_IDENTITY).ref(JavaRefs.JAVA_UTIL_FUNCTION));
      };
    }

    static Function<JavaPojoMember, Writer> autoUnmapListType(JavaPojoMember member) {
      return member
          .getJavaType()
          .onArrayType()
          .map(UnmapListTypeFieldBuilder::unmapListType)
          .orElseGet(UnmapListTypeFieldBuilder::unmapListTypeNotNecessary);
    }
  }

  @FieldBuilder(fieldName = "unwrapListItem", disableDefaultMethods = true)
  public static class UnwrapListItemFieldBuilder {
    static Function<JavaPojoMember, Writer> unwrapListItemNotNecessary() {
      return member -> javaWriter().print(FUNCTION_IDENTITY).ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unwrapOptionalListItem() {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("i").withPojoMemberAsMethodArgument(member);
        return javaWriter().print("%s -> %s.orElse(null)", variableName, variableName);
      };
    }

    static Function<JavaPojoMember, Writer> unwrapListItem(UnwrapListItemFunction function) {
      switch (function) {
        case IDENTITY:
          return unwrapListItemNotNecessary();
        case UNWRAP_OPTIONAL:
          return unwrapOptionalListItem();
        default:
          throw new IllegalArgumentException("Unknown unwrap list item function: " + function);
      }
    }

    static Function<JavaPojoMember, Writer> autoUnwrapListItem(JavaPojoMember member) {
      return member.getJavaType().isNullableItemsArrayType()
          ? unwrapOptionalListItem()
          : unwrapListItemNotNecessary();
    }
  }

  @FieldBuilder(fieldName = "unmapListItemType", disableDefaultMethods = true)
  public static class UnmapListItemTypeFieldBuilder {
    static Function<JavaPojoMember, Writer> unmapListItemTypeNotNecessary() {
      return member -> javaWriter().print(FUNCTION_IDENTITY).ref(JavaRefs.JAVA_UTIL_FUNCTION);
    }

    static Function<JavaPojoMember, Writer> unmapListItemType(JavaArrayType javaArrayType) {
      return member -> {
        final LocalVariableName variableName =
            LocalVariableName.of("i").withPojoMemberAsMethodArgument(member);
        return javaArrayType
            .getItemType()
            .getApiType()
            .map(itemApiType -> conversionWriter(itemApiType, variableName.asString()))
            .map(
                writer ->
                    javaWriter()
                        .print("%s -> %s", variableName, writer.asString())
                        .refs(writer.getRefs()))
            .orElse(javaWriter().print(FUNCTION_IDENTITY).ref(JavaRefs.JAVA_UTIL_FUNCTION));
      };
    }

    static Function<JavaPojoMember, Writer> autoUnmapListItemType(JavaPojoMember member) {
      return member
          .getJavaType()
          .onArrayType()
          .map(UnmapListItemTypeFieldBuilder::unmapListItemType)
          .orElseGet(UnmapListItemTypeFieldBuilder::unmapListItemTypeNotNecessary);
    }
  }

  @BuildMethod
  public static Writer build(ListAssigmentWriter law) {
    final Mode mode = law.mode;
    final JavaPojoMember member = law.member;

    if (law.isAllFunctionIdentityWriter()) {
      return mode.initialWriter(member, false)
          .println("%s%s", member.getName(), mode.trailingComma());
    }

    if (law.isOnlyUnwrapOptionalListWriter()) {
      return mode.initialWriter(member, false)
          .println("%s.orElse(null)%s", member.getName(), mode.trailingComma());
    }

    if (law.isOnlyUnwrapTristateListWriter()) {
      return mode.initialWriter(member, false)
          .println("%s.%s%s", member.getName(), member.tristateToProperty(), mode.trailingComma());
    }

    return mode.initialWriter(member, true)
        .tab(mode.tabOffset())
        .println("%s(", UnmapListMethod.METHOD_NAME)
        .tab(mode.tabOffset() + 2)
        .println("%s,", member.getName())
        .append(mode.tabOffset() + 2, law.unwrapListWriter().println(","))
        .append(mode.tabOffset() + 2, law.unmapListTypeWriter().println(","))
        .append(mode.tabOffset() + 2, law.unwrapListItemWriter().println(","))
        .append(mode.tabOffset() + 2, law.unmapListItemTypeWriter())
        .tab(mode.tabOffset())
        .println(")%s", mode.trailingComma());
  }

  private Writer unwrapListWriter() {
    return unwrapList.apply(member);
  }

  private Writer unmapListTypeWriter() {
    return unmapListType.apply(member);
  }

  private Writer unwrapListItemWriter() {
    return unwrapListItem.apply(member);
  }

  private Writer unmapListItemTypeWriter() {
    return unmapListItemType.apply(member);
  }

  private boolean isAllFunctionIdentityWriter() {
    return isIdentityWriter(unwrapListWriter())
        && isIdentityWriter(unmapListTypeWriter())
        && isIdentityWriter(unwrapListItemWriter())
        && isIdentityWriter(unmapListItemTypeWriter());
  }

  private boolean isOnlyUnwrapOptionalListWriter() {
    return unwrapListWriter().asString().equals(unwrapOptionalList().apply(member).asString())
        && isIdentityWriter(unmapListTypeWriter())
        && isIdentityWriter(unwrapListItemWriter())
        && isIdentityWriter(unmapListItemTypeWriter());
  }

  private boolean isOnlyUnwrapTristateListWriter() {
    return unwrapListWriter().asString().equals(unwrapTristateList().apply(member).asString())
        && isIdentityWriter(unmapListTypeWriter())
        && isIdentityWriter(unwrapListItemWriter())
        && isIdentityWriter(unmapListItemTypeWriter());
  }

  private static boolean isIdentityWriter(Writer writer) {
    return writer.asString().equals(FUNCTION_IDENTITY);
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
