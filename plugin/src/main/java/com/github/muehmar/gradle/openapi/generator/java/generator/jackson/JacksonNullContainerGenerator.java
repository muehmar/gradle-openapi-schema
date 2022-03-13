package com.github.muehmar.gradle.openapi.generator.java.generator.jackson;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import io.github.muehmar.pojoextension.generator.Generator;

public class JacksonNullContainerGenerator {
  private JacksonNullContainerGenerator() {}

  public static Generator<Void, Void> containerClass() {
    return Generator.ofWriterFunction(
        w ->
            w.println("public static class JacksonNullContainer<T> {")
                .tab(1)
                .println("private final T value;")
                .println()
                .tab(1)
                .println("public JacksonNullContainer(T value) {")
                .tab(2)
                .println("this.value = value;")
                .tab(1)
                .println("}")
                .println()
                .tab(1)
                .println("@JsonValue")
                .ref(JacksonRefs.JSON_VALUE)
                .tab(1)
                .println("public T getValue() {")
                .tab(2)
                .println("return value;")
                .tab(1)
                .println("}")
                .println("}"));
  }
}
