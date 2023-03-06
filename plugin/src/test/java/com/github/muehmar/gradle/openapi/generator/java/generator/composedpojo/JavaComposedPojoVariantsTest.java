package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import au.com.origin.snapshots.Expect;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class JavaComposedPojoVariantsTest {

  protected static Stream<Arguments> variants() {
    return JavaComposedPojos.variants();
  }

  protected void compareSnapshot(
      Expect expect,
      ComposedPojo.CompositionType type,
      Optional<Discriminator> discriminator,
      JavaComposedPojo pojo,
      Writer writer) {
    final String scenario =
        PList.of(type, discriminator.orElse(null))
            .mkString(",")
            .replace("=", "->")
            .replace("[", "(")
            .replace("]", ")");

    expect.scenario(scenario).toMatchSnapshot(writer.asString());
  }
}
