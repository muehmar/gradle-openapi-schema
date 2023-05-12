package com.github.muehmar.gradle.openapi.generator.java.generator.map;

import static com.github.muehmar.gradle.openapi.generator.java.generator.map.MapPropertyCountMethodGenerator.propertyCountMethod;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.FieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaMapPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class MapPojoGenerator implements Generator<JavaMapPojo, PojoSettings> {
  private final Generator<JavaMapPojo, PojoSettings> delegate;

  public MapPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaMapPojo, PojoSettings>create()
            .clazz()
            .topLevel()
            .packageGen(new PackageGenerator<>())
            .javaDoc(
                JavaDocGenerator.<PojoSettings>javaDoc().contraMap(JavaMapPojo::getDescription))
            .noAnnotations()
            .modifiers(PUBLIC, FINAL)
            .className(pojo -> pojo.getClassName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .andAllOptionals()
            .build();
  }

  @Override
  public Writer generate(JavaMapPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private static Generator<JavaMapPojo, PojoSettings> content() {
    return JacksonAnnotationGenerator.<JavaMapPojo>jsonValue()
        .append(FieldsGenerator.singleField(), JavaMapPojo::getMember)
        .appendNewLine()
        .append(JacksonAnnotationGenerator.jsonCreator())
        .append(PojoConstructorGenerator.generator())
        .appendNewLine()
        .append(GetterGenerator.generator(), JavaMapPojo::getMember)
        .appendNewLine()
        .append(getPropertyMethod())
        .appendNewLine()
        .append(propertyCountMethod())
        .appendNewLine()
        .append(EqualsGenerator.equalsMethod())
        .appendNewLine()
        .append(HashCodeGenerator.hashCodeMethod())
        .appendNewLine()
        .append(ToStringGenerator.toStringMethod());
  }

  private static Generator<JavaMapPojo, PojoSettings> getPropertyMethod() {
    return MethodGenBuilder.<JavaMapPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(pojo -> String.format("Optional<%s>", pojo.getValueType().getFullClassName()))
        .methodName("getProperty")
        .arguments(ignore -> PList.single("String propertyName"))
        .content(
            pojo ->
                String.format(
                    "return Optional.ofNullable(%s.get(propertyName));",
                    pojo.getMember().getNameAsIdentifier()))
        .build()
        .append(RefsGenerator.fieldRefs(), JavaMapPojo::getMember)
        .append(RefsGenerator.optionalRef());
  }
}
