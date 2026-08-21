package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConstructorConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaArrayTypeTest {
  @Test
  void wrap_when_arrayTypeWrapped_then_correctWrapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE);
    final JavaArrayType javaArrayType = JavaArrayType.wrap(arrayType, TypeMappings.empty());

    assertEquals(Optional.empty(), javaArrayType.getApiType());

    assertEquals("List<UUID>", javaArrayType.getParameterizedClassName().asString());
    assertEquals("List", javaArrayType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.util.List", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_arrayTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE);
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(
            arrayType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("List", "com.custom.CustomList", Optional.empty())));

    assertEquals(Optional.empty(), javaArrayType.getApiType());

    assertEquals("CustomList<UUID>", javaArrayType.getParameterizedClassName().asString());
    assertEquals("CustomList", javaArrayType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomList", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_arrayTypeWrappedWithClassMappingAndConversion_then_correctTypeMapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE);
    final TypeConversion typeConversion =
        new TypeConversion("toList", "com.custom.CustomList#fromList");
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("List", "com.custom.CustomList", Optional.of(typeConversion));
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(arrayType, TypeMappings.ofSingleClassTypeMapping(classTypeMapping));

    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomList");

    assertEquals(Optional.of(className), javaArrayType.getApiType().map(ApiType::getClassName));
    assertEquals(
        Optional.of("CustomList<UUID>"),
        javaArrayType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            PList.single(
                new ToApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getToCustomType())))),
        javaArrayType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            PList.single(
                new FromApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getFromCustomType())))),
        javaArrayType.getApiType().map(ApiType::getFromApiTypeConversion));

    assertEquals("List<UUID>", javaArrayType.getParameterizedClassName().asString());
    assertEquals("List", javaArrayType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomList", "java.util.List", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_arrayTypeWithUniqueItems_then_setTypeUsed() {
    final ArrayType arrayType =
        ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE)
            .withConstraints(Constraints.ofUniqueItems(true));
    final JavaArrayType javaArrayType = JavaArrayType.wrap(arrayType, TypeMappings.empty());

    // Internal type should remain List
    assertEquals("List<UUID>", javaArrayType.getParameterizedClassName().asString());
    assertEquals("List", javaArrayType.getQualifiedClassName().getClassName().asString());

    // ApiType should be Set
    assertTrue(javaArrayType.getApiType().isPresent());
    final ApiType apiType = javaArrayType.getApiType().get();
    assertEquals(QualifiedClassNames.SET, apiType.getClassName());
    assertEquals("Set<UUID>", apiType.getParameterizedClassName().asString());

    // Check conversions
    assertEquals(
        PList.single(
            new ToApiTypeConversion(
                ConversionMethod.ofConstructor(ConstructorConversion.conversionForSet()))),
        apiType.getToApiTypeConversion());
    assertEquals(
        PList.single(
            new FromApiTypeConversion(
                ConversionMethod.ofConstructor(ConstructorConversion.conversionForList()))),
        apiType.getFromApiTypeConversion());

    assertEquals(
        PList.of("java.util.List", "java.util.Set", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_arrayTypeWithoutUniqueItems_then_standardListUsed() {
    final ArrayType arrayType =
        ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE)
            .withConstraints(Constraints.ofUniqueItems(false));
    final JavaArrayType javaArrayType = JavaArrayType.wrap(arrayType, TypeMappings.empty());

    // No ApiType should be created for non-unique arrays
    assertEquals(Optional.empty(), javaArrayType.getApiType());
    assertEquals("List<UUID>", javaArrayType.getParameterizedClassName().asString());
  }

  @Test
  void wrap_when_arrayTypeWithUniqueItemsAndClassTypeMapping_then_combinedApiType() {
    final ArrayType arrayType =
        ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE)
            .withConstraints(Constraints.ofUniqueItems(true));
    final TypeConversion typeConversion =
        new TypeConversion("toSet", "com.custom.CustomSet#fromSet");
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("Set", "com.custom.CustomSet", Optional.of(typeConversion));
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(arrayType, TypeMappings.ofSingleClassTypeMapping(classTypeMapping));

    assertTrue(javaArrayType.getApiType().isPresent());
    final ApiType apiType = javaArrayType.getApiType().get();
    assertEquals(
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomSet"), apiType.getClassName());

    // Should NOT be Set
    assertEquals("CustomSet<UUID>", apiType.getParameterizedClassName().asString());
  }

  @Test
  void
      wrap_when_arrayTypeWithUniqueItemsAndClassTypeMappingForList_then_userMappingNotUsedAndSetRemainsAsApiType() {
    final ArrayType arrayType =
        ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE)
            .withConstraints(Constraints.ofUniqueItems(true));
    final TypeConversion typeConversion =
        new TypeConversion("toList", "com.custom.CustomList#fromList");
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("List", "com.custom.CustomList", Optional.of(typeConversion));
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(arrayType, TypeMappings.ofSingleClassTypeMapping(classTypeMapping));

    assertTrue(javaArrayType.getApiType().isPresent());
    final ApiType apiType = javaArrayType.getApiType().get();
    assertEquals(QualifiedClassName.ofQualifiedClassName("java.util.Set"), apiType.getClassName());

    assertEquals("Set<UUID>", apiType.getParameterizedClassName().asString());
  }

  @Test
  void
      wrap_when_arrayTypeWithUniqueItemsAndClassMappingWithoutConversion_then_useClassMappingType() {
    final ArrayType arrayType =
        ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE)
            .withConstraints(Constraints.ofUniqueItems(true));
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("Set", "com.custom.CustomSet", Optional.empty());
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(arrayType, TypeMappings.ofSingleClassTypeMapping(classTypeMapping));

    // User mapping without conversion should take precedence (no apiType created)
    assertEquals(Optional.empty(), javaArrayType.getApiType());
    assertEquals("CustomSet", javaArrayType.getQualifiedClassName().getClassName().asString());
    assertEquals("CustomSet<UUID>", javaArrayType.getParameterizedClassName().asString());
  }
}
