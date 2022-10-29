package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GetterGeneratorTest {

  @Mock private GetterGenerator.RequiredNullableGetterGen requiredNullableGetterGen;
  @Mock private GetterGenerator.RequiredNotNullableGetterGen requiredNotNullableGetterGen;
  @Mock private GetterGenerator.OptionalNullableGetterGen optionalNullableGetterGen;
  @Mock private GetterGenerator.OptionalNotNullableGetterGen optionalNotNullableGetterGen;

  @BeforeEach
  void setupMocks() {
    when(requiredNullableGetterGen.generate(any(), any(), any()))
        .thenReturn(Writer.createDefault().println("requiredNullable"));
    when(requiredNotNullableGetterGen.generate(any(), any(), any()))
        .thenReturn(Writer.createDefault().println("requiredNotNullable"));
    when(optionalNullableGetterGen.generate(any(), any(), any()))
        .thenReturn(Writer.createDefault().println("optionalNullable"));
    when(optionalNotNullableGetterGen.generate(any(), any(), any()))
        .thenReturn(Writer.createDefault().println("optionalNotNullable"));
  }

  @Test
  void generator_when_requiredAndNotNullableField_then_requiredNotNullableGetterGenCalled() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        GetterGenerator.generator(
            requiredNullableGetterGen,
            requiredNotNullableGetterGen,
            optionalNullableGetterGen,
            optionalNotNullableGetterGen);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("requiredNotNullable", writer.asString());
  }

  @Test
  void generator_when_requiredAndNullableField_then_requiredNullableGetterGenCalled() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        GetterGenerator.generator(
            requiredNullableGetterGen,
            requiredNotNullableGetterGen,
            optionalNullableGetterGen,
            optionalNotNullableGetterGen);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("requiredNullable", writer.asString());
  }

  @Test
  void generator_when_optionalAndNotNullableField_then_optionalNotNullableGetterGenCalled() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        GetterGenerator.generator(
            requiredNullableGetterGen,
            requiredNotNullableGetterGen,
            optionalNullableGetterGen,
            optionalNotNullableGetterGen);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.OPTIONAL, Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("optionalNotNullable", writer.asString());
  }

  @Test
  void generator_when_optionalAndNullableField_then_optionalNullableGetterGenCalled() {
    final Generator<JavaPojoMember, PojoSettings> generator =
        GetterGenerator.generator(
            requiredNullableGetterGen,
            requiredNotNullableGetterGen,
            optionalNullableGetterGen,
            optionalNotNullableGetterGen);
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.OPTIONAL, Nullability.NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("optionalNullable", writer.asString());
  }
}
