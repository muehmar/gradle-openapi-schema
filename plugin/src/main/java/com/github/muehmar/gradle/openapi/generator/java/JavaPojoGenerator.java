package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Pojo;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.PojoMember;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class JavaPojoGenerator implements PojoGenerator {
  private final Supplier<Writer> createWriter;
  private final Resolver resolver;

  public JavaPojoGenerator(Supplier<Writer> createWriter) {
    this.createWriter = createWriter;
    this.resolver = new JavaResolver();
  }

  @Override
  public void generatePojo(Pojo pojo, PojoSettings pojoSettings) {

    final String packagePath =
        pojoSettings.getPackageName().replace(".", "/").replaceFirst("^/", "");

    final Writer writer = createWriter.get();
    printPackage(writer, pojoSettings.getPackageName());
    printImports(writer, pojo, pojoSettings);
    printClassStart(writer, pojo);

    printFields(writer, pojo, pojoSettings);
    printConstructor(writer, pojo, pojoSettings);

    printEnums(writer, pojo);

    printGetters(writer, pojo, pojoSettings);
    printWithers(writer, pojo);

    printEqualsAndHash(writer, pojo);
    printToString(writer, pojo);

    printBuilder(writer, pojo, pojoSettings);
    if (pojoSettings.isEnableSafeBuilder()) {
      printSafeBuilder(writer, pojo);
    }
    printClassEnd(writer);

    writer.close(packagePath + "/" + pojo.className(resolver) + ".java");
  }

  private void printPackage(Writer writer, String packageName) {
    writer.println("package %s;", packageName);
  }

  private void printImports(Writer writer, Pojo pojo, PojoSettings settings) {
    writer.println();
    writer.println("import java.util.Objects;");
    writer.println("import java.util.Optional;");
    writer.println();

    if (settings.isJacksonJson()) {
      writer.println("import com.fasterxml.jackson.annotation.JsonIgnore;");
      writer.println("import com.fasterxml.jackson.annotation.JsonProperty;");
      writer.println("import com.fasterxml.jackson.annotation.JsonCreator;");
      writer.println("import com.fasterxml.jackson.annotation.JsonValue;");
      writer.println();
    }

    pojo.getMembers()
        .flatMap(PojoMember::getImports)
        .distinct(Function.identity())
        .forEach(classImport -> writer.println("import %s;", classImport));
  }

  private void printJavaDoc(Writer writer, int tabs, String javadoc) {
    writer.tab(tabs).println("/**");
    final ArrayList<String> words = new ArrayList<>(Arrays.asList(javadoc.split("\\s")));
    int idx = 0;
    while (idx < words.size()) {
      final ArrayList<String> lineWords = new ArrayList<>();
      int characters = 0;
      while (characters < 80 && idx < words.size()) {
        final String word = words.get(idx);
        lineWords.add(word);
        characters += word.length();
        idx++;
      }
      writer.tab(tabs).println(" * %s", String.join(" ", lineWords));
    }

    writer.tab(tabs).println(" */");
  }

  private void printClassStart(Writer writer, Pojo pojo) {
    writer.println();
    printJavaDoc(writer, 0, pojo.getDescription());
    writer.tab(0).println("public class %s {", pojo.className(resolver));
  }

  private void printFields(Writer writer, Pojo pojo, PojoSettings settings) {
    if (pojo.isArray() && settings.isJacksonJson()) {
      writer.tab(1).println("@JsonValue");
    }

    pojo.getMembers()
        .forEach(
            member ->
                writer
                    .tab(1)
                    .println(
                        "private final %s %s;",
                        member.getTypeName(resolver), member.memberName(resolver)));
  }

  private void printConstructor(Writer writer, Pojo pojo, PojoSettings settings) {
    writer.println();
    if (settings.isJacksonJson()) {
      if (pojo.isArray()) {
        writer.tab(1).println("@JsonCreator");
      } else {
        writer.tab(1).println("@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)");
      }
    }
    writer.tab(1).println("public %s(", pojo.className(resolver));

    final PList<String> memberArguments = createMemberArguments(pojo, settings);

    for (int i = 0; i < memberArguments.size(); i++) {
      writer.tab(3).print(memberArguments.apply(i));
      if (i == memberArguments.size() - 1) {
        writer.println(") {");
      } else {
        writer.println(",");
      }
    }

    pojo.getMembers()
        .forEach(
            member ->
                writer
                    .tab(2)
                    .println(
                        "this.%s = %s;", member.memberName(resolver), member.memberName(resolver)));

    writer.tab(1).println("}");
  }

  private PList<String> createMemberArguments(Pojo pojo, PojoSettings settings) {
    final Function<PojoMember, String> createJsonSupport =
        member -> {
          if (settings.isJacksonJson()) {
            return String.format("@JsonProperty(\"%s\") ", member.memberName(resolver));
          } else {
            return "";
          }
        };
    return pojo.getMembers()
        .map(
            member -> {
              if (pojo.isArray()) {
                return String.format(
                    "%s %s", member.getTypeName(resolver), member.memberName(resolver));
              } else {
                return String.format(
                    "%s%s %s",
                    createJsonSupport.apply(member),
                    member.getTypeName(resolver),
                    member.memberName(resolver));
              }
            });
  }

  private String createNamesCommaSeparated(Pojo pojo) {
    final PList<String> formattedPairs =
        pojo.getMembers().map(member -> String.format("%s", member.memberName(resolver)));

    return String.join(", ", formattedPairs);
  }

  protected void printEnums(Writer writer, Pojo pojo) {
    pojo.getMembers()
        .forEach(
            member ->
                member.onEnum(
                    enumMembers -> {
                      writer.println();
                      printJavaDoc(writer, 1, member.getDescription());

                      writer.tab(1).println("public enum %s {", member.getTypeName(resolver));
                      writer.tab(2).println("%s", String.join(", ", enumMembers));
                      writer.tab(1).println("}");
                    }));
  }

  protected void printGetters(Writer writer, Pojo pojo, PojoSettings settings) {
    pojo.getMembers()
        .forEach(
            member -> {
              writer.println();
              printJavaDoc(writer, 1, member.getDescription());

              final boolean nullable = member.isNullable();

              final String returnType =
                  nullable
                      ? String.format("Optional<%s>", member.getTypeName(resolver))
                      : member.getTypeName(resolver);
              final String field =
                  nullable
                      ? String.format("Optional.ofNullable(this.%s)", member.memberName(resolver))
                      : String.format("this.%s", member.memberName(resolver));

              if (nullable && settings.isJacksonJson()) {
                writer.tab(1).println("@JsonIgnore");
              }
              writer
                  .tab(1)
                  .println(
                      "public %s %s%s() {",
                      returnType, member.getterName(resolver), nullable ? "Optional" : "");
              writer.tab(2).println("return %s;", field);
              writer.tab(1).println("}");

              if (nullable) {
                writer.println();
                printJavaDoc(writer, 1, member.getDescription());
                if (settings.isJacksonJson()) {
                  writer.tab(1).println("@JsonProperty(\"%s\")", member.memberName(resolver));
                }
                writer
                    .tab(1)
                    .println(
                        "public %s %sNullable() {",
                        member.getTypeName(resolver), member.getterName(resolver));
                writer.tab(2).println("return %s;", member.memberName(resolver));
                writer.tab(1).println("}");
              }
            });
  }

  protected void printWithers(Writer writer, Pojo pojo) {
    pojo.getMembers()
        .forEach(
            member -> {
              writer.println();
              printJavaDoc(writer, 1, member.getDescription());
              writer
                  .tab(1)
                  .println(
                      "public %s %s(%s %s) {",
                      pojo.className(resolver),
                      member.witherName(resolver),
                      member.getTypeName(resolver),
                      member.memberName(resolver));
              writer
                  .tab(2)
                  .println(
                      "return new %s(%s);",
                      pojo.className(resolver), createNamesCommaSeparated(pojo));
              writer.tab(1).println("}");
            });
  }

  protected void printBuilder(Writer writer, Pojo pojo, PojoSettings settings) {
    if (settings.isDisableSafeBuilder()) {
      writer.println();
      writer.tab(1).println("public static Builder newBuilder() {");
      writer.tab(2).println("return new Builder();");
      writer.tab(1).println("}");
    }

    writer.println();
    writer.tab(1).println("public static final class Builder {");

    if (settings.isEnableSafeBuilder()) {
      writer.println();
      writer.tab(2).println("private Builder() {");
      writer.tab(2).println("}");
    }

    writer.println();
    pojo.getMembers()
        .forEach(
            member -> {
              final String type = member.getTypeName(resolver);
              final String fieldName = member.memberName(resolver);
              writer.tab(2).println("private %s %s;", type, fieldName);
            });

    pojo.getMembers()
        .forEach(
            member -> {
              final String type = member.getTypeName(resolver);
              final String fieldName = member.memberName(resolver);
              final String setterModifier =
                  settings.isEnableSafeBuilder() && member.isRequired() ? "private" : "public";
              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "%s Builder %s(%s %s) {",
                      setterModifier, member.setterName(resolver), type, fieldName);
              writer.tab(3).println("this.%s = %s;", fieldName, fieldName);
              writer.tab(3).println("return this;");
              writer.tab(2).println("}");
            });

    writer.println();
    writer.tab(2).println("public %s build() {", pojo.className(resolver));
    writer
        .tab(3)
        .println("return new %s(%s);", pojo.className(resolver), createNamesCommaSeparated(pojo));
    writer.tab(2).println("}");

    writer.tab(1).println("}");
  }

  protected void printSafeBuilder(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(1).println("public static Builder0 newBuilder() {");
    writer.tab(2).println("return new Builder0(new Builder());");
    writer.tab(1).println("}");

    final PList<PojoMember> optionalMembers = pojo.getMembers().filter(PojoMember::isNullable);
    final PList<PojoMember> requiredMembers = pojo.getMembers().filter(PojoMember::isRequired);

    IntStream.range(0, requiredMembers.size())
        .forEach(
            idx -> {
              final PojoMember member = requiredMembers.apply(idx);
              final String memberName = member.memberName(resolver);
              final String memberType = member.getTypeName(resolver);
              writer.println();
              writer.tab(1).println("public static final class Builder%d {", idx);

              writer.tab(2).println("private final Builder builder;");
              writer.tab(2).println("private Builder%d(Builder builder) {", idx);
              writer.tab(3).println("this.builder = builder;");
              writer.tab(2).println("}");

              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "public Builder%d %s(%s %s){",
                      idx + 1, member.setterName(resolver), memberType, memberName);
              writer
                  .tab(3)
                  .println(
                      "return new Builder%d(builder.%s(%s));",
                      idx + 1, member.setterName(resolver), memberName);
              writer.tab(2).println("}");

              writer.tab(1).println("}");
            });

    // Builder after all required members have been set
    writer.println();
    writer.tab(1).println("public static final class Builder%d {", requiredMembers.size());
    writer.tab(2).println("private final Builder builder;");
    writer.tab(2).println("private Builder%d(Builder builder) {", requiredMembers.size());
    writer.tab(3).println("this.builder = builder;");
    writer.tab(2).println("}");
    writer.tab(2).println("public OptBuilder0 andAllOptionals(){");
    writer.tab(3).println("return new OptBuilder0(builder);");
    writer.tab(2).println("}");
    writer.tab(2).println("public Builder andOptionals(){");
    writer.tab(3).println("return builder;");
    writer.tab(2).println("}");
    writer.tab(2).println("public %s build(){", pojo.className(resolver));
    writer.tab(3).println("return builder.build();");
    writer.tab(2).println("}");
    writer.tab(1).println("}");

    IntStream.range(0, optionalMembers.size())
        .forEach(
            idx -> {
              final PojoMember member = optionalMembers.apply(idx);
              final String memberName = member.memberName(resolver);
              final String memberType = member.getTypeName(resolver);
              writer.println();
              writer.tab(1).println("public static final class OptBuilder%d {", idx);

              writer.tab(2).println("private final Builder builder;");
              writer.tab(2).println("private OptBuilder%d(Builder builder) {", idx);
              writer.tab(3).println("this.builder = builder;");
              writer.tab(2).println("}");

              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "public OptBuilder%d %s(%s %s){",
                      idx + 1, member.setterName(resolver), memberType, memberName);
              writer
                  .tab(3)
                  .println(
                      "return new OptBuilder%d(builder.%s(%s));",
                      idx + 1, member.setterName(resolver), memberName);
              writer.tab(2).println("}");

              writer.tab(1).println("}");
            });

    // Final Builder
    writer.println();
    writer.tab(1).println("public static final class OptBuilder%d {", optionalMembers.size());
    writer.tab(2).println("private final Builder builder;");
    writer.tab(2).println("private OptBuilder%d(Builder builder) {", optionalMembers.size());
    writer.tab(3).println("this.builder = builder;");
    writer.tab(2).println("}");
    writer.tab(2).println("public %s build(){", pojo.className(resolver));
    writer.tab(3).println("return builder.build();");
    writer.tab(2).println("}");
    writer.tab(1).println("}");
  }

  protected void printEqualsAndHash(Writer writer, Pojo pojo) {

    writer.println();

    writer.tab(1).println("@Override");
    writer.tab(1).println("public boolean equals(Object other) {");
    writer.tab(2).println("if (this == other) {");
    writer.tab(3).println("return true;");
    writer.tab(2).println("}");
    writer.tab(2).println("if (other == null || getClass() != other.getClass()) {");
    writer.tab(3).println("return false;");
    writer.tab(2).println("}");

    writer
        .tab(2)
        .println("final %s v = (%s) other;", pojo.className(resolver), pojo.className(resolver));

    final PList<String> objectEquals =
        pojo.getMembers()
            .map(
                member -> {
                  final String fieldName = member.memberName(resolver);
                  return String.format("Objects.equals(%s, v.%s)", fieldName, fieldName);
                });

    writer.tab(2).print("return ");
    for (int i = 0; i < objectEquals.size(); i++) {
      if (i > 0) {
        writer.tab(4).print("&& ");
      }
      writer.tab(4).print(objectEquals.apply(i));
      if (i == objectEquals.size() - 1) {
        writer.println(";");
      } else {
        writer.println();
      }
    }
    writer.tab(1).println("}");
    writer.println();

    writer.println();
    writer.tab(1).println("@Override");
    writer.tab(1).println("public int hashCode() {");

    final PList<String> fieldNames = pojo.getMembers().map(member -> member.memberName(resolver));

    writer.tab(2).println("return Objects.hash(%s);", String.join(", ", fieldNames));
    writer.tab(1).println("}");
  }

  protected void printToString(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(1).println("@Override");
    writer.tab(1).println("public String toString() {");
    writer.tab(2).println("return \"%s{\" +", pojo.className(resolver));

    String separator = "";
    for (PojoMember member : pojo.getMembers()) {
      writer
          .tab(3)
          .println(
              "\"%s%s=\" + %s +",
              separator, member.memberName(resolver), member.memberName(resolver));
      separator = ", ";
    }
    writer.tab(3).println("\"}\";");
    writer.tab(1).println("}");
  }

  private void printClassEnd(Writer writer) {
    writer.println("}");
  }
}
