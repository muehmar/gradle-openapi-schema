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
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.model.EnumMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGen;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.ConstructorGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.Value;

public class EnumGenerator implements Generator<EnumGenerator.EnumContent, PojoSettings> {

  private final ClassGen<EnumContent, PojoSettings> delegate;

  private EnumGenerator(ClassGen.Declaration declaration) {
    delegate =
        ClassGenBuilder.<EnumContent, PojoSettings>create()
            .enum_()
            .declaration(declaration)
            .packageGen(new PackageGenerator<>())
            .javaDoc(
                JavaDocGenerator.<PojoSettings>javaDoc().contraMap(EnumContent::getDescription))
            .noAnnotations()
            .modifiers(PUBLIC)
            .className(enumPojo -> enumPojo.getClassName().asString())
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
  public Writer generate(EnumContent data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<EnumContent, PojoSettings> content() {
    return Generator.<EnumContent, PojoSettings>emptyGen()
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

  private Writer printEnumMembers(EnumContent content, PojoSettings settings, Writer writer) {
    final PList<EnumConstantName> members = content.getMembers();

    return EnumMember.extractDescriptions(
            members, settings.getEnumDescriptionSettings(), content.getDescription())
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

  private Writer printClassMembers(EnumContent content, PojoSettings settings, Writer writer) {
    return writer
        .println("private final String value;")
        .println("private final String description;");
  }

  private Generator<EnumContent, PojoSettings> printConstructor() {
    return ConstructorGenBuilder.<EnumContent, PojoSettings>create()
        .modifiers()
        .className(content -> content.getClassName().asString())
        .arguments(ignore -> PList.of("String value", "String description"))
        .content(w -> w.println("this.value = value;").println("this.description = description;"))
        .build();
  }

  private <T> Generator<T, PojoSettings> printValueGetter() {
    return MethodGenBuilder.<T, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("String")
        .methodName("getValue")
        .noArguments()
        .content("return value;")
        .build();
  }

  private <T> Generator<T, PojoSettings> printDescriptionGetter() {
    final MethodGen<T, PojoSettings> methodGen =
        MethodGenBuilder.<T, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType("String")
            .methodName("getDescription")
            .noArguments()
            .content("return description;")
            .build();
    return Generator.<T, PojoSettings>newLine()
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(methodGen)
        .filter((data, settings) -> settings.getEnumDescriptionSettings().isEnabled());
  }

  private <T> Generator<T, PojoSettings> printToString() {
    return MethodGenBuilder.<T, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("String")
        .methodName("toString")
        .noArguments()
        .content("return value;")
        .build();
  }

  private Generator<EnumContent, PojoSettings> printFromValue() {
    return MethodGenBuilder.<EnumContent, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType(javaEnumPojo -> javaEnumPojo.getClassName().asString())
        .methodName("fromValue")
        .singleArgument(javaEnumPojo -> "String value")
        .content(this::fromValueContent)
        .build();
  }

  private Writer fromValueContent(EnumContent content, PojoSettings settings, Writer writer) {
    final JavaIdentifier enumName = content.getClassName();
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
        .ref(JavaRefs.JAVA_UTIL_STREAM_COLLECTORS)
        .ref(JavaRefs.JAVA_UTIL_STREAM_STREAM);
  }

  @Value
  @PojoBuilder(builderName = "EnumContentBuilder")
  public static class EnumContent {
    JavaIdentifier className;
    String description;
    PList<EnumConstantName> members;
  }
}
