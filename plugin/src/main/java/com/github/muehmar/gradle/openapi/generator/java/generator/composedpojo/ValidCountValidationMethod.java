package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.Function;

public class ValidCountValidationMethod {
  private ValidCountValidationMethod() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(isValidAgainstNoSchemaMethod())
        .append(isValidAgainstMoreThanOneSchema())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaComposedPojo, PojoSettings> isValidAgainstNoSchemaMethod() {
    final Function<JavaComposedPojo, String> message =
        pojo ->
            String.format(
                "Is not valid against one of the schemas [%s]",
                pojo.getJavaPojos().map(JavaPojo::getName).map(PojoName::getName).mkString(", "));
    final Generator<JavaComposedPojo, PojoSettings> annotation =
        ValidationGenerator.assertFalse(message);
    final MethodGen<JavaComposedPojo, PojoSettings> method =
        MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .modifiers((pojo, settings) -> settings.getRawGetter().getModifier().asJavaModifiers())
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isValidAgainstNoSchema")
            .noArguments()
            .content("return getValidCount() == 0;")
            .build();
    return annotation.append(method);
  }

  private static Generator<JavaComposedPojo, PojoSettings> isValidAgainstMoreThanOneSchema() {
    final Function<JavaComposedPojo, String> message =
        pojo ->
            String.format(
                "Is valid against more than one of the schemas [%s]",
                pojo.getJavaPojos().map(JavaPojo::getName).map(PojoName::getName).mkString(", "));
    final Generator<JavaComposedPojo, PojoSettings> annotation =
        ValidationGenerator.assertFalse(message);
    final MethodGen<JavaComposedPojo, PojoSettings> method =
        MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
            .modifiers((pojo, settings) -> settings.getRawGetter().getModifier().asJavaModifiers())
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isValidAgainstMoreThanOneSchema")
            .noArguments()
            .content("return getValidCount() > 1;")
            .build();
    return annotation
        .append(method)
        .prependNewLine()
        .filter(pojo -> pojo.getCompositionType().equals(ComposedPojo.CompositionType.ONE_OF));
  }
}
