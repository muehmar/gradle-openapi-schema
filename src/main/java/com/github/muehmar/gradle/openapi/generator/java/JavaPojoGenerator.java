package com.github.muehmar.gradle.openapi.generator.java;

import com.github.muehmar.gradle.openapi.generator.Pojo;
import com.github.muehmar.gradle.openapi.generator.PojoMember;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.Writer;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JavaPojoGenerator {
  private final PojoSettings pojoSettings;
  private final OpenAPI openAPI;
  private final Function<String, Writer> createWriter;
  private final Resolver resolver;

  public JavaPojoGenerator(
      PojoSettings pojoSettings, OpenAPI openAPI, Function<String, Writer> createWriter) {
    this.pojoSettings = pojoSettings;
    this.openAPI = openAPI;
    this.createWriter = createWriter;
    this.resolver = new JavaResolver();
  }

  public void generate(String directory) {
    final Components components = openAPI.getComponents();
    final Map<String, Schema> schemas = components.getSchemas();

    schemas
        .entrySet()
        .forEach(
            schema -> {
              if (schema == null) {
                return;
              }

              final Pojo pojo =
                  JavaPojo.fromSchema(pojoSettings, schema.getKey(), schema.getValue());
              final String packagePath = pojoSettings.getPackageName().replaceAll("\\.", "/");

              final Writer writer =
                  createWriter.apply(
                      directory + "/" + packagePath + "/" + pojo.className(resolver) + ".java");

              printPackage(writer, pojoSettings.getPackageName());
              printImports(writer, pojo, pojoSettings.getJsonSupport());
              printClassStart(writer, pojo);

              printFields(writer, pojo, pojoSettings.getJsonSupport());
              printConstructor(writer, pojo, pojoSettings.getJsonSupport());

              printGetters(writer, pojo, pojoSettings.getJsonSupport());
              printWithers(writer, pojo);
              printToString(writer, pojo);
              printEqualsAndHash(writer, pojo);
              printBuilder(writer, pojo);

              printClassEnd(writer);

              writer.close();
            });
  }

  private void printPackage(Writer writer, String packageName) {
    writer.println("package %s;", packageName);
  }

  private void printImports(Writer writer, Pojo pojo, JsonSupport jsonSupport) {
    writer.println();
    writer.println("import java.util.Objects;");
    writer.println("import java.util.Optional;");
    writer.println();

    if (jsonSupport.equals(JsonSupport.JACKSON)) {
      writer.println("import com.fasterxml.jackson.annotation.JsonIgnore;");
      writer.println("import com.fasterxml.jackson.annotation.JsonProperty;");
      writer.println("import com.fasterxml.jackson.annotation.JsonCreator;");
      writer.println("import com.fasterxml.jackson.annotation.JsonValue;");
      writer.println();
    }

    pojo.getMembers().stream()
        .flatMap(member -> member.getImports().stream())
        .distinct()
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

  private void printFields(Writer writer, Pojo pojo, JsonSupport jsonSupport) {
    if (pojo.isArray() && jsonSupport.equals(JsonSupport.JACKSON)) {
      writer.tab(1).println("@JsonValue");
    }

    pojo.getMembers()
        .forEach(
            member ->
                writer
                    .tab(1)
                    .println(
                        "private final %s %s;", member.getTypeName(), member.memberName(resolver)));
  }

  private void printConstructor(Writer writer, Pojo pojo, JsonSupport jsonSupport) {
    writer.println();
    if (jsonSupport.equals(JsonSupport.JACKSON)) {
      if (pojo.isArray()) {
        writer.tab(1).println("@JsonCreator");
      } else {
        writer.tab(1).println("@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)");
      }
    }
    writer.tab(1).println("public %s(", pojo.className(resolver));

    final List<String> memberArguments = createMemberArguments(pojo, jsonSupport);

    for (int i = 0; i < memberArguments.size(); i++) {
      writer.tab(3).print(memberArguments.get(i));
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

  private List<String> createMemberArguments(Pojo pojo, JsonSupport jsonSupport) {
    final Function<PojoMember, String> createJsonSupport =
        member -> {
          if (jsonSupport.equals(JsonSupport.JACKSON)) {
            return String.format("@JsonProperty(\"%s\") ", member.memberName(resolver));
          } else {
            return "";
          }
        };
    return pojo.getMembers().stream()
        .map(
            member -> {
              if (pojo.isArray()) {
                return String.format("%s %s", member.getTypeName(), member.memberName(resolver));
              } else {
                return String.format(
                    "%s%s %s",
                    createJsonSupport.apply(member),
                    member.getTypeName(),
                    member.memberName(resolver));
              }
            })
        .collect(Collectors.toList());
  }

  private String createNamesCommaSeparated(Pojo pojo) {
    final List<String> formattedPairs =
        pojo.getMembers().stream()
            .map(member -> String.format("%s", member.memberName(resolver)))
            .collect(Collectors.toList());

    return String.join(", ", formattedPairs);
  }

  private void printClassEnd(Writer writer) {
    writer.println("}");
  }

  protected void printGetters(Writer writer, Pojo pojo, JsonSupport jsonSupport) {
    pojo.getMembers()
        .forEach(
            member -> {
              writer.println();
              printJavaDoc(writer, 1, member.getDescription());

              final boolean nullable = member.isNullable();

              final String returnType =
                  nullable
                      ? String.format("Optional<%s>", member.getTypeName())
                      : member.getTypeName();
              final String field =
                  nullable
                      ? String.format("Optional.ofNullable(this.%s)", member.memberName(resolver))
                      : String.format("this.%s", member.memberName(resolver));

              if (nullable && jsonSupport.equals(JsonSupport.JACKSON)) {
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
                if (jsonSupport.equals(JsonSupport.JACKSON)) {
                  writer.tab(1).println("@JsonProperty(\"%s\")", member.memberName(resolver));
                }
                writer
                    .tab(1)
                    .println(
                        "public %s %sNullable() {",
                        member.getTypeName(), member.getterName(resolver));
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
                      member.getTypeName(),
                      member.memberName(resolver));
              writer
                  .tab(2)
                  .println(
                      "return new %s(%s);",
                      pojo.className(resolver), createNamesCommaSeparated(pojo));
              writer.tab(1).println("}");
            });
  }

  protected void printBuilder(Writer writer, Pojo pojo) {

    writer.println();
    writer.tab(1).println("public static Builder newBuilder() {");
    writer.tab(2).println("return new Builder();");
    writer.tab(1).println("}");

    writer.println();
    writer.tab(1).println("public static final class Builder {");
    pojo.getMembers()
        .forEach(
            member -> {
              final String type = member.getTypeName();
              final String fieldName = member.memberName(resolver);
              writer.tab(2).println("private %s %s;", type, fieldName);
            });

    pojo.getMembers()
        .forEach(
            member -> {
              final String type = member.getTypeName();
              final String fieldName = member.memberName(resolver);
              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "public Builder %s(%s %s) {", member.setterName(resolver), type, fieldName);
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

    final List<String> objectEquals =
        pojo.getMembers().stream()
            .map(
                member -> {
                  final String fieldName = member.memberName(resolver);
                  return String.format("Objects.equals(%s, v.%s)", fieldName, fieldName);
                })
            .collect(Collectors.toList());

    writer.tab(2).print("return ");
    for (int i = 0; i < objectEquals.size(); i++) {
      if (i > 0) {
        writer.tab(4).print("&& ");
      }
      writer.tab(4).print(objectEquals.get(i));
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

    final List<String> fieldNames =
        pojo.getMembers().stream()
            .map(member -> member.memberName(resolver))
            .collect(Collectors.toList());

    writer.tab(2).println("return Objects.hash(%s);", String.join(", ", fieldNames));
    writer.tab(1).println("}");
  }

  protected void printToString(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(1).println("@Override");
    writer.tab(1).println("public String toString() {");
    writer.tab(2).println("return \"%s{\" + ", pojo.className(resolver));

    String separator = "";
    for (PojoMember member : pojo.getMembers()) {
      writer
          .tab(3)
          .println(
              "\"%s%s=\" + %s + ",
              separator, member.memberName(resolver), member.memberName(resolver));
      separator = ", ";
    }
    writer.tab(3).println("\"}\";");
    writer.tab(1).println("}");
  }
}
