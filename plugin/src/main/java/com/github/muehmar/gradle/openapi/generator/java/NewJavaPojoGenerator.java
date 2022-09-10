package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.NewPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.NewEqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.NewFieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.NewHashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.NewPojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.NewToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.getter.NewGetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.EnumMember;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.Writer;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class NewJavaPojoGenerator implements NewPojoGenerator {
  private final Supplier<Writer> createWriter;

  public NewJavaPojoGenerator(Supplier<Writer> createWriter) {
    this.createWriter = createWriter;
  }

  @Override
  public void generatePojo(NewPojo pojo, PojoSettings pojoSettings) {
    final JavaPojo javaPojo = JavaPojo.wrap(pojo, pojoSettings.getTypeMappings());
    generatePojo(javaPojo, pojoSettings);
  }

  public void generatePojo(JavaPojo pojo, PojoSettings pojoSettings) {
    final String packagePath =
        pojoSettings.getPackageName().replace(".", "/").replaceFirst("^/", "");

    final Writer writer = createWriter.get();

    printPackage(writer, pojoSettings.getPackageName());

    pojo.fold(
        arrayPojo -> generateArrayPojo(arrayPojo, writer, pojoSettings),
        enumPojo -> generateEnumPojo(enumPojo, writer, pojoSettings),
        objectPojo -> generateObjectPojo(objectPojo, writer, pojoSettings));

    writer.close(packagePath + "/" + pojo.getName() + ".java");
  }

  private Void generateObjectPojo(JavaObjectPojo pojo, Writer writer, PojoSettings pojoSettings) {
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
    return null;
  }

  private Void generateEnumPojo(JavaEnumPojo pojo, Writer writer, PojoSettings pojoSettings) {
    printJsonSupportImports(writer, pojoSettings);
    printStreamImports(writer);
    printEnum(writer, pojo.getName(), pojo.getDescription(), pojo.getMembers(), pojoSettings, 0);
    return null;
  }

  private Void generateArrayPojo(JavaArrayPojo pojo, Writer writer, PojoSettings pojoSettings) {
    printImports(writer, pojo, pojoSettings);
    printClassStart(writer, pojo, pojoSettings);
    printFields(writer, pojo, pojoSettings);
    printConstructor(writer, pojo, pojoSettings);

    printEnums(writer, pojo, pojoSettings);

    printGetters(writer, pojo, pojoSettings);
    printWithers(writer, pojo);

    printEqualsAndHash(writer, pojo, pojoSettings);
    printToString(writer, pojo, pojoSettings);
    printClassEnd(writer);
    return null;
  }

  private void printPackage(Writer writer, String packageName) {
    writer.println("package %s;", packageName);
  }

  private void printImports(Writer writer, JavaPojo pojo, PojoSettings settings) {
    printOpenApiUtilImports(writer, settings);

    printJavaUtilImports(writer);

    printJsonSupportImports(writer, settings);
    printStreamImports(writer);

    printValidationImports(writer, settings);

    pojo.getMembersOrEmpty()
        .map(JavaPojoMember::getJavaType)
        .flatMap(JavaType::getImportsAsString)
        .distinct(Function.identity())
        .forEach(classImport -> writer.println("import %s;", classImport));

    writer.println();
  }

  private void printOpenApiUtilImports(Writer writer, PojoSettings settings) {
    writer.println();
    if (settings.isJacksonJson()) {
      writer.println("import com.github.muehmar.openapi.util.JacksonNullContainer;");
    }
    writer.println("import com.github.muehmar.openapi.util.Tristate;");
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
      writer.println("import com.fasterxml.jackson.annotation.JsonInclude;");
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
      writer.println("import javax.validation.constraints.AssertTrue;");
    }
  }

  private void printJavaDoc(Writer writer, int tabs, String javadoc) {
    final Generator<String, Void> gen =
        Generator.<String, Void>emptyGen().append(JavaDocGenerator.javaDoc(), tabs);
    final String output = applyGen(gen, javadoc);
    writer.println(output);
  }

  private void printClassStart(Writer writer, JavaPojo pojo, PojoSettings settings) {
    writer.println();
    printJavaDoc(writer, 0, pojo.getDescription());
    if (settings.isJacksonJson() && not(pojo.isArray())) {
      writer.println("@JsonDeserialize(builder = %s.Builder.class)", pojo.getName());
    }
    writer.tab(0).println("public class %s {", pojo.getName());
  }

  private void printFields(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojo, PojoSettings> fieldsGen = NewFieldsGenerator.fields();
    final Generator<JavaPojo, PojoSettings> indentedFieldsGen =
        Generator.<JavaPojo, PojoSettings>emptyGen().append(fieldsGen, 1);
    final String output = applyGen(indentedFieldsGen, pojo, settings);
    writer.println(output);
  }

  private void printConstructor(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojo, PojoSettings> generator = NewPojoConstructorGenerator.generator();

    writer.println();

    if (settings.isJacksonJson() && pojo.isArray()) {
      writer.tab(1).println("@JsonCreator");
    }

    final String output =
        applyGen(Generator.<JavaPojo, PojoSettings>emptyGen().append(generator, 1), pojo, settings);
    writer.println(output);
  }

  private String createNamesCommaSeparated(JavaPojo pojo) {
    final PList<String> formattedPairs =
        pojo.getMembersOrEmpty()
            .flatMap(
                member -> {
                  final Name memberName = member.getName();
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

  protected void printEnums(Writer writer, JavaPojo pojo, PojoSettings settings) {
    pojo.getMembersOrEmpty()
        .forEach(
            member ->
                member
                    .getJavaType()
                    .fold(
                        ignore -> null,
                        ignore -> null,
                        javaEnumType -> {
                          printEnum(
                              writer,
                              PojoName.ofName(member.getJavaType().getClassName()),
                              member.getDescription(),
                              javaEnumType.getMembers(),
                              settings,
                              1);
                          return null;
                        },
                        ignore -> null,
                        ignore -> null,
                        ignore -> null,
                        ignore -> null,
                        ignore -> null));
  }

  protected void printEnum(
      Writer writer,
      PojoName enumName,
      String description,
      PList<String> enumMembers,
      PojoSettings settings,
      int indention) {
    writer.println();
    printJavaDoc(writer, indention, description);

    final String enumNameString = enumName.asString();
    writer.tab(indention).println("public enum %s {", enumNameString);
    // FIXME: Enable proper description extraction again with the JavaEnumMemberName type
    EnumMember.extractDescriptions(
            enumMembers.map(Name::ofString), settings.getEnumDescriptionSettings(), description)
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
                      memberName.asString(), memberName.asString(), anEnumMember.getDescription());
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

  protected void printGetters(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(NewGetterGenerator.generator(), 1)
            .prependNewLine();
    pojo.getMembersOrEmpty()
        .map(member -> applyGen(generator, member, settings))
        .forEach(writer::println);
  }

  protected void printWithers(Writer writer, JavaPojo pojo) {
    pojo.getMembersOrEmpty()
        .forEach(
            member -> {
              writer.println();
              printJavaDoc(writer, 1, member.getDescription());
              writer
                  .tab(1)
                  .println(
                      "public %s %s(%s %s) {",
                      pojo.getName(),
                      member.getWitherName(),
                      member.getJavaType().getFullClassName(),
                      member.getName());
              writer
                  .tab(2)
                  .println("return new %s(%s);", pojo.getName(), createNamesCommaSeparated(pojo));
              writer.tab(1).println("}");
            });
  }

  protected void printBuilder(Writer writer, JavaPojo pojo, PojoSettings settings) {
    if (settings.isDisableSafeBuilder()) {
      writer.println();
      writer.tab(1).println("public static Builder newBuilder() {");
      writer.tab(2).println("return new Builder();");
      writer.tab(1).println("}");
    }

    writer.println();
    if (settings.isJacksonJson()) {
      writer.tab(1).println("@JsonPOJOBuilder(withPrefix = \"set\")");
    }
    writer.tab(1).println("public static final class Builder {");

    if (settings.isEnableSafeBuilder()) {
      writer.println();
      writer.tab(2).println("private Builder() {");
      writer.tab(2).println("}");
    }

    writer.println();
    pojo.getMembersOrEmpty()
        .forEach(
            member -> {
              final String type = member.getJavaType().getFullClassName().asString();
              final Name fieldName = member.getName();
              writer.tab(2).println("private %s %s;", type, fieldName);
              if (member.isRequiredAndNullable()) {
                writer
                    .tab(2)
                    .println("private boolean is%sPresent = false;", fieldName.startUpperCase());
              } else if (member.isOptionalAndNullable()) {
                writer
                    .tab(2)
                    .println("private boolean is%sNull = false;", fieldName.startUpperCase());
              }
            });

    pojo.getMembersOrEmpty()
        .forEach(
            member -> {
              final String type = member.getJavaType().getFullClassName().asString();
              final Name fieldName = member.getName();
              final String setterModifier =
                  settings.isEnableSafeBuilder() && member.isRequired() ? "private" : "public";

              // Normal setter
              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              if (settings.isJacksonJson()) {
                writer.tab(2).println("@JsonProperty(\"%s\")", member.getName());
              }
              writer
                  .tab(2)
                  .println(
                      "%s Builder %s(%s %s) {",
                      setterModifier, member.getSetterName(), type, fieldName);
              writer.tab(3).println("this.%s = %s;", fieldName, fieldName);
              if (member.isRequiredAndNullable()) {
                writer.tab(3).println("this.is%sPresent = true;", fieldName.startUpperCase());
              } else if (member.isOptionalAndNullable()) {
                writer.tab(3).println("if (%s == null) {", fieldName);
                writer.tab(4).println("this.is%sNull = true;", fieldName.startUpperCase());
                writer.tab(3).println("}");
              }
              writer.tab(3).println("return this;");
              writer.tab(2).println("}");

              // Optional setter
              if ((member.isOptional() && !member.isNullable()) || member.isRequiredAndNullable()) {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "%s Builder %s(Optional<%s> %s) {",
                        setterModifier, member.getSetterName(), type, fieldName);
                writer.tab(3).println("this.%s = %s.orElse(null);", fieldName, fieldName);
                if (member.isOptionalAndNullable()) {
                  writer.tab(3).println("if (!%s.isPresent()) {", fieldName);
                  writer.tab(4).println("this.is%sNull = true;", fieldName.startUpperCase());
                  writer.tab(3).println("}");
                } else if (member.isRequiredAndNullable()) {
                  writer.tab(3).println("this.is%sPresent = true;", fieldName.startUpperCase());
                }
                writer.tab(3).println("return this;");
                writer.tab(2).println("}");
              }

              // Optional nullable setter
              if (member.isOptionalAndNullable()) {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "%s Builder %s(Tristate<%s> %s) {",
                        setterModifier, member.getSetterName(), type, fieldName);
                writer
                    .tab(3)
                    .println(
                        "this.%s = %s.onValue(val -> val).onNull(() -> null).onAbsent(() -> null);",
                        fieldName, fieldName);
                writer
                    .tab(3)
                    .println(
                        "this.is%sNull = %s.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);",
                        fieldName.startUpperCase(), fieldName);
                writer.tab(3).println("return this;");
                writer.tab(2).println("}");
              }
            });

    writer.println();
    writer.tab(2).println("public %s build() {", pojo.getName());
    writer.tab(3).println("return new %s(%s);", pojo.getName(), createNamesCommaSeparated(pojo));
    writer.tab(2).println("}");

    writer.tab(1).println("}");
  }

  protected void printSafeBuilder(Writer writer, JavaPojo pojo) {
    writer.println();
    writer.tab(1).println("public static Builder0 newBuilder() {");
    writer.tab(2).println("return new Builder0(new Builder());");
    writer.tab(1).println("}");

    final PList<JavaPojoMember> optionalMembers =
        pojo.getMembersOrEmpty().filter(JavaPojoMember::isOptional);
    final PList<JavaPojoMember> requiredMembers =
        pojo.getMembersOrEmpty().filter(JavaPojoMember::isRequired);

    IntStream.range(0, requiredMembers.size())
        .forEach(
            idx -> {
              final JavaPojoMember member = requiredMembers.apply(idx);
              final String memberName = member.getName().asString();
              final String memberType = member.getJavaType().getFullClassName().asString();
              writer.println();
              writer.tab(1).println("public static final class Builder%d {", idx);

              writer.tab(2).println("private final Builder builder;");
              writer.tab(2).println("private Builder%d(Builder builder) {", idx);
              writer.tab(3).println("this.builder = builder;");
              writer.tab(2).println("}");

              // Normal setter method
              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "public Builder%d %s(%s %s){",
                      idx + 1, member.getSetterName(), memberType, memberName);
              writer
                  .tab(3)
                  .println(
                      "return new Builder%d(builder.%s(%s));",
                      idx + 1, member.getSetterName(), memberName);
              writer.tab(2).println("}");

              // Required nullable method
              if (member.isRequiredAndNullable()) {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "public Builder%d %s(Optional<%s> %s){",
                        idx + 1, member.getSetterName(), memberType, memberName);
                writer
                    .tab(3)
                    .println(
                        "return new Builder%d(builder.%s(%s));",
                        idx + 1, member.getSetterName(), memberName);
                writer.tab(2).println("}");
              }

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
    writer.tab(2).println("public %s build(){", pojo.getName());
    writer.tab(3).println("return builder.build();");
    writer.tab(2).println("}");
    writer.tab(1).println("}");

    IntStream.range(0, optionalMembers.size())
        .forEach(
            idx -> {
              final JavaPojoMember member = optionalMembers.apply(idx);
              final String memberName = member.getName().asString();
              final String memberType = member.getJavaType().getFullClassName().asString();
              writer.println();
              writer.tab(1).println("public static final class OptBuilder%d {", idx);

              writer.tab(2).println("private final Builder builder;");
              writer.tab(2).println("private OptBuilder%d(Builder builder) {", idx);
              writer.tab(3).println("this.builder = builder;");
              writer.tab(2).println("}");

              // Normal setter
              writer.println();
              printJavaDoc(writer, 2, member.getDescription());
              writer
                  .tab(2)
                  .println(
                      "public OptBuilder%d %s(%s %s){",
                      idx + 1, member.getSetterName(), memberType, memberName);
              writer
                  .tab(3)
                  .println(
                      "return new OptBuilder%d(builder.%s(%s));",
                      idx + 1, member.getSetterName(), memberName);
              writer.tab(2).println("}");

              if (member.isNotNullable()) {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "public OptBuilder%d %s(Optional<%s> %s){",
                        idx + 1, member.getSetterName(), memberType, memberName);
                writer
                    .tab(3)
                    .println(
                        "return new OptBuilder%d(%s.map(builder::%s).orElse(builder));",
                        idx + 1, memberName, member.getSetterName());
                writer.tab(2).println("}");
              } else {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "public OptBuilder%d %s(Tristate<%s> %s){",
                        idx + 1, member.getSetterName(), memberType, memberName);
                writer
                    .tab(3)
                    .println(
                        "return new OptBuilder%d(builder.%s(%s));",
                        idx + 1, member.getSetterName(), memberName);
                writer.tab(2).println("}");
              }

              writer.tab(1).println("}");
            });

    // Final Builder
    writer.println();
    writer.tab(1).println("public static final class OptBuilder%d {", optionalMembers.size());
    writer.tab(2).println("private final Builder builder;");
    writer.tab(2).println("private OptBuilder%d(Builder builder) {", optionalMembers.size());
    writer.tab(3).println("this.builder = builder;");
    writer.tab(2).println("}");
    writer.tab(2).println("public %s build(){", pojo.getName());
    writer.tab(3).println("return builder.build();");
    writer.tab(2).println("}");
    writer.tab(1).println("}");
  }

  protected void printEqualsAndHash(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojo, PojoSettings> equalsMethod = NewEqualsGenerator.equalsMethod();
    final Generator<JavaPojo, PojoSettings> hashCodeMethod = NewHashCodeGenerator.hashCodeMethod();
    final Generator<JavaPojo, PojoSettings> equalsAndHashCodeMethods =
        equalsMethod.appendNewLine().append(hashCodeMethod);
    final Generator<JavaPojo, PojoSettings> generator =
        Generator.<JavaPojo, PojoSettings>emptyGen()
            .appendNewLine()
            .append(equalsAndHashCodeMethods, 1);

    final String output = applyGen(generator, pojo, settings);
    writer.println(output);
  }

  protected void printToString(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojo, PojoSettings> generator = NewToStringGenerator.toStringMethod();

    writer.println();
    final String output =
        applyGen(Generator.<JavaPojo, PojoSettings>emptyGen().append(generator, 1), pojo, settings);
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
