package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.DtoSetterGenerator.dtoSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredStringNamed;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.stringNamed;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.objectPojo;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

/**
 * The names generated for a property must not collide with the names generated for one of its
 * siblings, see issue #438.
 */
@SnapshotTest
class MemberNameCollisionTest {

  private Expect expect;

  @Test
  @SnapshotName("jsonAnchorCollidingWithApiAccessorOfSibling")
  void generatePojo_when_siblingNamedAfterTheJsonAnchor_then_anchorsDoNotCollide() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaObjectPojo pojo =
        objectPojo(requiredStringNamed("name"), requiredStringNamed("nameJson"));

    final PojoSettings settings = defaultTestSettings().withEnableValidation(false);

    final String content = generator.generate(pojo, settings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("jsonAnchorsWithBuilderMethodPrefixOfTheGetters")
  void generatePojo_when_builderPrefixIsGet_then_dtoAndBuilderAnchorsResolvedIndependently() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    // With this prefix the setters are named like the getters, so both scopes must stay apart.
    final JavaObjectPojo pojo =
        objectPojo(requiredStringNamed("name"), requiredStringNamed("nameJson"));

    final PojoSettings settings =
        defaultTestSettings().withEnableValidation(false).withBuilderMethodPrefix("get");

    final String content = generator.generate(pojo, settings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("validationGetterCollidingWithApiAccessorOfSibling")
  void generatePojo_when_siblingNamedAfterTheValidationGetter_then_anchorsDoNotCollide() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaObjectPojo pojo =
        objectPojo(requiredStringNamed("name"), requiredStringNamed("name_"));

    final String content = generator.generate(pojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("presenceFlagFieldCollidingWithPropertyOfSibling")
  void generatePojo_when_siblingNamedAfterThePresenceFlag_then_fieldsDoNotCollide() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaObjectPojo pojo =
        objectPojo(stringNamed("name", REQUIRED, NULLABLE), requiredStringNamed("isNamePresent"));

    final String content = generator.generate(pojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("notNullFlagFieldCollidingWithPropertyOfSibling")
  void generatePojo_when_siblingNamedAfterTheNotNullFlag_then_fieldsDoNotCollide() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaObjectPojo pojo =
        objectPojo(
            stringNamed("name", OPTIONAL, NOT_NULLABLE), requiredStringNamed("isNameNotNull"));

    final String content = generator.generate(pojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("nullFlagFieldCollidingWithPropertyOfSibling")
  void generatePojo_when_siblingNamedAfterTheNullFlag_then_fieldsDoNotCollide() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaObjectPojo pojo =
        objectPojo(stringNamed("name", OPTIONAL, NULLABLE), requiredStringNamed("isNameNull"));

    final String content = generator.generate(pojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("flagAccessorCollidingWithApiAccessorOfSiblingOnComposedDto")
  void generatePojo_when_siblingNamedAfterTheFlagAccessor_then_accessorsDoNotCollide() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    // The parent's builder reads the flag accessor across classes, so it cannot be renamed.
    final JavaObjectPojo variant =
        objectPojo(
                stringNamed("name", OPTIONAL, NOT_NULLABLE), requiredStringNamed("isNameNotNull"))
            .withName(JavaPojoNames.fromNameAndSuffix("Variant", "Dto"));

    final JavaObjectPojo pojo = JavaPojos.oneOfPojo(variant);

    final String content = generator.generate(pojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("frameworkMethodCollidingWithProperty")
  void generatePojo_when_propertyNamedAfterFrameworkMethod_then_frameworkMethodRenamed() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    // getPropertyCount() must stay getter-shaped for bean validation, hence it is renamed and the
    // validation getter of the property has to avoid it in turn.
    final JavaObjectPojo pojo = objectPojo(requiredStringNamed("propertyCount"));

    final String content = generator.generate(pojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_apiGettersOfTwoPropertiesCollide_then_generationFails() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    // A property name is normalised before the getter name is derived from it.
    final JavaObjectPojo pojo =
        objectPojo(requiredStringNamed("propertyName"), requiredStringNamed("PropertyName"));

    final OpenApiGeneratorException exception =
        assertThrows(
            OpenApiGeneratorException.class,
            () -> generator.generate(pojo, defaultTestSettings(), javaWriter()));

    assertTrue(exception.getMessage().contains("getPropertyName()"), exception.getMessage());
  }

  @Test
  @SnapshotName("dtoSetterReadingTheFlagAccessorAcrossClasses")
  void generateDtoSetter_when_siblingNamedAfterTheFlagAccessor_then_callSiteMatchesDeclaration() {
    final Generator<JavaObjectPojo, PojoSettings> generator = dtoSetterGenerator();

    // The call site derives the name from a different JavaPojoMember than the declaration.
    final JavaObjectPojo variant =
        objectPojo(
                stringNamed("name", OPTIONAL, NOT_NULLABLE), requiredStringNamed("isNameNotNull"))
            .withName(JavaPojoNames.fromNameAndSuffix("Variant", "Dto"));

    final Writer writer =
        generator.generate(JavaPojos.oneOfPojo(variant), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
