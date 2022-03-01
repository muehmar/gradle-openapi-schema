package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.data.EnumMember;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.Writer;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;
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

    final boolean isEnum = pojo.isEnum();

    printPackage(writer, pojoSettings.getPackageName());

    if (isEnum) {
      printJsonSupportImports(writer, pojoSettings);
      printStreamImports(writer);
      printEnum(
          writer,
          pojo.getName().append(pojoSettings.getSuffix()),
          pojo.getDescription(),
          pojo.getEnumType().orElseThrow(IllegalStateException::new).getEnumMembers(),
          pojoSettings,
          0);
    } else {
      printImports(writer, pojo, pojoSettings);
      printClassStart(writer, pojo);
      printFields(writer, pojo, pojoSettings);
      printConstructor(writer, pojo, pojoSettings);

      printEnums(writer, pojo, pojoSettings);

      printGetters(writer, pojo, pojoSettings);
      printWithers(writer, pojo);

      printEqualsAndHash(writer, pojo);
      printToString(writer, pojo);

      printBuilder(writer, pojo, pojoSettings);
      if (pojoSettings.isEnableSafeBuilder()) {
        printSafeBuilder(writer, pojo);
      }
      printClassEnd(writer);
    }

    writer.close(packagePath + "/" + pojo.className(resolver).asString() + ".java");
  }

  private void printPackage(Writer writer, String packageName) {
    writer.println("package %s;", packageName);
  }

  private void printImports(Writer writer, Pojo pojo, PojoSettings settings) {
    printJavaUtilImports(writer);

    printJsonSupportImports(writer, settings);
    printStreamImports(writer);

    printValidationImports(writer, settings);

    pojo.getMembers()
        .flatMap(PojoMember::getImports)
        .distinct(Function.identity())
        .forEach(classImport -> writer.println("import %s;", classImport));

    writer.println();
  }

  private void printJavaUtilImports(Writer writer) {
    writer.println();
    writer.println("import java.util.Objects;");
    writer.println("import java.util.Optional;");
  }

  private void printStreamImports(Writer writer) {
    writer.println();
    writer.println("import java.util.stream.Collectors;");
    writer.println("import java.util.stream.Stream;");
  }

  private void printJsonSupportImports(Writer writer, PojoSettings settings) {
    if (settings.isJacksonJson()) {
      writer.println();
      writer.println("import com.fasterxml.jackson.annotation.JsonIgnore;");
      writer.println("import com.fasterxml.jackson.annotation.JsonProperty;");
      writer.println("import com.fasterxml.jackson.annotation.JsonCreator;");
      writer.println("import com.fasterxml.jackson.annotation.JsonValue;");
    }
  }

  private void printValidationImports(Writer writer, PojoSettings settings) {
    if (settings.isEnableConstraints()) {
      writer.println();
      writer.println("import javax.validation.Valid;");
      writer.println("import javax.validation.constraints.Max;");
      writer.println("import javax.validation.constraints.Min;");
      writer.println("import javax.validation.constraints.Pattern;");
      writer.println("import javax.validation.constraints.Size;");
      writer.println("import javax.validation.constraints.NotNull;");
      writer.println("import javax.validation.constraints.Email;");
    }
  }

  private void printJavaDoc(Writer writer, int tabs, String javadoc) {
    final Generator<String, Void> gen =
        Generator.<String, Void>emptyGen().append(JavaDocGenerator.javaDoc(), tabs);
    final String output = applyGen(gen, javadoc);
    writer.println(output);
  }

  private void printClassStart(Writer writer, Pojo pojo) {
    writer.println();
    printJavaDoc(writer, 0, pojo.getDescription());
    writer.tab(0).println("public class %s {", pojo.className(resolver).asString());
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
                        member.getTypeName(resolver).asString(),
                        member.memberName(resolver).asString()));
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
    writer.tab(1).println("public %s(", pojo.className(resolver).asString());

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
                        "this.%s = %s;",
                        member.memberName(resolver).asString(),
                        member.memberName(resolver).asString()));

    writer.tab(1).println("}");
  }

  private PList<String> createMemberArguments(Pojo pojo, PojoSettings settings) {
    final Function<PojoMember, String> createJsonSupport =
        member -> {
          if (settings.isJacksonJson()) {
            return String.format("@JsonProperty(\"%s\") ", member.memberName(resolver).asString());
          } else {
            return "";
          }
        };
    return pojo.getMembers()
        .map(
            member -> {
              if (pojo.isArray()) {
                return String.format(
                    "%s %s",
                    member.getTypeName(resolver).asString(),
                    member.memberName(resolver).asString());
              } else {
                return String.format(
                    "%s%s %s",
                    createJsonSupport.apply(member),
                    member.getTypeName(resolver).asString(),
                    member.memberName(resolver).asString());
              }
            });
  }

  private String createNamesCommaSeparated(Pojo pojo) {
    final PList<String> formattedPairs =
        pojo.getMembers()
            .map(member -> String.format("%s", member.memberName(resolver).asString()));

    return String.join(", ", formattedPairs);
  }

  protected void printEnums(Writer writer, Pojo pojo, PojoSettings settings) {
    pojo.getMembers()
        .forEach(
            member ->
                member.onEnum(
                    enumMembers ->
                        printEnum(
                            writer,
                            member.getTypeName(resolver),
                            member.getDescription(),
                            enumMembers,
                            settings,
                            1)));
  }

  protected void printEnum(
      Writer writer,
      Name enumName,
      String description,
      PList<String> enumMembers,
      PojoSettings settings,
      int indention) {
    writer.println();
    printJavaDoc(writer, indention, description);

    final String enumNameString = enumName.asString();
    writer.tab(indention).println("public enum %s {", enumNameString);
    EnumMember.extractDescriptions(
            enumMembers.map(Name::of), settings.getEnumDescriptionSettings(), description)
        .zipWithIndex()
        .forEach(
            p -> {
              final EnumMember anEnumMember = p.first();
              final Name memberName = anEnumMember.getName();
              final Integer idx = p.second();
              writer
                  .tab(indention + 1)
                  .print(
                      "%s(\"%s\", \"%s\")",
                      resolver.enumMemberName(memberName).asString(),
                      memberName.asString(),
                      anEnumMember.getDescription());
              if (idx + 1 < enumMembers.size()) {
                writer.println(",");
              } else {
                writer.println(";");
              }
            });
    writer.println();
    writer.tab(indention + 1).println("private final String value;");
    writer.tab(indention + 1).println("private final String description;");
    writer.println();
    writer.tab(indention + 1).println("%s(String value, String description) {", enumNameString);
    writer.tab(indention + 2).println("this.value = value;");
    writer.tab(indention + 2).println("this.description = description;");
    writer.tab(indention + 1).println("}");

    writer.println();
    if (settings.isJacksonJson()) {
      writer.tab(indention + 1).println("@JsonValue");
    }
    writer.tab(indention + 1).println("public String getValue() {");
    writer.tab(indention + 2).println("return value;");
    writer.tab(indention + 1).println("}");

    if (settings.getEnumDescriptionSettings().isEnabled()) {
      writer.println();
      if (settings.isJacksonJson()) {
        writer.tab(indention + 1).println("@JsonIgnore");
      }
      writer.tab(indention + 1).println("public String getDescription() {");
      writer.tab(indention + 2).println("return description;");
      writer.tab(indention + 1).println("}");
    }

    writer.println();
    writer.tab(indention + 1).println("@Override");
    writer.tab(indention + 1).println("public String toString() {");
    writer.tab(indention + 2).println("return String.valueOf(value);");
    writer.tab(indention + 1).println("}");

    writer.println();
    if (settings.isJacksonJson()) {
      writer.tab(indention + 1).println("@JsonCreator");
    }
    writer.tab(indention + 1).println("public static %s fromValue(String value) {", enumNameString);
    writer.tab(indention + 2).println("for (%s e : %s.values()) {", enumNameString, enumNameString);
    writer.tab(indention + 3).println("if (e.value.equals(value)) {");
    writer.tab(indention + 4).println("return e;");
    writer.tab(indention + 3).println("}");
    writer.tab(indention + 2).println("}");
    writer
        .tab(indention + 2)
        .println("final String possibleValues =")
        .tab(indention + 3)
        .println(
            "Stream.of(values()).map(%s::getValue).collect(Collectors.joining(\", \"));",
            enumNameString)
        .tab(indention + 2)
        .println("throw new IllegalArgumentException(")
        .tab(indention + 3)
        .println("\"Unexpected value '\"")
        .tab(indention + 4)
        .println("+ value")
        .tab(indention + 4)
        .println("+ \"' for %s, possible values are [\"", enumNameString)
        .tab(indention + 4)
        .println("+ possibleValues")
        .tab(indention + 4)
        .println("+ \"]\");");
    writer.tab(indention + 1).println("}");

    writer.tab(indention).println("}");
  }

  protected void printGetters(Writer writer, Pojo pojo, PojoSettings settings) {
    pojo.getMembers()
        .forEach(
            member -> {
              printMainGetter(writer, member, settings);
              printNullableGetter(writer, member, settings);
            });
  }

  /**
   * Prints either a normal getter in case the member is required or wrap it in an {@link Optional}
   * if its not required.
   */
  protected void printMainGetter(Writer writer, PojoMember member, PojoSettings settings) {
    writer.println();
    printJavaDoc(writer, 1, member.getDescription());

    final boolean optional = member.isOptional();

    final String returnType =
        optional
            ? String.format("Optional<%s>", member.getTypeName(resolver).asString())
            : member.getTypeName(resolver).asString();
    final String field =
        optional
            ? String.format("Optional.ofNullable(this.%s)", member.memberName(resolver).asString())
            : String.format("this.%s", member.memberName(resolver).asString());

    if (optional && settings.isJacksonJson()) {
      writer.tab(1).println("@JsonIgnore");
    }
    if (member.isRequired()) {
      printConstraints(writer, member, 1, settings);
    }

    writer
        .tab(1)
        .println(
            "public %s %s%s() {",
            returnType, member.getterName(resolver).asString(), optional ? "Optional" : "");
    writer.tab(2).println("return %s;", field);
    writer.tab(1).println("}");
  }

  /**
   * Prints a 'nullable' getter in case the member is not required. This getter is suffixed with
   * 'Nullable' and may return null if the value is not present.
   */
  protected void printNullableGetter(Writer writer, PojoMember member, PojoSettings settings) {
    if (member.isOptional()) {
      writer.println();
      printJavaDoc(writer, 1, member.getDescription());
      printConstraints(writer, member, 1, settings);
      if (settings.isJacksonJson()) {
        writer.tab(1).println("@JsonProperty(\"%s\")", member.memberName(resolver).asString());
      }
      writer
          .tab(1)
          .println(
              "public %s %sNullable() {",
              member.getTypeName(resolver).asString(), member.getterName(resolver).asString());
      writer.tab(2).println("return %s;", member.memberName(resolver).asString());
      writer.tab(1).println("}");
    }
  }

  protected void printConstraints(
      Writer writer, PojoMember member, int tabs, PojoSettings settings) {
    if (settings.isEnableConstraints()) {

      if (member.getType().containsPojo()) {
        writer.tab(tabs).println("@Valid");
      }

      if (member.isRequired()) {
        writer.tab(tabs).println("@NotNull");
      }

      final Constraints constraints = member.getType().getConstraints();
      constraints.onEmail(email -> writer.tab(tabs).println("@Email"));
      constraints.onMin(min -> writer.tab(tabs).println("@Min(value = %d)", min.getValue()));
      constraints.onMax(max -> writer.tab(tabs).println("@Max(value = %d)", max.getValue()));
      constraints.onDecimalMin(
          decMin ->
              writer
                  .tab(tabs)
                  .println(
                      "@DecimalMin(value = \"%s\", inclusive = %b)",
                      decMin.getValue(), decMin.isInclusiveMin()));
      constraints.onDecimalMax(
          decMax ->
              writer
                  .tab(tabs)
                  .println(
                      "@DecimalMax(value = \"%s\", inclusive = %b)",
                      decMax.getValue(), decMax.isInclusiveMax()));
      constraints.onSize(
          size -> {
            final String minMax =
                PList.of(
                        size.getMin().map(min -> String.format("min = %d", min)),
                        size.getMax().map(max -> String.format("max = %d", max)))
                    .flatMapOptional(Function.identity())
                    .mkString(", ");
            writer.tab(tabs).println("@Size(%s)", minMax);
          });
      constraints.onPattern(
          pattern ->
              writer
                  .tab(tabs)
                  .println(
                      "@Pattern(regexp=\"%s\")", pattern.getPatternEscaped(JavaEscaper::escape)));
    }
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
                      pojo.className(resolver).asString(),
                      member.witherName(resolver).asString(),
                      member.getTypeName(resolver).asString(),
                      member.memberName(resolver).asString());
              writer
                  .tab(2)
                  .println(
                      "return new %s(%s);",
                      pojo.className(resolver).asString(), createNamesCommaSeparated(pojo));
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
              final String type = member.getTypeName(resolver).asString();
              final String fieldName = member.memberName(resolver).asString();
              writer.tab(2).println("private %s %s;", type, fieldName);
            });

    pojo.getMembers()
        .forEach(
            member -> {
              final String type = member.getTypeName(resolver).asString();
              final String fieldName = member.memberName(resolver).asString();
              final String setterModifier =
                  settings.isEnableSafeBuilder() && member.isRequired() ? "private" : "public";

              // Normal setter
              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "%s Builder %s(%s %s) {",
                      setterModifier, member.setterName(resolver).asString(), type, fieldName);
              writer.tab(3).println("this.%s = %s;", fieldName, fieldName);
              writer.tab(3).println("return this;");
              writer.tab(2).println("}");

              // Optional setter
              if (member.isOptional()) {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "%s Builder %s(Optional<%s> %s) {",
                        setterModifier, member.setterName(resolver).asString(), type, fieldName);
                writer.tab(3).println("this.%s = %s.orElse(null);", fieldName, fieldName);
                writer.tab(3).println("return this;");
                writer.tab(2).println("}");
              }
            });

    writer.println();
    writer.tab(2).println("public %s build() {", pojo.className(resolver).asString());
    writer
        .tab(3)
        .println(
            "return new %s(%s);",
            pojo.className(resolver).asString(), createNamesCommaSeparated(pojo));
    writer.tab(2).println("}");

    writer.tab(1).println("}");
  }

  protected void printSafeBuilder(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(1).println("public static Builder0 newBuilder() {");
    writer.tab(2).println("return new Builder0(new Builder());");
    writer.tab(1).println("}");

    final PList<PojoMember> optionalMembers = pojo.getMembers().filter(PojoMember::isOptional);
    final PList<PojoMember> requiredMembers = pojo.getMembers().filter(PojoMember::isRequired);

    IntStream.range(0, requiredMembers.size())
        .forEach(
            idx -> {
              final PojoMember member = requiredMembers.apply(idx);
              final String memberName = member.memberName(resolver).asString();
              final String memberType = member.getTypeName(resolver).asString();
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
                      idx + 1, member.setterName(resolver).asString(), memberType, memberName);
              writer
                  .tab(3)
                  .println(
                      "return new Builder%d(builder.%s(%s));",
                      idx + 1, member.setterName(resolver).asString(), memberName);
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
    writer.tab(2).println("public %s build(){", pojo.className(resolver).asString());
    writer.tab(3).println("return builder.build();");
    writer.tab(2).println("}");
    writer.tab(1).println("}");

    IntStream.range(0, optionalMembers.size())
        .forEach(
            idx -> {
              final PojoMember member = optionalMembers.apply(idx);
              final String memberName = member.memberName(resolver).asString();
              final String memberType = member.getTypeName(resolver).asString();
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
                      idx + 1, member.setterName(resolver).asString(), memberType, memberName);
              writer
                  .tab(3)
                  .println(
                      "return new OptBuilder%d(builder.%s(%s));",
                      idx + 1, member.setterName(resolver).asString(), memberName);
              writer.tab(2).println("}");

              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "public OptBuilder%d %s(Optional<%s> %s){",
                      idx + 1, member.setterName(resolver).asString(), memberType, memberName);
              writer
                  .tab(3)
                  .println(
                      "return new OptBuilder%d(%s.map(builder::%s).orElse(builder));",
                      idx + 1, memberName, member.setterName(resolver).asString());
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
    writer.tab(2).println("public %s build(){", pojo.className(resolver).asString());
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
        .println(
            "final %s v = (%s) other;",
            pojo.className(resolver).asString(), pojo.className(resolver).asString());

    final PList<String> objectEquals =
        pojo.getMembers()
            .map(
                member -> {
                  final String fieldName = member.memberName(resolver).asString();
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

    final PList<String> fieldNames =
        pojo.getMembers().map(member -> member.memberName(resolver).asString());

    writer.tab(2).println("return Objects.hash(%s);", String.join(", ", fieldNames));
    writer.tab(1).println("}");
  }

  protected void printToString(Writer writer, Pojo pojo) {
    writer.println();
    writer.tab(1).println("@Override");
    writer.tab(1).println("public String toString() {");
    writer.tab(2).println("return \"%s{\" +", pojo.className(resolver).asString());

    String separator = "";
    for (PojoMember member : pojo.getMembers()) {
      writer
          .tab(3)
          .println(
              "\"%s%s=\" + %s +",
              separator,
              member.memberName(resolver).asString(),
              member.memberName(resolver).asString());
      separator = ", ";
    }
    writer.tab(3).println("\"}\";");
    writer.tab(1).println("}");
  }

  private void printClassEnd(Writer writer) {
    writer.println("}");
  }

  private static <T, S> String applyGen(Generator<T, S> gen, T data, S settings) {
    return gen.generate(
            data, settings, io.github.muehmar.codegenerator.writer.Writer.createDefault())
        .asString();
  }

  private static <T> String applyGen(Generator<T, Void> gen, T data) {
    final Void noSettings = null;
    return applyGen(gen, data, noSettings);
  }
}
