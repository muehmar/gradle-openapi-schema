package com.github.muehmar.gradle.openapi.generator.java.generator.array;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class FactoryMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctOutput() {
    final Generator<JavaArrayPojo, PojoSettings> generator =
        FactoryMethodGenerator.factoryMethodGenerator();
    final Writer writer =
        generator.generate(JavaPojos.arrayPojo(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("arrayPojoWithMappedListType")
  void generate_when_arrayPojoWithMappedListType_then_correctOutput() {
    final Generator<JavaArrayPojo, PojoSettings> generator =
        FactoryMethodGenerator.factoryMethodGenerator();

    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            componentName("Posology", "Dto"),
            "Doses to be taken",
            Nullability.NOT_NULLABLE,
            NumericType.formatDouble(),
            Constraints.empty());

    final JavaArrayPojo javaArrayPojo =
        JavaArrayPojo.wrap(
            arrayPojo,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping(JavaRefs.JAVA_UTIL_LIST, "custom.CustomList")));

    final Writer writer = generator.generate(javaArrayPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
