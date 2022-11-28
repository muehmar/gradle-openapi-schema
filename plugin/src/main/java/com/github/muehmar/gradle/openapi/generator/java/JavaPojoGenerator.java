package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewEqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewFieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewHashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewPojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.NormalBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.Writer;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class JavaPojoGenerator implements PojoGenerator {
  private final Supplier<Writer> createWriter;

  public JavaPojoGenerator(Supplier<Writer> createWriter) {
    this.createWriter = createWriter;
  }

  @Override
  public void generatePojo(Pojo pojo, PojoSettings pojoSettings) {
    final JavaPojo javaPojo = JavaPojo.wrap(pojo, pojoSettings.getTypeMappings());
    generatePojo(javaPojo, pojoSettings);
  }

  public void generatePojo(JavaPojo pojo, PojoSettings pojoSettings) {
    final String packagePath =
        pojoSettings.getPackageName().replace(".", "/").replaceFirst("^/", "");

    final Writer writer = createWriter.get();

    pojo.fold(
        arrayPojo -> generateArrayPojo(arrayPojo, writer, pojoSettings),
        enumPojo -> generateEnumPojo(enumPojo, writer, pojoSettings),
        objectPojo -> generateObjectPojo(objectPojo, writer, pojoSettings));

    writer.close(packagePath + "/" + pojo.getName() + ".java");
  }

  private Void generateObjectPojo(JavaObjectPojo pojo, Writer writer, PojoSettings pojoSettings) {
    printPackage(writer, pojoSettings.getPackageName());
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
      printSafeBuilder(writer, pojo, pojoSettings);
    }
    printClassEnd(writer);
    return null;
  }

  private Void generateEnumPojo(JavaEnumPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final EnumGenerator generator = EnumGenerator.topLevel();
    final String output = applyGen(generator, pojo, pojoSettings);
    writer.println(output);
    return null;
  }

  private Void generateArrayPojo(JavaArrayPojo pojo, Writer writer, PojoSettings pojoSettings) {
    printPackage(writer, pojoSettings.getPackageName());
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
    final Generator<JavaEnumPojo, PojoSettings> generator =
        Generator.<JavaEnumPojo, PojoSettings>emptyGen()
            .appendNewLine()
            .append(EnumGenerator.nested(), 1);
    pojo.getMembersOrEmpty()
        .forEach(
            member ->
                member
                    .getJavaType()
                    .fold(
                        ignore -> null,
                        ignore -> null,
                        javaEnumType -> {
                          final JavaEnumPojo javaEnumPojo =
                              JavaEnumPojo.of(
                                  PojoName.ofName(member.getJavaType().getClassName()),
                                  member.getDescription(),
                                  javaEnumType.getMembers());
                          final String output = applyGen(generator, javaEnumPojo, settings);
                          writer.println(output);
                          return null;
                        },
                        ignore -> null,
                        ignore -> null,
                        ignore -> null,
                        ignore -> null,
                        ignore -> null,
                        ignore -> null));
  }

  protected void printGetters(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojoMember, PojoSettings> generator =
        Generator.<JavaPojoMember, PojoSettings>emptyGen()
            .append(GetterGeneratorFactory.create(), 1)
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

  protected void printBuilder(Writer writer, JavaObjectPojo pojo, PojoSettings settings) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        Generator.<JavaObjectPojo, PojoSettings>emptyGen()
            .append(new NormalBuilderGenerator(), 1)
            .prependNewLine();
    final String output = applyGen(generator, pojo, settings);
    writer.println(output);
  }

  protected void printSafeBuilder(Writer writer, JavaPojo pojo, PojoSettings settings) {
    writer.println();
    writer.tab(1).println("public static Builder0 newBuilder() {");
    writer.tab(2).println("return new Builder0(new Builder());");
    writer.tab(1).println("}");

    final PList<JavaPojoMember> optionalMembers =
        pojo.getMembersOrEmpty().filter(JavaPojoMember::isOptional);
    final PList<JavaPojoMember> requiredMembers =
        pojo.getMembersOrEmpty().filter(JavaPojoMember::isRequired);

    final String builderMethodPrefix = settings.getBuilderMethodPrefix();
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
                      idx + 1,
                      member.prefixedMethodName(builderMethodPrefix),
                      memberType,
                      memberName);
              writer
                  .tab(3)
                  .println(
                      "return new Builder%d(builder.%s(%s));",
                      idx + 1, member.prefixedMethodName(builderMethodPrefix), memberName);
              writer.tab(2).println("}");

              // Required nullable method
              if (member.isRequiredAndNullable()) {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "public Builder%d %s(Optional<%s> %s){",
                        idx + 1,
                        member.prefixedMethodName(builderMethodPrefix),
                        memberType,
                        memberName);
                writer
                    .tab(3)
                    .println(
                        "return new Builder%d(builder.%s(%s));",
                        idx + 1, member.prefixedMethodName(builderMethodPrefix), memberName);
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
                      idx + 1,
                      member.prefixedMethodName(builderMethodPrefix),
                      memberType,
                      memberName);
              writer
                  .tab(3)
                  .println(
                      "return new OptBuilder%d(builder.%s(%s));",
                      idx + 1, member.prefixedMethodName(builderMethodPrefix), memberName);
              writer.tab(2).println("}");

              if (member.isNotNullable()) {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "public OptBuilder%d %s(Optional<%s> %s){",
                        idx + 1,
                        member.prefixedMethodName(builderMethodPrefix),
                        memberType,
                        memberName);
                writer
                    .tab(3)
                    .println(
                        "return new OptBuilder%d(%s.map(builder::%s).orElse(builder));",
                        idx + 1, memberName, member.prefixedMethodName(builderMethodPrefix));
                writer.tab(2).println("}");
              } else {
                writer.println();
                printJavaDoc(writer, 2, member.getDescription());
                writer
                    .tab(2)
                    .println(
                        "public OptBuilder%d %s(Tristate<%s> %s){",
                        idx + 1,
                        member.prefixedMethodName(builderMethodPrefix),
                        memberType,
                        memberName);
                writer
                    .tab(3)
                    .println(
                        "return new OptBuilder%d(builder.%s(%s));",
                        idx + 1, member.prefixedMethodName(builderMethodPrefix), memberName);
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
