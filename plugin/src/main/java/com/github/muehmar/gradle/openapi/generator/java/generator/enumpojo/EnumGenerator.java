package com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo;

import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.EnumMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGen;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.ConstructorGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class EnumGenerator implements Generator<JavaEnumPojo, PojoSettings> {

  private final ClassGen<JavaEnumPojo, PojoSettings> delegate;

  private EnumGenerator(ClassGen.Declaration declaration) {
    delegate =
        ClassGenBuilder.<JavaEnumPojo, PojoSettings>create()
            .enum_()
            .declaration(declaration)
            .packageGen(new PackageGenerator<>())
            .javaDoc(
                JavaDocGenerator.<PojoSettings>javaDoc().contraMap(JavaEnumPojo::getDescription))
            .modifiers(PUBLIC)
            .className(enumPojo -> enumPojo.getName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
  }

  public static EnumGenerator topLevel() {
    return new EnumGenerator(ClassGen.Declaration.TOP_LEVEL);
  }

  public static EnumGenerator nested() {
    return new EnumGenerator(ClassGen.Declaration.NESTED);
  }

  @Override
  public Writer generate(JavaEnumPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaEnumPojo, PojoSettings> content() {
    return Generator.<JavaEnumPojo, PojoSettings>emptyGen()
        .append(this::printEnumMembers)
        .appendNewLine()
        .append(this::printClassMembers)
        .appendNewLine()
        .append(printConstructor())
        .appendNewLine()
        .append(JacksonAnnotationGenerator.jsonValue())
        .append(printValueGetter())
        .append(printDescriptionGetter())
        .appendNewLine()
        .append(AnnotationGenerator.override())
        .append(printToString())
        .appendNewLine()
        .append(JacksonAnnotationGenerator.jsonCreator())
        .append(printFromValue());
  }

  private Writer printEnumMembers(JavaEnumPojo enumPojo, PojoSettings settings, Writer writer) {
    final PList<EnumConstantName> members = enumPojo.getMembers();

    return EnumMember.extractDescriptions(
            members, settings.getEnumDescriptionSettings(), enumPojo.getDescription())
        .zipWithIndex()
        .foldLeft(
            writer,
            (w, p) -> {
              final EnumMember enumMember = p.first();
              final EnumConstantName enumConstantName = enumMember.getName();
              final int index = p.second();
              final String separator = index == members.size() - 1 ? ";" : ",";
              return w.println(
                  "%s(\"%s\", \"%s\")%s",
                  enumConstantName.asJavaConstant(),
                  enumConstantName.getOriginalConstant(),
                  enumMember.getDescription(),
                  separator);
            });
  }

  private Writer printClassMembers(JavaEnumPojo enumPojo, PojoSettings settings, Writer writer) {
    return writer
        .println("private final String value;")
        .println("private final String description;");
  }

  private Generator<JavaEnumPojo, PojoSettings> printConstructor() {
    return ConstructorGenBuilder.<JavaEnumPojo, PojoSettings>create()
        .modifiers()
        .className(javaEnumPojo -> javaEnumPojo.getName().asString())
        .arguments(ignore -> PList.of("String value", "String description"))
        .content(w -> w.println("this.value = value;").println("this.description = description;"))
        .build();
  }

  private Generator<JavaEnumPojo, PojoSettings> printValueGetter() {
    return MethodGenBuilder.<JavaEnumPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("String")
        .methodName("getValue")
        .noArguments()
        .content("return value;")
        .build();
  }

  private Generator<JavaEnumPojo, PojoSettings> printDescriptionGetter() {
    final MethodGen<JavaEnumPojo, PojoSettings> methodGen =
        MethodGenBuilder.<JavaEnumPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("getDescription")
            .noArguments()
            .content("return description;")
            .build();
    return Generator.<JavaEnumPojo, PojoSettings>newLine()
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(methodGen)
        .filter((pojo, settings) -> settings.getEnumDescriptionSettings().isEnabled());
  }

  private Generator<JavaEnumPojo, PojoSettings> printToString() {
    return MethodGenBuilder.<JavaEnumPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("String")
        .methodName("toString")
        .noArguments()
        .content("return value;")
        .build();
  }

  private Generator<JavaEnumPojo, PojoSettings> printFromValue() {
    return MethodGenBuilder.<JavaEnumPojo, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType(javaEnumPojo -> javaEnumPojo.getName().asString())
        .methodName("fromValue")
        .singleArgument(javaEnumPojo -> "String value")
        .content(this::fromValueContent)
        .build();
  }

  private Writer fromValueContent(JavaEnumPojo javaEnumPojo, PojoSettings settings, Writer writer) {
    final String enumName = javaEnumPojo.getName().asString();
    return writer
        .println("for (%s e: %s.values()) {", enumName, enumName)
        .tab(1)
        .println("if (e.value.equals(value)) {")
        .tab(2)
        .println("return e;")
        .tab(1)
        .println("}")
        .println("}")
        .println("final String possibleValues =")
        .tab(1)
        .println(
            "Stream.of(values()).map(%s::getValue).collect(Collectors.joining(\", \"));", enumName)
        .println("throw new IllegalArgumentException(")
        .tab(1)
        .println("\"Unexpected value '\"")
        .tab(2)
        .println("+ value")
        .tab(2)
        .println("+ \"' for %s, possible values are [\"", enumName)
        .tab(2)
        .println("+ possibleValues")
        .tab(2)
        .println("+ \"]\");")
        .ref(JavaRefs.JAVA_UTIL_STREAM_COLLECTOR)
        .ref(JavaRefs.JAVA_UTIL_STREAM_STREAM);
  }
}
