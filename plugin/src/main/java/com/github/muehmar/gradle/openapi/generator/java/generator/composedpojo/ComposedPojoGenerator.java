package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.java.ClassGen.Declaration.TOP_LEVEL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.NewFieldsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.PackageGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class ComposedPojoGenerator implements Generator<JavaComposedPojo, PojoSettings> {

  private final Generator<JavaComposedPojo, PojoSettings> delegate;

  public ComposedPojoGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .enum_()
            .declaration(TOP_LEVEL)
            .packageGen(new PackageGenerator<>())
            .javaDoc(
                JavaDocGenerator.<PojoSettings>javaDoc()
                    .contraMap(JavaComposedPojo::getDescription))
            .noAnnotations()
            .modifiers(PUBLIC)
            .className(enumPojo -> enumPojo.getName().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .build();
  }

  @Override
  public Writer generate(JavaComposedPojo data, PojoSettings settings, Writer writer) {
    return delegate.generate(data, settings, writer);
  }

  private Generator<JavaComposedPojo, PojoSettings> content() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(NewFieldsGenerator.fields(), JavaComposedPojo::getJavaPojos);
  }
}
