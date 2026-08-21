package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static org.junit.jupiter.api.Assertions.*;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ApiTypeTest {

  @Test
  void getToApiTypeConversion_when_pluginTypeOnly_then_returnsSinglePluginConversion() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final ApiType apiType = ApiType.ofPluginType(pluginApiType);

    final PList<ToApiTypeConversion> conversions = apiType.getToApiTypeConversion();

    assertEquals(PList.single(pluginApiType.getToApiTypeConversion()), conversions);
  }

  @Test
  void getToApiTypeConversion_when_userDefinedTypeOnly_then_returnsSingleUserConversion() {
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            QualifiedClassNames.STRING,
            ParameterizedApiClassName.ofClassNameAndGenerics(userClassName),
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("fromString")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));
    final ApiType apiType = ApiType.ofUserDefinedType(userDefinedApiType);

    final PList<ToApiTypeConversion> conversions = apiType.getToApiTypeConversion();

    assertEquals(PList.single(userDefinedApiType.getToApiTypeConversion()), conversions);
  }

  @Test
  void
      getToApiTypeConversion_when_bothPluginAndUserDefinedType_then_returnsPluginConversionFirstThenUserConversion() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            QualifiedClassNames.STRING,
            ParameterizedApiClassName.ofClassNameAndGenerics(userClassName),
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("of")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));

    final ApiType apiType = ApiType.of(userDefinedApiType, Optional.of(pluginApiType));

    final PList<ToApiTypeConversion> conversions = apiType.getToApiTypeConversion();

    assertEquals(
        PList.of(
            pluginApiType.getToApiTypeConversion(), userDefinedApiType.getToApiTypeConversion()),
        conversions);
  }

  @Test
  void getFromApiTypeConversion_when_pluginTypeOnly_then_returnsSinglePluginConversion() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final ApiType apiType = ApiType.ofPluginType(pluginApiType);

    final PList<FromApiTypeConversion> conversions = apiType.getFromApiTypeConversion();

    assertEquals(PList.single(pluginApiType.getFromApiTypeConversion()), conversions);
  }

  @Test
  void getFromApiTypeConversion_when_userDefinedTypeOnly_then_returnsSingleUserConversion() {
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            QualifiedClassNames.STRING,
            ParameterizedApiClassName.ofClassNameAndGenerics(userClassName),
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("fromString")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));
    final ApiType apiType = ApiType.ofUserDefinedType(userDefinedApiType);

    final PList<FromApiTypeConversion> conversions = apiType.getFromApiTypeConversion();

    assertEquals(PList.single(userDefinedApiType.getFromApiTypeConversion()), conversions);
  }

  @Test
  void
      getFromApiTypeConversion_when_bothPluginAndUserDefinedType_then_returnsUserConversionFirstThenPluginConversion() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            QualifiedClassNames.STRING,
            ParameterizedApiClassName.ofClassNameAndGenerics(userClassName),
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("of")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));

    final ApiType apiType = ApiType.of(userDefinedApiType, Optional.of(pluginApiType));

    final PList<FromApiTypeConversion> conversions = apiType.getFromApiTypeConversion();

    assertEquals(
        PList.of(
            userDefinedApiType.getFromApiTypeConversion(),
            pluginApiType.getFromApiTypeConversion()),
        conversions);
  }

  @Test
  void getClassName_when_pluginTypeOnly_then_returnsPluginClassName() {
    final ApiType apiType =
        ApiType.ofPluginType(PluginApiType.useSetForListType(JavaTypes.stringType()));

    final QualifiedClassName className = apiType.getClassName();

    assertEquals(QualifiedClassNames.SET, className);
  }

  @Test
  void getClassName_when_userDefinedTypeOnly_then_returnsUserDefinedClassName() {
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            userClassName,
            ParameterizedApiClassName.ofClassNameAndGenerics(userClassName),
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("of")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));

    final ApiType apiType = ApiType.ofUserDefinedType(userDefinedApiType);

    final QualifiedClassName className = apiType.getClassName();

    assertEquals(userClassName, className);
  }

  @Test
  void getClassName_when_bothPluginAndUserDefinedType_then_returnsUserDefinedClassName() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            userClassName,
            ParameterizedApiClassName.ofClassNameAndGenerics(userClassName),
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("of")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));

    final ApiType apiType = ApiType.of(userDefinedApiType, Optional.of(pluginApiType));

    final QualifiedClassName className = apiType.getClassName();

    assertEquals(
        userClassName,
        className,
        "When both types present, user-defined className should be returned");
  }

  @Test
  void getParameterizedClassName_when_pluginTypeOnly_then_returnsPluginParameterizedClassName() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final ApiType apiType = ApiType.ofPluginType(pluginApiType);

    final ParameterizedApiClassName parameterizedClassName = apiType.getParameterizedClassName();

    assertEquals(pluginApiType.getParameterizedClassName(), parameterizedClassName);
    assertEquals("Set<String>", parameterizedClassName.asString());
  }

  @Test
  void
      getParameterizedClassName_when_userDefinedTypeOnly_then_returnsUserDefinedParameterizedClassName() {
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final ParameterizedApiClassName userParamClassName =
        ParameterizedApiClassName.ofClassNameAndGenerics(userClassName);
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            userClassName,
            userParamClassName,
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("of")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));

    final ApiType apiType = ApiType.ofUserDefinedType(userDefinedApiType);

    final ParameterizedApiClassName parameterizedClassName = apiType.getParameterizedClassName();

    assertEquals(userParamClassName, parameterizedClassName);
  }

  @Test
  void
      getParameterizedClassName_when_bothPluginAndUserDefinedType_then_returnsUserDefinedParameterizedClassName() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.example.CustomType");
    final ParameterizedApiClassName userParamClassName =
        ParameterizedApiClassName.ofClassNameAndGenerics(
            userClassName, PList.single(JavaTypes.integerType()));
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            userClassName,
            userParamClassName,
            new ToApiTypeConversion(
                ConversionMethod.ofFactoryMethod(
                    new FactoryMethodConversion(userClassName, Name.ofString("of")))),
            new FromApiTypeConversion(
                ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("toString"))));

    final ApiType apiType = ApiType.of(userDefinedApiType, Optional.of(pluginApiType));

    final ParameterizedApiClassName parameterizedClassName = apiType.getParameterizedClassName();

    assertEquals(
        userParamClassName,
        parameterizedClassName,
        "When both types present, user-defined parameterizedClassName should be returned");
  }
}
