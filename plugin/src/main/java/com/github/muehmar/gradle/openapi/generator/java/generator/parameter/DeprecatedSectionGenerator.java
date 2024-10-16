package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static io.github.muehmar.codegenerator.Generator.constant;

import io.github.muehmar.codegenerator.Generator;

public class DeprecatedSectionGenerator {
  private DeprecatedSectionGenerator() {}

  public static <A, B> Generator<A, B> deprecatedSectionGenerator() {
    return Generator.<A, B>constant("/**")
        .append(
            constant(
                " * @deprecated The generation of parameters will be removed with the next major version 4.x of the"))
        .append(
            constant(
                " *     plugin. Consider using the official openapi generator plugin to generate the endpoints from"))
        .append(
            constant(
                " *     the openapi specification while using the DTO's generated by this plugin. You can find an"))
        .append(
            constant(" *     example with Spring Boot in the GitHub repository of this plugin."))
        .append(constant(" */"))
        .append(constant("@Deprecated"));
  }
}