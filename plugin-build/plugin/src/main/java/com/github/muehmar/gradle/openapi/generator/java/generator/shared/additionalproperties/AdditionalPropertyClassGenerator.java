package com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.OPENAPI_UTIL_PACKAGE;

import io.github.muehmar.codegenerator.Generator;

public class AdditionalPropertyClassGenerator {
  public static final String CLASSNAME = "AdditionalProperty";

  private AdditionalPropertyClassGenerator() {}

  public static Generator<Void, Void> additionalPropertyClassGenerator() {
    return Generator.constant(
        "package %s;\n"
            + "\n"
            + "import java.util.Objects;\n"
            + "\n"
            + "public class %s<T> {\n"
            + "  private final String name;\n"
            + "  private final T value;\n"
            + "\n"
            + "  public %s(String name, T value) {\n"
            + "    this.name = name;\n"
            + "    this.value = value;\n"
            + "  }\n"
            + "\n"
            + "  public String getName() {\n"
            + "    return name;\n"
            + "  }\n"
            + "\n"
            + "  public T getValue() {\n"
            + "    return value;\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public boolean equals(Object o) {\n"
            + "    if (this == o) return true;\n"
            + "    if (o == null || getClass() != o.getClass()) return false;\n"
            + "    final %s<?> that = (%s<?>) o;\n"
            + "    return Objects.equals(name, that.name) && Objects.equals(value, that.value);\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public int hashCode() {\n"
            + "    return Objects.hash(name, value);\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public String toString() {\n"
            + "    return \"%s{\" + \"name='\" + name + '\\'' + \", value=\" + value + '}';\n"
            + "  }\n"
            + "}",
        OPENAPI_UTIL_PACKAGE, CLASSNAME, CLASSNAME, CLASSNAME, CLASSNAME, CLASSNAME);
  }
}
