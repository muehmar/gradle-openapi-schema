package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.util.OneOrBoth;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ApiType {
  private final OneOrBoth<PluginApiType, UserDefinedApiType> type;

  private ApiType(OneOrBoth<PluginApiType, UserDefinedApiType> type) {
    this.type = type;
  }

  public static ApiType ofPluginType(PluginApiType pluginApiType) {
    return new ApiType(OneOrBoth.ofFirst(pluginApiType));
  }

  public static ApiType ofUserDefinedType(UserDefinedApiType userDefinedApiType) {
    return new ApiType(OneOrBoth.ofSecond(userDefinedApiType));
  }

  public static ApiType of(
      UserDefinedApiType userDefinedApiType, Optional<PluginApiType> pluginApiType) {
    return new ApiType(OneOrBoth.ofBothOptional(pluginApiType, userDefinedApiType));
  }

  public QualifiedClassName getClassName() {
    return fold(
        PluginApiType::getClassName,
        UserDefinedApiType::getClassName,
        (pluginType, userDefinedType) -> userDefinedType.getClassName());
  }

  public ParameterizedApiClassName getParameterizedClassName() {
    return fold(
        PluginApiType::getParameterizedClassName,
        UserDefinedApiType::getParameterizedClassName,
        (pluginType, userDefinedType) -> userDefinedType.getParameterizedClassName());
  }

  public PList<ToApiTypeConversion> getToApiTypeConversion() {
    return fold(
        pluginApiType -> PList.single(pluginApiType.getToApiTypeConversion()),
        userDefinedApiType -> PList.single(userDefinedApiType.getToApiTypeConversion()),
        (pluginType, userDefinedType) ->
            PList.of(
                pluginType.getToApiTypeConversion(), userDefinedType.getToApiTypeConversion()));
  }

  public PList<FromApiTypeConversion> getFromApiTypeConversion() {
    return fold(
        pluginApiType -> PList.single(pluginApiType.getFromApiTypeConversion()),
        userDefinedApiType -> PList.single(userDefinedApiType.getFromApiTypeConversion()),
        (pluginType, userDefinedType) ->
            PList.of(
                userDefinedType.getFromApiTypeConversion(), pluginType.getFromApiTypeConversion()));
  }

  public <T> T fold(
      Function<PluginApiType, T> onPluginType,
      Function<UserDefinedApiType, T> onUserDefinedType,
      BiFunction<PluginApiType, UserDefinedApiType, T> onPluginAndUserDefinedType) {
    return type.fold(onPluginType, onUserDefinedType, onPluginAndUserDefinedType);
  }
}
