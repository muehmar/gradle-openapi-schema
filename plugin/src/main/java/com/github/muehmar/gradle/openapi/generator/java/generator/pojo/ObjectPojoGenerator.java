package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.UniqueItemsValidationMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SafeBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class ObjectPojoGenerator implements Generator<JavaObjectPojo, PojoSettings> {
  private final Generator<JavaObjectPojo, PojoSettings> delegate;

  public ObjectPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaObjectPojo, PojoSettings>create()
            .clazz()
            .declaration(TOP_LEVEL)
            .packageGen(new PackageGenerator<>())
            .javaDoc(JavaDocGenerator.javaDoc((pojo, settings) -> pojo.getDescription()))
            .singleAnnotation(JacksonAnnotationGenerator.jsonDeserialize())
            .modifiers(PUBLIC)
            .className(pojo -> pojo.getClassName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
  }

  @Override
  public Writer generate(JavaObjectPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaObjectPojo, PojoSettings> content() {
    return FieldsGenerator.fields()
        .<JavaObjectPojo>contraMap(pojo -> pojo)
        .appendSingleBlankLine()
        .append(PojoConstructorGenerator.generator())
        .appendSingleBlankLine()
        .appendList(
            EnumGenerator.nested(),
            pojo -> pojo.getMembers().flatMapOptional(JavaPojoMember::asEnumPojo),
            newLine())
        .appendSingleBlankLine()
        .appendList(GetterGenerator.generator(), JavaObjectPojo::getMembers, newLine())
        .appendSingleBlankLine()
        .append(WitherGenerator.generator(), pojo -> pojo)
        .appendSingleBlankLine()
        .append(PojoPropertyCountMethod.propertyCountMethod())
        .appendSingleBlankLine()
        .append(EqualsGenerator.equalsMethod())
        .appendSingleBlankLine()
        .append(HashCodeGenerator.hashCodeMethod())
        .appendSingleBlankLine()
        .append(ToStringGenerator.toStringMethod(), pojo -> pojo)
        .appendSingleBlankLine()
        .append(MultipleOfValidationMethodGenerator.generator())
        .appendSingleBlankLine()
        .appendList(
            UniqueItemsValidationMethodGenerator.generator().appendSingleBlankLine(),
            JavaObjectPojo::getMembers)
        .append(new NormalBuilderGenerator())
        .appendSingleBlankLine()
        .append(new SafeBuilderGenerator());
  }
}
