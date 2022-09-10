package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class NewRefsGeneratorTest {
  @Test
  void fieldRefs_when_calledForBirthdate_then_correctRefs() {
    final Generator<JavaPojoMember, PojoSettings> gen = NewRefsGenerator.fieldRefs();
    final JavaPojoMember javaPojoMember =
        JavaPojoMember.wrap(NewPojoMembers.requiredBirthdate(), TypeMappings.empty());

    final Writer writer =
        gen.generate(javaPojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(PList.of("java.time.LocalDate"), writer.getRefs());

    assertEquals("", writer.asString());
  }
}
