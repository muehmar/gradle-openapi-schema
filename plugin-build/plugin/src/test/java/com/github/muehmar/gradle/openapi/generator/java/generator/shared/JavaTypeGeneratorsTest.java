package com.github.muehmar.gradle.openapi.generator.java.generator.shared;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaTypeGenerators.deepAnnotatedParameterizedClassName;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.ValidationAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.PropertyInfoName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
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
    final Constraints itemTypeConstraints =
        Constraints.ofSize(Size.ofMin(5))
            .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern")));
    final StringType itemType = StringType.noFormat().withConstraints(itemTypeConstraints);
    final ArrayType arrayType = ArrayType.ofItemType(itemType, NOT_NULLABLE);

    final Writer writer = generateForType(arrayType);

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("nestedArrayType")
  void deepAnnotatedFullClassName_when_usedWithNestedArrayType_then_innerItemTypeAnnotated() {
    final IntegerType innerItemType =
        IntegerType.formatInteger().withConstraints(Constraints.ofMax(new Max(100)));
    final ArrayType innerArrayType = ArrayType.ofItemType(innerItemType, NOT_NULLABLE);
    final ArrayType arrayType = ArrayType.ofItemType(innerArrayType, NOT_NULLABLE);

    final Writer writer = generateForType(arrayType);

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("mapTypeWithArrayValueType")
  void deepAnnotatedFullClassName_when_usedWithMapOfArrays_then_innerItemTypeAnnotated() {
    final StringType itemType =
        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(5)));
    final ArrayType valueType = ArrayType.ofItemType(itemType, NOT_NULLABLE);
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), valueType);

    final Writer writer = generateForType(mapType);

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  private static Writer generateForType(Type type) {
    final Generator<ValidationAnnotationGenerator.PropertyType, PojoSettings> generator =
        deepAnnotatedParameterizedClassName();
    final JavaType javaType = JavaType.wrap(type, TypeMappings.empty());
    final PropertyInfoName propertyInfoName =
        PropertyInfoName.fromPojoNameAndMemberName(
            JavaPojoNames.invoiceName(), JavaName.fromString("arrayProperty"));
    final ValidationAnnotationGenerator.PropertyType propertyType =
        new ValidationAnnotationGenerator.PropertyType(propertyInfoName, javaType);

    return generator.generate(propertyType, defaultTestSettings(), javaWriter());
  }
}
