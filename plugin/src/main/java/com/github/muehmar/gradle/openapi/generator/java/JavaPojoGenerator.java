package com.github.muehmar.gradle.openapi.generator.java;

import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.array.ArrayPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo.ComposedPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.freeform.FreeFormPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ObjectPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaFileName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaFreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;
import io.github.muehmar.codegenerator.writer.Writer;

public class JavaPojoGenerator implements PojoGenerator {

  @Override
  public GeneratedFile generatePojo(Pojo pojo, PojoSettings pojoSettings) {
    final JavaPojo javaPojo = JavaPojo.wrap(pojo, pojoSettings.getTypeMappings());
    return generatePojo(javaPojo, pojoSettings);
  }

  public GeneratedFile generatePojo(JavaPojo pojo, PojoSettings pojoSettings) {
    final Writer writer = Writer.createDefault();

    final String content =
        pojo.fold(
                arrayPojo -> generateArrayPojo(arrayPojo, writer, pojoSettings),
                enumPojo -> generateEnumPojo(enumPojo, writer, pojoSettings),
                objectPojo -> generateObjectPojo(objectPojo, writer, pojoSettings),
                composedPojo -> generateComposedPojo(composedPojo, writer, pojoSettings),
                freeFormPojo -> generateFreeFormPojo(freeFormPojo, writer, pojoSettings))
            .asString();

    final JavaFileName javaFileName = JavaFileName.fromSettingsAndPojo(pojoSettings, pojo);
    return new GeneratedFile(javaFileName.asPath(), content);
  }

  private Writer generateFreeFormPojo(
      JavaFreeFormPojo freeFormPojo, Writer writer, PojoSettings pojoSettings) {
    final FreeFormPojoGenerator freeFormPojoGenerator = new FreeFormPojoGenerator();
    return freeFormPojoGenerator.generate(freeFormPojo, pojoSettings, writer);
  }

  private Writer generateComposedPojo(
      JavaComposedPojo composedPojo, Writer writer, PojoSettings pojoSettings) {
    final ComposedPojoGenerator generator = new ComposedPojoGenerator();
    return generator.generate(composedPojo, pojoSettings, writer);
  }

  private Writer generateObjectPojo(JavaObjectPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final ObjectPojoGenerator objectPojoGenerator = new ObjectPojoGenerator();
    return objectPojoGenerator.generate(pojo, pojoSettings, writer);
  }

  private Writer generateEnumPojo(JavaEnumPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final EnumGenerator generator = EnumGenerator.topLevel();
    return generator.generate(pojo, pojoSettings, writer);
  }

  private Writer generateArrayPojo(JavaArrayPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final ArrayPojoGenerator arrayPojoGenerator = new ArrayPojoGenerator();
    return arrayPojoGenerator.generate(pojo, pojoSettings, writer);
  }
}
