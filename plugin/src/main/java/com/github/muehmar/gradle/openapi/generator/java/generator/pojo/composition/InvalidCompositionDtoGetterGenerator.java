package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.JavaDocGenerators.deprecatedValidationMethodJavaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator.jsonIgnore;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.SettingsFunctions;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

public class InvalidCompositionDtoGetterGenerator {
  private InvalidCompositionDtoGetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> invalidCompositionDtoGetterGenerator() {
    final Generator<JavaObjectPojo, PojoSettings> method =
        JavaGenerators.<JavaObjectPojo, PojoSettings>methodGen()
            .modifiers(SettingsFunctions::validationMethodModifiers)
            .noGenericTypes()
            .returnType("List<Object>")
            .methodName("getInvalidCompositionDtos")
            .noArguments()
            .content(invalidCompositionDtoGetterContent())
            .build()
            .append(ref(JavaRefs.JAVA_UTIL_LIST))
            .append(ref(JavaRefs.JAVA_UTIL_ARRAY_LIST));

    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(deprecatedValidationMethodJavaDoc())
        .append(ValidationGenerator.validAnnotation())
        .append(jsonIgnore())
        .append(method)
        .filter(p -> p.getOneOfComposition().isPresent() || p.getAnyOfComposition().isPresent())
        .filter(Filters.isValidationEnabled());
  }

  private static Generator<JavaObjectPojo, PojoSettings> invalidCompositionDtoGetterContent() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(constant("final List<Object> dtos = new ArrayList<>();"))
        .append(addInvalidOneOfDtos())
        .append(addInvalidAnyOfDtos())
        .append(constant("return dtos;"));
  }

  private static Generator<JavaObjectPojo, PojoSettings> addInvalidOneOfDtos() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(constant("if(getOneOfValidCount() != 1) {"))
        .appendList(addSingleInvalidDto(), JavaObjectPojo::getOneOfPojos)
        .append(constant("}"))
        .filter(pojo -> pojo.getOneOfComposition().isPresent());
  }

  private static Generator<JavaObjectPojo, PojoSettings> addInvalidAnyOfDtos() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(constant("if(getAnyOfValidCount() == 0) {"))
        .appendList(addSingleInvalidDto(), JavaObjectPojo::getAnyOfPojos)
        .append(constant("}"))
        .filter(pojo -> pojo.getAnyOfComposition().isPresent());
  }

  private static Generator<JavaObjectPojo, PojoSettings> addSingleInvalidDto() {
    return Generator.<JavaObjectPojo, PojoSettings>of(
            (p, s, w) -> w.println("dtos.add(%s());", CompositionNames.asConversionMethodName(p)))
        .indent(1);
  }
}
