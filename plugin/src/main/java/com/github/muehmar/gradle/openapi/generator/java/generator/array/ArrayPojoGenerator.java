package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.UniqueItemsValidationMethodGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class ArrayPojoGenerator implements Generator<JavaArrayPojo, PojoSettings> {

  private final Generator<JavaArrayPojo, PojoSettings> delegate;

  public ArrayPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaArrayPojo, PojoSettings>create()
            .clazz()
            .declaration(TOP_LEVEL)
            .packageGen(new PackageGenerator<>())
            .javaDoc(JavaDocGenerator.javaDoc((pojo, settings) -> pojo.getDescription()))
            .noAnnotations()
            .modifiers(PUBLIC)
            .className(pojo -> pojo.getClassName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
  }

  @Override
  public Writer generate(JavaArrayPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaArrayPojo, PojoSettings> content() {
    return Generator.<JavaArrayPojo, PojoSettings>emptyGen()
        .append(MemberGenerator.generator(), JavaArrayPojo::getMemberContent)
        .appendSingleBlankLine()
        .append(PojoConstructorGenerator.generator(), JavaArrayPojo::getConstructorContent)
        .appendSingleBlankLine()
        .appendOptional(EnumGenerator.nested(), pojo -> pojo.getArrayPojoMember().asEnumPojo())
        .appendSingleBlankLine()
        .append(GetterGenerator.generator(), JavaArrayPojo::getArrayPojoMember)
        .appendSingleBlankLine()
        .append(WitherGenerator.generator(), pojo -> pojo)
        .appendSingleBlankLine()
        .append(UniqueItemsValidationMethodGenerator.generator(), JavaArrayPojo::getArrayPojoMember)
        .appendSingleBlankLine()
        .append(EqualsGenerator.equalsMethod(), JavaArrayPojo::getEqualsContent)
        .appendSingleBlankLine()
        .append(HashCodeGenerator.hashCodeMethod(), JavaArrayPojo::getHashCodeContent)
        .appendSingleBlankLine()
        .append(ToStringGenerator.toStringMethod(), JavaArrayPojo::getToStringContent);
  }
}
