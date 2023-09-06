package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.Jakarta2ValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class JavaTypeGeneratorsTest {
  private Expect expect;

  @Test
  @SnapshotName("arrayType")
  void deepAnnotatedFullClassName_when_usedWithArrayType_then_correctOutputAndRefs() {
    final Generator<JavaType, PojoSettings> generator =
        JavaTypeGenerators.deepAnnotatedFullClassName();
    final ArrayType arrayType =
        ArrayType.ofItemType(
            StringType.noFormat()
                .withConstraints(Constraints.ofMinAndMax(new Min(5), new Max(10))));
    final JavaType javaType = JavaType.wrap(arrayType, TypeMappings.empty());

    final Writer writer = generator.generate(javaType, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());

    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.MIN::equals));
    assertTrue(writer.getRefs().exists(Jakarta2ValidationRefs.MAX::equals));
  }
}
