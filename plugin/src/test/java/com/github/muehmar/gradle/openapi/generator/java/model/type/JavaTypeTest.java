package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class JavaTypeTest {
  @Test
  void getImportsAsString_when_calledForMapType_then_noJavaLangImportsOrClassesFromSamePackage() {
    final MapType mapType =
        MapType.ofKeyAndValueType(
            StringType.noFormat(),
            ArrayType.ofItemType(
                StandardObjectType.ofName(pojoName("Object", "Dto")), NOT_NULLABLE));
    final JavaType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    assertEquals(PList.of("java.util.Map", "java.util.List"), javaType.getImportsAsString());
  }

  @Test
  void
      getWritableParameterizedClassName_when_stringConversionInMapType_then_returnCorrectClassName() {
    final TypeMappings stringTypeMapping =
        TypeMappings.ofClassTypeMappings(STRING_MAPPING_WITH_CONVERSION);

    final MapType mapType =
        MapType.ofKeyAndValueType(IntegerType.formatInteger(), StringType.noFormat());
    final JavaType javaType = JavaMapType.wrap(mapType, stringTypeMapping);

    assertEquals(
        "Map<Integer, CustomString>", javaType.getWriteableParameterizedClassName().asString());
  }

  @Test
  void
      getWritableParameterizedClassName_when_stringWithoutConversionInMapType_then_returnCorrectClassName() {
    final MapType mapType =
        MapType.ofKeyAndValueType(IntegerType.formatInteger(), StringType.noFormat());
    final JavaType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    assertEquals("Map<Integer, String>", javaType.getWriteableParameterizedClassName().asString());
  }

  @ParameterizedTest
  @EnumSource(Nullability.class)
  void isNullableValuesMapType_when_nullabilityForValueType_then_matchExpected(
      Nullability nullability) {
    final MapType mapType =
        MapType.ofKeyAndValueType(
            IntegerType.formatInteger(), StringType.noFormat().withNullability(nullability));

    final JavaType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    assertEquals(nullability.isNullable(), javaType.isNullableValuesMapType());
  }

  @ParameterizedTest
  @EnumSource(Nullability.class)
  void isNullableItemsArrayType_when_nullabilityForItems_then_matchExpected(
      Nullability nullability) {
    final ArrayType arrayType =
        ArrayType.ofItemType(StringType.noFormat().withNullability(nullability), NOT_NULLABLE);

    final JavaType javaType = JavaArrayType.wrap(arrayType, TypeMappings.empty());

    assertEquals(nullability.isNullable(), javaType.isNullableItemsArrayType());
  }
}
