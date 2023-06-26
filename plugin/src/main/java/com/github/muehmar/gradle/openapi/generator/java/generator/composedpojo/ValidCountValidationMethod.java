package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.AnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import java.util.function.Function;

public class ValidCountValidationMethod {
  private ValidCountValidationMethod() {}

  public static Generator<JavaObjectPojo, PojoSettings> validCountValidationMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(
            isValidAgainstNoSchemaMethod("OneOf"), ValidCountValidationMethod::getOneOfPojos)
        .appendOptional(
            isValidAgainstNoSchemaMethod("AnyOf"), ValidCountValidationMethod::getAnyOfPojos)
        .appendOptional(
            isValidAgainstMoreThanOneSchema(), ValidCountValidationMethod::getOneOfPojos)
        .filter(Filters.isValidationEnabled());
  }

  private static Optional<PList<JavaObjectPojo>> getOneOfPojos(JavaObjectPojo pojo) {
    return pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos);
  }

  private static Optional<PList<JavaObjectPojo>> getAnyOfPojos(JavaObjectPojo pojo) {
    return pojo.getAnyOfComposition().map(JavaAnyOfComposition::getPojos);
  }

  private static Generator<PList<JavaObjectPojo>, PojoSettings> isValidAgainstNoSchemaMethod(
      String oneOfOrAnyOf) {
    final Function<PList<JavaObjectPojo>, String> message =
        pojos ->
            String.format(
                "Is not valid against one of the schemas [%s]",
                pojos.map(JavaObjectPojo::getSchemaName).mkString(", "));
    final Generator<PList<JavaObjectPojo>, PojoSettings> annotation =
        ValidationGenerator.assertFalse(message);
    final MethodGen<PList<JavaObjectPojo>, PojoSettings> method =
        MethodGenBuilder.<PList<JavaObjectPojo>, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName(String.format("isValidAgainstNo%sSchema", oneOfOrAnyOf))
            .noArguments()
            .content(String.format("return get%sValidCount() == 0;", oneOfOrAnyOf))
            .build();
    return JavaDocGenerators.<PList<JavaObjectPojo>>deprecatedValidationMethodJavaDoc()
        .append(annotation)
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method);
  }

  private static Generator<PList<JavaObjectPojo>, PojoSettings> isValidAgainstMoreThanOneSchema() {
    final Function<PList<JavaObjectPojo>, String> message =
        pojos ->
            String.format(
                "Is valid against more than one of the schemas [%s]",
                pojos.map(JavaPojo::getSchemaName).mkString(", "));
    final Generator<PList<JavaObjectPojo>, PojoSettings> annotation =
        ValidationGenerator.assertFalse(message);
    final MethodGen<PList<JavaObjectPojo>, PojoSettings> method =
        MethodGenBuilder.<PList<JavaObjectPojo>, PojoSettings>create()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("boolean")
            .methodName("isValidAgainstMoreThanOneSchema")
            .noArguments()
            .content("return getOneOfValidCount() > 1;")
            .build();
    return JavaDocGenerators.<PList<JavaObjectPojo>>deprecatedValidationMethodJavaDoc()
        .append(annotation)
        .append(AnnotationGenerator.deprecatedValidationMethod())
        .append(JacksonAnnotationGenerator.jsonIgnore())
        .append(method)
        .prependNewLine();
  }
}
