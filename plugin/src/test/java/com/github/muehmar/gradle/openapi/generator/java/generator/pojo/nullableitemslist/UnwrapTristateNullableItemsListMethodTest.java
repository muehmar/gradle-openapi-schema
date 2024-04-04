package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.nullableitemslist.UnwrapTristateNullableItemsListMethod.unwrapTristateNullableItemsListMethod;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class UnwrapTristateNullableItemsListMethodTest {
  private Expect expect;

  @Test
  @SnapshotName("unwrapTristateNullableItemsList")
  void generate_when_unwrapTristateNullableItemsList_then_matchSnapshot() {
    final Generator<Object, PojoSettings> generator = unwrapTristateNullableItemsListMethod();

    final Writer writer = generator.generate("", defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
