package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.java.GeneratorUtil.noSettingsGen;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator.javaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator.validationAnnotations;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.ValidationGenerator.validationAnnotationsForType;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.AnnotatedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.AnnotationsCreator;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.writer.Writer;

public class RequiredNotNullableGetter {
  private RequiredNotNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> getter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(noSettingsGen(javaDoc()), JavaPojoMember::getDescription)
        .append(validationAnnotations())
        .append(getterMethod())
        .filter(JavaPojoMember::isRequiredAndNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> getterMethod() {
    return JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(returnType().contraMap(JavaPojoMember::getJavaType))
        .methodName((f, settings) -> f.getGetterNameWithSuffix(settings).asString())
        .noArguments()
        .content(f -> String.format("return %s;", f.getNameAsIdentifier()))
        .build()
        .append(RefsGenerator.fieldRefs());
  }

  private static Generator<JavaType, PojoSettings> returnType() {
    return (type, s, w) -> {
      final AnnotationsCreator annotationsCreator =
          valueType -> {
            final Writer annotationWriter =
                validationAnnotationsForType().generate(valueType, s, Writer.createDefault());
            return new AnnotationsCreator.Annotations(
                annotationWriter.asString(), annotationWriter.getRefs());
          };
      final AnnotatedClassName annotatedClass = type.getFullAnnotatedClassName(annotationsCreator);
      return annotatedClass
          .getImports()
          .foldLeft(w, Writer::ref)
          .println(annotatedClass.getClassName().asString());
    };
  }
}
