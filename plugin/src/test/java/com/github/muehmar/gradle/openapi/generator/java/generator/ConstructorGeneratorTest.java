package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class ConstructorGeneratorTest {

  @Test
  void generate_when_minimalGeneratorCreated_then_outputCorrect() {
    final ConstructorGenerator<PList<String>, Void> generator =
        ConstructorGeneratorBuilder.<PList<String>, Void>create()
            .modifiers(JavaModifier.PUBLIC)
            .className(l -> l.apply(0))
            .arguments(l -> l.drop(1))
            .content("System.out.println(\"Hello World\");")
            .build();

    final PList<String> data = PList.of("Customer", "String a", "int b");

    final String output = generator.generate(data, null, Writer.createDefault()).asString();
    assertEquals(
        "public Customer(String a, int b) {\n" + "  System.out.println(\"Hello World\");\n" + "}",
        output);
  }
}
