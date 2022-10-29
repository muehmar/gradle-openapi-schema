package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.jackson;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.Generator;

public class JacksonNullContainerGenerator {
  private JacksonNullContainerGenerator() {}

  public static Generator<Void, Void> containerClass() {
    return Generator.ofWriterFunction(
        w ->
            w.println("package %s;", OpenApiUtilRefs.OPENAPI_UTIL_PACKAGE)
                .println()
                .println("import com.fasterxml.jackson.annotation.JsonValue;")
                .println()
                .println("public class JacksonNullContainer<T> {")
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
