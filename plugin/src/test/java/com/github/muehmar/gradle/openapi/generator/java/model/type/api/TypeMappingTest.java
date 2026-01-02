package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static com.github.muehmar.gradle.openapi.generator.settings.DtoMappings.DTO_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.DtoMappings.DTO_MAPPING_WITHOUT_CONVERSION;
import static com.github.muehmar.gradle.openapi.generator.settings.DtoMappings.DTO_MAPPING_WITH_CONVERSION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.DtoMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TypeMappingTest {

  private static final ClassTypeMapping STRING_MAPPING_WITHOUT_CONVERSION =
      new ClassTypeMapping("String", "com.custom.CustomString", Optional.empty());

  private static final TypeConversion STRING_CONVERSION =
      new TypeConversion("toString", "com.custom.CustomString#fromString");
  private static final ClassTypeMapping STRING_MAPPING_WITH_CONVERSION =
      new ClassTypeMapping("String", "com.custom.CustomString", Optional.of(STRING_CONVERSION));

  private static final FormatTypeMapping STRING_FORMAT_MAPPING_WITHOUT_CONVERSION =
      new FormatTypeMapping("id", "com.custom.Id", Optional.empty());

  private static final TypeConversion STRING_ID_CONVERSION =
      new TypeConversion("toString", "com.custom.Id#fromString");
  private static final FormatTypeMapping STRING_FORMAT_MAPPING_WITH_CONVERSION =
      new FormatTypeMapping("id", "com.custom.Id", Optional.of(STRING_ID_CONVERSION));

  @Test
  void fromClassMappings_when_wrongMapping_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.LONG,
            Optional.empty(),
            PList.of(STRING_MAPPING_WITH_CONVERSION),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.LONG, typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingWithoutConversion_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.STRING,
            Optional.empty(),
            PList.of(STRING_MAPPING_WITHOUT_CONVERSION),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomString"),
        typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingWithConversion_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.STRING,
            Optional.empty(),
            PList.of(STRING_MAPPING_WITH_CONVERSION),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    final ApiType expectedApiType =
        ApiType.ofUserDefinedType(
            UserDefinedApiType.fromConversion(
                QualifiedClassName.ofQualifiedClassName("com.custom.CustomString"),
                STRING_CONVERSION,
                PList.empty()));
    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingWithConversionAndGenerics_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.STRING,
            Optional.empty(),
            PList.of(STRING_MAPPING_WITH_CONVERSION),
            PList.of(JavaTypes.integerType()),
            TaskIdentifier.fromString("test"));
    final ApiType expectedApiType =
        ApiType.ofUserDefinedType(
            UserDefinedApiType.fromConversion(
                QualifiedClassName.ofQualifiedClassName("com.custom.CustomString"),
                STRING_CONVERSION,
                PList.of(JavaTypes.integerType())));
    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void
      fromClassMappings_when_mappingWithoutConversionAndPluginApiType_then_useClassMappingWithoutPluginApiType() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());

    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("Set", "com.custom.CustomSet", Optional.empty());

    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.LIST,
            Optional.of(pluginApiType),
            PList.of(classTypeMapping),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomSet"),
        typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingPresentButDoesNotMatchPluginApiType_then_usePluginApiType() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());

    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("List", "com.custom.CustomList", Optional.empty());

    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.LIST,
            Optional.of(pluginApiType),
            PList.of(classTypeMapping),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.of(ApiType.ofPluginType(pluginApiType)), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.LIST, typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingAndPluginApiTypeWithConversion_then_combinedApiType() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());

    final TypeConversion typeConversion =
        new TypeConversion("toSet", "com.custom.CustomList#fromSet");
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("Set", "com.custom.CustomSet", Optional.of(typeConversion));

    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.LIST,
            Optional.of(pluginApiType),
            PList.of(classTypeMapping),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    final UserDefinedApiType expectedUserDefinedType =
        UserDefinedApiType.fromConversion(
            QualifiedClassName.ofQualifiedClassName("com.custom.CustomSet"),
            typeConversion,
            PList.empty());
    final ApiType expectedApiType = ApiType.of(expectedUserDefinedType, Optional.of(pluginApiType));

    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.LIST, typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_wrongMapping_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            Optional.empty(),
            "key",
            PList.of(STRING_FORMAT_MAPPING_WITHOUT_CONVERSION),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_mappingWithoutConversion_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            Optional.empty(),
            "id",
            PList.of(STRING_FORMAT_MAPPING_WITHOUT_CONVERSION),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(
        QualifiedClassName.ofQualifiedClassName("com.custom.Id"), typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_mappingWithConversion_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            Optional.empty(),
            "id",
            PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    final ApiType expectedApiType =
        ApiType.ofUserDefinedType(
            UserDefinedApiType.fromConversion(
                QualifiedClassName.ofQualifiedClassName("com.custom.Id"),
                STRING_ID_CONVERSION,
                PList.empty()));

    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_mappingWithConversionAndGenerics_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            Optional.empty(),
            "id",
            PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
            PList.of(JavaTypes.integerType()),
            TaskIdentifier.fromString("test"));

    final ApiType expectedApiType =
        ApiType.ofUserDefinedType(
            UserDefinedApiType.fromConversion(
                QualifiedClassName.ofQualifiedClassName("com.custom.Id"),
                STRING_ID_CONVERSION,
                PList.of(JavaTypes.integerType())));

    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void
      fromFormatMappings_when_mappingWithoutConversionAndPluginApiType_then_useFormatMappingWithoutPluginApiType() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping("id", "com.custom.Id", Optional.empty());
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            Optional.of(pluginApiType),
            "id",
            PList.of(formatTypeMapping),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());

    assertEquals(
        QualifiedClassName.ofQualifiedClassName("com.custom.Id"), typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_mappingAndPluginApiTypeWithConversion_then_combinedApiType() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());

    final TypeConversion typeConversion = new TypeConversion("toId", "com.custom.Id#fromId");
    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping("id", "com.custom.Id", Optional.of(typeConversion));

    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            Optional.of(pluginApiType),
            "id",
            PList.of(formatTypeMapping),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    final UserDefinedApiType expectedUserDefinedType =
        UserDefinedApiType.fromConversion(
            QualifiedClassName.ofQualifiedClassName("com.custom.Id"),
            typeConversion,
            PList.empty());
    final ApiType expectedApiType = ApiType.of(expectedUserDefinedType, Optional.of(pluginApiType));

    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void
      fromFormatMappings_when_pluginApiTypeAndMappingPresentButDoesNotMatchFormat_then_usePluginApiType() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping("uuid", "com.custom.Id", Optional.empty());
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            Optional.of(pluginApiType),
            "id",
            PList.of(formatTypeMapping),
            PList.empty(),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.of(ApiType.ofPluginType(pluginApiType)), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromDtoMappings_when_wrongMapping_then_noApiTypeAndCorrectClassName() {
    final QualifiedClassName adminDtoClassName = QualifiedClassName.ofName("AdminDto");
    final TypeMapping typeMapping =
        TypeMapping.fromDtoMappings(
            adminDtoClassName,
            PList.of(DTO_MAPPING_WITH_CONVERSION),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(adminDtoClassName, typeMapping.getClassName());
  }

  @Test
  void fromDtoMappings_when_mappingWithoutConversion_then_noApiTypeAndCorrectClassName() {
    final QualifiedClassName userDtoClassName = QualifiedClassName.ofName("UserDto");
    final TypeMapping typeMapping =
        TypeMapping.fromDtoMappings(
            userDtoClassName,
            PList.of(DTO_MAPPING_WITHOUT_CONVERSION),
            TaskIdentifier.fromString("test"));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(
        QualifiedClassName.ofQualifiedClassName(DTO_MAPPING_WITHOUT_CONVERSION.getCustomType()),
        typeMapping.getClassName());
  }

  @Test
  void fromDtoMappings_when_mappingWithConversion_then_correctApiTypeAndClassName() {
    final QualifiedClassName userDtoClassName = QualifiedClassName.ofName("UserDto");
    final TypeMapping typeMapping =
        TypeMapping.fromDtoMappings(
            userDtoClassName,
            PList.of(DTO_MAPPING_WITH_CONVERSION),
            TaskIdentifier.fromString("test"));

    final ApiType expectedApiType =
        ApiType.ofUserDefinedType(
            UserDefinedApiType.fromConversion(
                QualifiedClassName.ofQualifiedClassName(
                    DTO_MAPPING_WITHOUT_CONVERSION.getCustomType()),
                DTO_CONVERSION,
                PList.empty()));

    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(userDtoClassName, typeMapping.getClassName());
  }

  @ParameterizedTest
  @MethodSource("typeMappings")
  void or_when_typeMappings_then_returnExpectedMapping(
      TypeMapping typeMapping1,
      TypeMapping typeMapping2,
      QualifiedClassName originalClassName,
      TypeMapping expectedTypeMapping) {
    final TypeMapping resultingMapping = typeMapping1.or(typeMapping2, originalClassName);

    assertEquals(expectedTypeMapping, resultingMapping);
  }

  public static Stream<Arguments> typeMappings() {
    final TypeMapping longMapping = new TypeMapping(QualifiedClassNames.LONG, Optional.empty());
    final TypeMapping stringMapping = new TypeMapping(QualifiedClassNames.STRING, Optional.empty());

    final ApiType apiType =
        ApiType.ofUserDefinedType(
            UserDefinedApiType.fromConversion(
                QualifiedClassName.ofQualifiedClassName("com.custom.Id"),
                STRING_ID_CONVERSION,
                PList.empty()));

    final TypeMapping stringMappingApiType =
        new TypeMapping(QualifiedClassNames.STRING, Optional.of(apiType));

    return Stream.of(
        arguments(longMapping, stringMapping, QualifiedClassNames.STRING, longMapping),
        arguments(stringMapping, longMapping, QualifiedClassNames.STRING, longMapping),
        arguments(
            stringMappingApiType, longMapping, QualifiedClassNames.STRING, stringMappingApiType),
        arguments(
            stringMappingApiType, longMapping, QualifiedClassNames.LONG, stringMappingApiType),
        arguments(
            longMapping, stringMappingApiType, QualifiedClassNames.STRING, stringMappingApiType),
        arguments(
            longMapping, stringMappingApiType, QualifiedClassNames.LONG, stringMappingApiType));
  }

  @Test
  void fromClassMappings_when_mappingMatches_then_recordUsageInContext() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    TypeMapping.fromClassMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        PList.of(STRING_MAPPING_WITH_CONVERSION),
        PList.empty(),
        taskId);

    final Set<ClassTypeMapping> usedMappings = UsedMappingsContext.getUsedClassMappings(taskId);

    assertEquals(Set.of(STRING_MAPPING_WITH_CONVERSION), usedMappings);
  }

  @Test
  void fromClassMappings_when_mappingDoesNotMatch_then_doNotRecordUsage() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    TypeMapping.fromClassMappings(
        QualifiedClassNames.LONG,
        Optional.empty(),
        PList.of(STRING_MAPPING_WITH_CONVERSION),
        PList.empty(),
        taskId);

    final Set<ClassTypeMapping> usedMappings = UsedMappingsContext.getUsedClassMappings(taskId);

    assertEquals(Set.of(), usedMappings);
  }

  @Test
  void fromClassMappings_when_mappingWithoutConversion_then_stillRecorded() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    TypeMapping.fromClassMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        PList.of(STRING_MAPPING_WITHOUT_CONVERSION),
        PList.empty(),
        taskId);

    final Set<ClassTypeMapping> usedMappings = UsedMappingsContext.getUsedClassMappings(taskId);

    assertEquals(Set.of(STRING_MAPPING_WITHOUT_CONVERSION), usedMappings);
  }

  @Test
  void fromFormatMappings_when_formatMatches_then_recordUsageInContext() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    TypeMapping.fromFormatMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        "id",
        PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
        PList.empty(),
        taskId);

    final Set<FormatTypeMapping> usedMappings = UsedMappingsContext.getUsedFormatMappings(taskId);

    assertEquals(Set.of(STRING_FORMAT_MAPPING_WITH_CONVERSION), usedMappings);
  }

  @Test
  void fromFormatMappings_when_formatDoesNotMatch_then_doNotRecordUsage() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    TypeMapping.fromFormatMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        "uuid",
        PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
        PList.empty(),
        taskId);

    final Set<FormatTypeMapping> usedMappings = UsedMappingsContext.getUsedFormatMappings(taskId);

    assertEquals(Set.of(), usedMappings);
  }

  @Test
  void fromDtoMappings_when_dtoMatches_then_recordUsageInContext() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());
    final QualifiedClassName userDtoClassName = QualifiedClassName.ofName("UserDto");

    TypeMapping.fromDtoMappings(userDtoClassName, PList.of(DTO_MAPPING_WITH_CONVERSION), taskId);

    final Set<DtoMapping> usedMappings = UsedMappingsContext.getUsedDtoMappings(taskId);

    assertEquals(Set.of(DTO_MAPPING_WITH_CONVERSION), usedMappings);
  }

  @Test
  void fromDtoMappings_when_dtoDoesNotMatch_then_doNotRecordUsage() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());
    final QualifiedClassName adminDtoClassName = QualifiedClassName.ofName("AdminDto");

    TypeMapping.fromDtoMappings(adminDtoClassName, PList.of(DTO_MAPPING_WITH_CONVERSION), taskId);

    final Set<DtoMapping> usedMappings = UsedMappingsContext.getUsedDtoMappings(taskId);

    assertEquals(Set.of(), usedMappings);
  }

  @Test
  void multipleMappings_when_sameTask_then_allRecordedSeparately() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());

    TypeMapping.fromClassMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        PList.of(STRING_MAPPING_WITH_CONVERSION),
        PList.empty(),
        taskId);

    TypeMapping.fromFormatMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        "id",
        PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
        PList.empty(),
        taskId);

    final QualifiedClassName userDtoClassName = QualifiedClassName.ofName("UserDto");
    TypeMapping.fromDtoMappings(userDtoClassName, PList.of(DTO_MAPPING_WITH_CONVERSION), taskId);

    assertEquals(
        Set.of(STRING_MAPPING_WITH_CONVERSION), UsedMappingsContext.getUsedClassMappings(taskId));
    assertEquals(
        Set.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
        UsedMappingsContext.getUsedFormatMappings(taskId));
    assertEquals(
        Set.of(DTO_MAPPING_WITH_CONVERSION), UsedMappingsContext.getUsedDtoMappings(taskId));
  }

  @Test
  void multipleTasks_when_differentTaskIdentifiers_then_mappingsIsolated() {
    final TaskIdentifier task1 = TaskIdentifier.fromString(UUID.randomUUID().toString());
    final TaskIdentifier task2 = TaskIdentifier.fromString(UUID.randomUUID().toString());

    TypeMapping.fromClassMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        PList.of(STRING_MAPPING_WITH_CONVERSION),
        PList.empty(),
        task1);

    TypeMapping.fromFormatMappings(
        QualifiedClassNames.STRING,
        Optional.empty(),
        "id",
        PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
        PList.empty(),
        task2);

    assertEquals(
        Set.of(STRING_MAPPING_WITH_CONVERSION), UsedMappingsContext.getUsedClassMappings(task1));
    assertEquals(Set.of(), UsedMappingsContext.getUsedFormatMappings(task1));

    assertEquals(Set.of(), UsedMappingsContext.getUsedClassMappings(task2));
    assertEquals(
        Set.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
        UsedMappingsContext.getUsedFormatMappings(task2));
  }

  @Test
  void fromClassMappings_when_pluginApiTypeAndMapping_then_recordOnlyIfMappingMatches() {
    final TaskIdentifier taskId = TaskIdentifier.fromString(UUID.randomUUID().toString());
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());

    final TypeConversion typeConversion =
        new TypeConversion("toSet", "com.custom.CustomList#fromSet");
    final ClassTypeMapping setMapping =
        new ClassTypeMapping("Set", "com.custom.CustomSet", Optional.of(typeConversion));

    TypeMapping.fromClassMappings(
        QualifiedClassNames.LIST,
        Optional.of(pluginApiType),
        PList.of(setMapping),
        PList.empty(),
        taskId);

    final Set<ClassTypeMapping> usedMappings = UsedMappingsContext.getUsedClassMappings(taskId);

    assertEquals(Set.of(setMapping), usedMappings);
  }
}
