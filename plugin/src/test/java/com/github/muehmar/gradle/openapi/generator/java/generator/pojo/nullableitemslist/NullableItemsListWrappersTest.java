package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.NullableItemsListWrappers.nullableItemsListWrappers;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class NullableItemsListWrappersTest {
  private Expect expect;

  @Test
  void generate_when_noNullableItemsListMembers_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = nullableItemsListWrappers();

    final Writer writer =
        generator.generate(JavaPojos.sampleObjectPojo1(), defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }

  @Test
  void generate_when_nullableItemsListMembers_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = nullableItemsListWrappers();

    final Writer writer =
        generator.generate(
            JavaPojos.objectPojo(TestJavaPojoMembers.optionalListWithNullableItems()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
