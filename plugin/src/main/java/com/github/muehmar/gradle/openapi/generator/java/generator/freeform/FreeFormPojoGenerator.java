package com.github.muehmar.gradle.openapi.generator.java.generator.freeform;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.FieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGeneratorFactory;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaFreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class FreeFormPojoGenerator implements Generator<JavaFreeFormPojo, PojoSettings> {
  private final Generator<JavaFreeFormPojo, PojoSettings> delegate;

  public FreeFormPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaFreeFormPojo, PojoSettings>create()
            .clazz()
            .topLevel()
            .packageGen(new PackageGenerator<>())
            .javaDoc(
                JavaDocGenerator.<PojoSettings>javaDoc()
                    .contraMap(JavaFreeFormPojo::getDescription))
            .noAnnotations()
            .modifiers(PUBLIC, FINAL)
            .className(pojo -> pojo.getName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .andAllOptionals()
            .build();
  }

  @Override
  public Writer generate(JavaFreeFormPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private static Generator<JavaFreeFormPojo, PojoSettings> content() {
    return JacksonAnnotationGenerator.<JavaFreeFormPojo>jsonValue()
        .append(FieldsGenerator.singleField(), JavaFreeFormPojo::getMember)
        .appendNewLine()
        .append(JacksonAnnotationGenerator.jsonCreator())
        .append(PojoConstructorGenerator.generator())
        .appendNewLine()
        .append(ValidationGenerator.validAnnotation(), JavaFreeFormPojo::getMember)
        .append(GetterGeneratorFactory.create(), JavaFreeFormPojo::getMember)
        .appendNewLine()
        .append(getPropertyMethod())
        .appendNewLine()
        .append(EqualsGenerator.equalsMethod())
        .appendNewLine()
        .append(HashCodeGenerator.hashCodeMethod());
  }

  private static Generator<JavaFreeFormPojo, PojoSettings> getPropertyMethod() {
    return MethodGenBuilder.<JavaFreeFormPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(pojo -> String.format("Optional<%s>", pojo.getValueType().getFullClassName()))
        .methodName("getProperty")
        .arguments(ignore -> PList.single("String propertyName"))
        .content(
            pojo ->
                String.format(
                    "return Optional.ofNullable(%s.get(propertyName));",
                    pojo.getMember().getName()))
        .build()
        .append(RefsGenerator.fieldRefs(), JavaFreeFormPojo::getMember)
        .append(RefsGenerator.optionalRef());
  }
}
