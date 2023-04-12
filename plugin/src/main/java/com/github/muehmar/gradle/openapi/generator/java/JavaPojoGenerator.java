package com.github.muehmar.gradle.openapi.generator.java;

import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.array.ArrayPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ComposedPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.freeform.FreeFormPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ObjectPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaFreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.Writer;
import io.github.muehmar.codegenerator.Generator;
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
        .close(packagePath + "/" + pojo.getClassName() + ".java");
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
    final ObjectPojoGenerator objectPojoGenerator = new ObjectPojoGenerator();
    final String output = applyGen(objectPojoGenerator, pojo, pojoSettings);
    writer.println(output);
    return writer;
  }

  private Writer generateEnumPojo(JavaEnumPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final EnumGenerator generator = EnumGenerator.topLevel();
    final String output = applyGen(generator, pojo, pojoSettings);
    writer.print(output);
    return writer;
  }

  private Writer generateArrayPojo(JavaArrayPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final ArrayPojoGenerator arrayPojoGenerator = new ArrayPojoGenerator();
    final String output = applyGen(arrayPojoGenerator, pojo, pojoSettings);
    writer.println(output);
    return writer;
  }

  private static <T, S> String applyGen(Generator<T, S> gen, T data, S settings) {
    return gen.generate(
            data, settings, io.github.muehmar.codegenerator.writer.Writer.createDefault())
        .asString()
        .replace("%", "%%");
  }
}
