package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.settings.ValidationApi.JAKARTA_2_0;
import static com.github.muehmar.gradle.openapi.generator.settings.ValidationApi.JAKARTA_3_0;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ComposedPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.freeform.FreeFormPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.FieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.PojoPropertyCountMethod;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SafeBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaFreeFormPojo;
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
            objectPojo -> generateObjectPojo(objectPojo, writer, pojoSettings),
            composedPojo -> generateComposedPojo(composedPojo, writer, pojoSettings),
            freeFormPojo -> generateFreeFormPojo(freeFormPojo, writer, pojoSettings))
        .close(packagePath + "/" + pojo.getName() + ".java");
  }

  private Writer generateFreeFormPojo(
      JavaFreeFormPojo freeFormPojo, Writer writer, PojoSettings pojoSettings) {
    final FreeFormPojoGenerator freeFormPojoGenerator = new FreeFormPojoGenerator();
    final String output = applyGen(freeFormPojoGenerator, freeFormPojo, pojoSettings);
    return writer.print(output);
  }

  private Writer generateComposedPojo(
      JavaComposedPojo composedPojo, Writer writer, PojoSettings pojoSettings) {
    final ComposedPojoGenerator generator = new ComposedPojoGenerator();
    final String output = applyGen(generator, composedPojo, pojoSettings);
    writer.println(output);
    return writer;
  }

  private Writer generateObjectPojo(JavaObjectPojo pojo, Writer writer, PojoSettings pojoSettings) {
    printPackage(writer, pojoSettings.getPackageName());
    printImports(writer, pojo, pojoSettings);
    printClassStart(writer, pojo, pojoSettings);
    printFields(writer, pojo, pojoSettings);
    printConstructor(writer, pojo, pojoSettings);

    printEnums(writer, pojo, pojoSettings);

    printGetters(writer, pojo, pojoSettings);
    printWithers(writer, pojo);

    printPropertyCount(writer, pojo, pojoSettings);

    printEqualsAndHash(writer, pojo, pojoSettings);
    printToString(writer, pojo, pojoSettings);

    printBuilder(writer, pojo, pojoSettings);
    if (pojoSettings.isEnableSafeBuilder()) {
      printSafeBuilder(writer, pojo, pojoSettings);
    }
    printClassEnd(writer);
    return writer;
  }

  private Writer generateEnumPojo(JavaEnumPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final EnumGenerator generator = EnumGenerator.topLevel();
    final String output = applyGen(generator, pojo, pojoSettings);
    writer.println(output);
    return writer;
  }

  private Writer generateArrayPojo(JavaArrayPojo pojo, Writer writer, PojoSettings pojoSettings) {
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
    return writer;
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
    if (settings.isEnableValidation() && settings.getValidationApi().equals(JAKARTA_2_0)) {
      writer.println();
      writer.println("import %s;", Jakarta2ValidationRefs.VALID);
      writer.println("import %s;", Jakarta2ValidationRefs.DECIMAL_MAX);
      writer.println("import %s;", Jakarta2ValidationRefs.DECIMAL_MIN);
      writer.println("import %s;", Jakarta2ValidationRefs.MAX);
      writer.println("import %s;", Jakarta2ValidationRefs.MIN);
      writer.println("import %s;", Jakarta2ValidationRefs.PATTERN);
      writer.println("import %s;", Jakarta2ValidationRefs.SIZE);
      writer.println("import %s;", Jakarta2ValidationRefs.NOT_NULL);
      writer.println("import %s;", Jakarta2ValidationRefs.EMAIL);
      writer.println("import %s;", Jakarta2ValidationRefs.ASSERT_TRUE);
    } else if (settings.isEnableValidation() && settings.getValidationApi().equals(JAKARTA_3_0)) {
      writer.println();
      writer.println("import %s;", Jakarta3ValidationRefs.VALID);
      writer.println("import %s;", Jakarta3ValidationRefs.DECIMAL_MAX);
      writer.println("import %s;", Jakarta3ValidationRefs.DECIMAL_MIN);
      writer.println("import %s;", Jakarta3ValidationRefs.MAX);
      writer.println("import %s;", Jakarta3ValidationRefs.MIN);
      writer.println("import %s;", Jakarta3ValidationRefs.PATTERN);
      writer.println("import %s;", Jakarta3ValidationRefs.SIZE);
      writer.println("import %s;", Jakarta3ValidationRefs.NOT_NULL);
      writer.println("import %s;", Jakarta3ValidationRefs.EMAIL);
      writer.println("import %s;", Jakarta3ValidationRefs.ASSERT_TRUE);
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
    final Generator<JavaPojo, PojoSettings> fieldsGen = FieldsGenerator.fields();
    final Generator<JavaPojo, PojoSettings> indentedFieldsGen =
        Generator.<JavaPojo, PojoSettings>emptyGen().append(fieldsGen, 1);
    final String output = applyGen(indentedFieldsGen, pojo, settings);
    writer.println(output);
  }

  private void printConstructor(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojo, PojoSettings> generator = PojoConstructorGenerator.generator();

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

  private void printPropertyCount(Writer writer, JavaObjectPojo pojo, PojoSettings settings) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        PojoPropertyCountMethod.propertyCountMethod().indent(1);
    final String output = applyGen(generator.prependNewLine(), pojo, settings);
    writer.println(output);
  }

  protected void printBuilder(Writer writer, JavaObjectPojo pojo, PojoSettings settings) {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        Generator.<JavaObjectPojo, PojoSettings>emptyGen()
            .append(new NormalBuilderGenerator(), 1)
            .prependNewLine();
    final String output = applyGen(generator, pojo, settings);
    writer.println(output);
  }

  protected void printSafeBuilder(Writer writer, JavaObjectPojo pojo, PojoSettings settings) {
    final Generator<JavaObjectPojo, PojoSettings> safeBuilderGenerator =
        new SafeBuilderGenerator().indent(1);
    final String output = applyGen(safeBuilderGenerator, pojo, settings);
    writer.println(output);
  }

  protected void printEqualsAndHash(Writer writer, JavaPojo pojo, PojoSettings settings) {
    final Generator<JavaPojo, PojoSettings> equalsMethod = EqualsGenerator.equalsMethod();
    final Generator<JavaPojo, PojoSettings> hashCodeMethod = HashCodeGenerator.hashCodeMethod();
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
    final Generator<JavaPojo, PojoSettings> generator = ToStringGenerator.toStringMethod();

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
