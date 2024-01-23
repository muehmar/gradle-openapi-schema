package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.multipojo.MultiPojoContainerGenerator.multiPojoContainerGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.singlepojo.SinglePojoContainerGenerator.singlePojoContainerGenerator;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.PojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.array.ArrayPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.ObjectPojoGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaFileName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.MultiPojoContainer;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.SinglePojoContainer;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;
import io.github.muehmar.codegenerator.writer.Writer;

public class JavaPojoGenerator implements PojoGenerator {

  @Override
  public NonEmptyList<GeneratedFile> generatePojo(Pojo pojo, PojoSettings pojoSettings) {
    return JavaPojo.wrap(pojo, pojoSettings.getTypeMappings())
        .asList()
        .flatMap(javaPojo -> generatePojo(javaPojo, pojoSettings));
  }

  public NonEmptyList<GeneratedFile> generatePojo(JavaPojo pojo, PojoSettings pojoSettings) {
    final Writer writer = javaWriter();

    final String content =
        pojo.fold(
                arrayPojo -> generateArrayPojo(arrayPojo, writer, pojoSettings),
                enumPojo -> generateEnumPojo(enumPojo, writer, pojoSettings),
                objectPojo -> generateObjectPojo(objectPojo, writer, pojoSettings))
            .asString();

    final JavaFileName javaFileName = JavaFileName.fromSettingsAndPojo(pojoSettings, pojo);
    final GeneratedFile mainFile = new GeneratedFile(javaFileName.asPath(), content);
    return NonEmptyList.of(mainFile).concat(generateAuxiliaryPojoFiles(pojo, pojoSettings));
  }

  private Writer generateObjectPojo(JavaObjectPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final ObjectPojoGenerator objectPojoGenerator = new ObjectPojoGenerator();
    return objectPojoGenerator.generate(pojo, pojoSettings, writer);
  }

  private Writer generateEnumPojo(JavaEnumPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final EnumGenerator generator = EnumGenerator.topLevel();
    return generator.generate(pojo.asEnumContent(), pojoSettings, writer);
  }

  private Writer generateArrayPojo(JavaArrayPojo pojo, Writer writer, PojoSettings pojoSettings) {
    final ArrayPojoGenerator arrayPojoGenerator = new ArrayPojoGenerator();
    return arrayPojoGenerator.generate(pojo, pojoSettings, writer);
  }

  private PList<GeneratedFile> generateAuxiliaryPojoFiles(JavaPojo pojo, PojoSettings settings) {
    final PList<GeneratedFile> singlePojoContainerFiles =
        pojo.asObjectPojo()
            .map(JavaObjectPojo::getSinglePojoContainers)
            .orElseGet(PList::empty)
            .map(container -> createSinglePojoContainerFile(container, settings));
    final PList<GeneratedFile> multiPojoContainerFiles =
        pojo.asObjectPojo()
            .map(JavaObjectPojo::getMultiPojoContainer)
            .orElseGet(PList::empty)
            .map(container -> createMultiPojoContainerFile(container, settings));
    return singlePojoContainerFiles.concat(multiPojoContainerFiles);
  }

  private static GeneratedFile createSinglePojoContainerFile(
      SinglePojoContainer container, PojoSettings settings) {
    final Writer writer =
        singlePojoContainerGenerator().generate(container, settings, javaWriter());
    final String content = writer.asString();
    final JavaFileName javaFileName =
        JavaFileName.fromSettingsAndClassname(settings, container.getContainerName());
    return new GeneratedFile(javaFileName.asPath(), content);
  }

  private static GeneratedFile createMultiPojoContainerFile(
      MultiPojoContainer container, PojoSettings settings) {
    final Writer writer = multiPojoContainerGenerator().generate(container, settings, javaWriter());
    final String content = writer.asString();
    final JavaFileName javaFileName =
        JavaFileName.fromSettingsAndClassname(settings, container.getContainerName());
    return new GeneratedFile(javaFileName.asPath(), content);
  }
}
