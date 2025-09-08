package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties.AdditionalPropertyClassGenerator.additionalPropertyClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.additionalproperties.NullableAdditionalPropertyClassGenerator.nullableAdditionalPropertyClassGenerator;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.UtilsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.TristateGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonNullContainerGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonZonedDateTimeDeserializerGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.email.EmailValidatorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaFileName;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;

public class JavaUtilsGenerator implements UtilsGenerator {
  @Override
  public PList<GeneratedFile> generateUtils(PojoSettings settings) {
    return PList.of(
            tristateClass(),
            emailValidator(),
            additionalPropertyClass(),
            nullableAdditionalPropertyClass())
        .concat(PList.fromOptional(jacksonContainerClass(settings)))
        .concat(PList.fromOptional(jacksonZonedDateTimeDeserializerClass(settings)));
  }

  private static GeneratedFile tristateClass() {
    final Generator<Void, Void> tristateGen = TristateGenerator.tristateClass();
    final Writer writer = tristateGen.generate(noData(), noSettings(), javaWriter());
    final JavaFileName javaFileName = JavaFileName.fromRef(OpenApiUtilRefs.TRISTATE);
    return new GeneratedFile(javaFileName.asPath(), writer.asString());
  }

  private static Optional<GeneratedFile> jacksonContainerClass(PojoSettings settings) {
    if (settings.isJacksonJson()) {
      final Generator<Void, Void> jacksonContainerGen =
          JacksonNullContainerGenerator.containerClass();
      final Writer writer = jacksonContainerGen.generate(noData(), noSettings(), javaWriter());
      final JavaFileName javaFileName =
          JavaFileName.fromRef(OpenApiUtilRefs.JACKSON_NULL_CONTAINER);
      return Optional.of(new GeneratedFile(javaFileName.asPath(), writer.asString()));
    } else {
      return Optional.empty();
    }
  }

  private static Optional<GeneratedFile> jacksonZonedDateTimeDeserializerClass(
      PojoSettings settings) {
    if (settings.isJacksonJson()) {
      final Generator<Void, Void> generator =
          JacksonZonedDateTimeDeserializerGenerator.zonedDateTimeDeserializer();
      final Writer writer = generator.generate(noData(), noSettings(), javaWriter());
      final JavaFileName javaFileName =
          JavaFileName.fromRef(OpenApiUtilRefs.ZONED_DATE_TIME_DESERIALIZER);
      return Optional.of(new GeneratedFile(javaFileName.asPath(), writer.asString()));
    } else {
      return Optional.empty();
    }
  }

  private static GeneratedFile emailValidator() {
    final Generator<Void, Void> emailedValidatorGenerator =
        EmailValidatorGenerator.emailValidatorGenerator();
    final Writer writer = emailedValidatorGenerator.generate(noData(), noSettings(), javaWriter());
    final JavaFileName javaFileName = JavaFileName.fromRef(OpenApiUtilRefs.EMAIL_VALIDATOR);
    return new GeneratedFile(javaFileName.asPath(), writer.asString());
  }

  private static GeneratedFile additionalPropertyClass() {
    final Generator<Void, Void> additionalPropertyClassGenerator =
        additionalPropertyClassGenerator();
    final Writer writer =
        additionalPropertyClassGenerator.generate(noData(), noSettings(), javaWriter());
    final JavaFileName javaFileName = JavaFileName.fromRef(OpenApiUtilRefs.ADDITIONAL_PROPERTY);
    return new GeneratedFile(javaFileName.asPath(), writer.asString());
  }

  private static GeneratedFile nullableAdditionalPropertyClass() {
    final Generator<Void, Void> nullableAdditionalPropertyClassGenerator =
        nullableAdditionalPropertyClassGenerator();
    final Writer writer =
        nullableAdditionalPropertyClassGenerator.generate(noData(), noSettings(), javaWriter());
    final JavaFileName javaFileName =
        JavaFileName.fromRef(OpenApiUtilRefs.NULLABLE_ADDITIONAL_PROPERTY);
    return new GeneratedFile(javaFileName.asPath(), writer.asString());
  }

  private static Void noData() {
    return null;
  }

  private static Void noSettings() {
    return null;
  }
}
