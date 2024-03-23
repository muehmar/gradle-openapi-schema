package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither.WitherGenerator.witherGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberBuilder.javaPojoMemberBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringType;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class WitherGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNullabilityAndNecessityVariants")
  void generate_when_calledWithNullabilityAndNecessityVariants_then_correctOutput() {
    final Generator<WitherGenerator.WitherContent, PojoSettings> generator = witherGenerator();
    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants().getWitherContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("noAdditionalProperties")
  void generate_when_noAdditionalProperties_then_correctOutput() {
    final Generator<WitherGenerator.WitherContent, PojoSettings> generator = witherGenerator();
    final Writer writer =
        generator.generate(
            WitherContentBuilder.create()
                .className(JavaName.fromString("ObjectDto"))
                .membersForWithers(PList.single(requiredString()))
                .technicalPojoMembers(
                    PList.of(requiredString(), requiredBirthdate())
                        .flatMap(JavaPojoMember::getTechnicalMembers))
                .build(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generate_when_illegalIdentifierPojo_then_correctOutput() {
    final Generator<WitherGenerator.WitherContent, PojoSettings> generator = witherGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.illegalIdentifierPojo().getWitherContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("propertyNameMatchesSubstringOfOtherProperty")
  void
      generate_when_pojoContainsPropertyNameWhichMatchesSubstringOfOtherProperty_then_correctOutput() {
    final JavaPojoMember surnameMember =
        javaPojoMemberBuilder()
            .pojoName(invoiceName())
            .name(JavaName.fromString("surname"))
            .description("desc")
            .javaType(stringType().withNullability(NULLABLE))
            .necessity(Necessity.REQUIRED)
            .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
            .build();

    final JavaPojoMember nameMember =
        javaPojoMemberBuilder()
            .pojoName(invoiceName())
            .name(JavaName.fromString("name"))
            .description("desc")
            .javaType(stringType().withNullability(NULLABLE))
            .necessity(Necessity.OPTIONAL)
            .type(JavaPojoMember.MemberType.OBJECT_MEMBER)
            .build();

    final JavaObjectPojo pojo = JavaPojos.objectPojo(PList.of(surnameMember, nameMember));

    final Generator<WitherGenerator.WitherContent, PojoSettings> generator = witherGenerator();
    final Writer writer =
        generator.generate(pojo.getWitherContent(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
