package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist.OptionalNullableGetter.optionalNullableGetter;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class OptionalNullableGetterTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(GetterType.class)
  @SnapshotName("nullableStringItem")
  void generate_when_nullableStringItem_then_matchSnapshot(GetterType getterType) {
    final Generator<JavaPojoMember, PojoSettings> generator = optionalNullableGetter(getterType);
    final JavaPojoMember member =
        TestJavaPojoMembers.list(
            StringType.noFormat()
                .withNullability(NULLABLE)
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("pattern"))),
            OPTIONAL,
            NULLABLE,
            Constraints.ofSize(Size.of(5, 10)));

    final Writer writer = generator.generate(member, defaultTestSettings(), javaWriter());

    expect.scenario(getterType.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
