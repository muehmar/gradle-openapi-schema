package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class RefsGeneratorTest {
  @Test
  void fieldRefs_when_calledForBirthdate_then_correctRefs() {
    final Generator<JavaPojoMember, PojoSettings> gen = RefsGenerator.fieldRefs();
    final JavaPojoMember javaPojoMember =
        JavaPojoMember.wrap(PojoMembers.requiredBirthdate(), TypeMappings.empty());

    final Writer writer = gen.generate(javaPojoMember, defaultTestSettings(), javaWriter());

    assertEquals(PList.of("java.time.LocalDate"), writer.getRefs());

    assertEquals("", writer.asString());
  }
}
