package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.EnumMember;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.FieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.getter.GetterGenerator;
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
      printClassStart(writer, pojo, pojoSettings);
      printFields(writer, pojo, pojoSettings);
      printConstructor(writer, pojo, pojoSettings);

      printEnums(writer, pojo, pojoSettings);

      printGetters(writer, pojo, pojoSettings);
      printWithers(writer, pojo);

      printEqualsAndHash(writer, pojo, pojoSettings);
      printToString(writer, pojo, pojoSettings);

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
      writer.println("import com.fasterxml.jackson.databind.annotation.JsonDeserialize;");
      writer.println("import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;");
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

  private void printClassStart(Writer writer, Pojo pojo, PojoSettings settings) {
    writer.println();
    printJavaDoc(writer, 0, pojo.getDescription());
    if (settings.isJacksonJson()) {
      writer.println("@JsonDeserialize(builder = %s.Builder.class)", pojo.className(resolver));
    }
    writer.tab(0).println("public class %s {", pojo.className(resolver).asString());
  }

  private void printFields(Writer writer, Pojo pojo, PojoSettings settings) {
    final Generator<Pojo, PojoSettings> fieldsGen = FieldsGenerator.fields();
    final Generator<Pojo, PojoSettings> indentedFieldsGen =
        Generator.<Pojo, PojoSettings>emptyGen().append(fieldsGen, 1);
    final String output = applyGen(indentedFieldsGen, pojo, settings);
    writer.println(output);
  }

  private void printConstructor(Writer writer, Pojo pojo, PojoSettings settings) {
    final Generator<Pojo, PojoSettings> generator = PojoConstructorGenerator.generator();

    writer.println();
    final String output =
        applyGen(Generator.<Pojo, PojoSettings>emptyGen().append(generator, 1), pojo, settings);
    writer.println(output);
  }

  private String createNamesCommaSeparated(Pojo pojo) {
    final PList<String> formattedPairs =
        pojo.getMembers()
            .flatMap(
                member -> {
                  final Name memberName = member.memberName(resolver);
                  if (member.isRequiredAndNullable()) {
                    final String requiredNullableFlagName =
                        String.format("is%sPresent", memberName.startUpperCase());
                    return PList.of(memberName.asString(), requiredNullableFlagName);
                  } else if (member.isOptionalAndNullable()) {
                    final String optionalNullableFlagName =
                        String.format("is%sNull", memberName.startUpperCase());
                    return PList.of(memberName.asString(), optionalNullableFlagName);
                  } else {
                    return PList.single(memberName.asString());
                  }
                });

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
    final Generator<PojoMember, PojoSettings> generator =
        Generator.<PojoMember, PojoSettings>emptyGen()
            .append(GetterGenerator.generator(), 1)
            .prependNewLine();
    pojo.getMembers().map(member -> applyGen(generator, member, settings)).forEach(writer::println);
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

  protected void printEqualsAndHash(Writer writer, Pojo pojo, PojoSettings settings) {
    final Generator<Pojo, PojoSettings> equalsMethod = EqualsGenerator.equalsMethod();
    final Generator<Pojo, PojoSettings> hashCodeMethod = HashCodeGenerator.hashCodeMethod();
    final Generator<Pojo, PojoSettings> equalsAndHashCodeMethods =
        equalsMethod.appendNewLine().append(hashCodeMethod);
    final Generator<Pojo, PojoSettings> generator =
        Generator.<Pojo, PojoSettings>emptyGen()
            .appendNewLine()
            .append(equalsAndHashCodeMethods, 1);

    final String output = applyGen(generator, pojo, settings);
    writer.println(output);
  }

  protected void printToString(Writer writer, Pojo pojo, PojoSettings settings) {
    final Generator<Pojo, PojoSettings> generator = ToStringGenerator.toStringMethod();

    writer.println();
    final String output =
        applyGen(Generator.<Pojo, PojoSettings>emptyGen().append(generator, 1), pojo, settings);
    writer.println(output);
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
