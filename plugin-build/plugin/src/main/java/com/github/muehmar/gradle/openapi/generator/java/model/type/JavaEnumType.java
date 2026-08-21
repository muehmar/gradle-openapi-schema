package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator.EnumContent;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.PluginApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  private final QualifiedClassName enumClassName;
  private final PList<EnumConstantName> members;
  private final Constraints constraints;
  private final TypeLevel level;

  private JavaEnumType(
      QualifiedClassName className,
      Optional<ApiType> apiType,
      QualifiedClassName enumClassName,
      PList<EnumConstantName> members,
      Nullability nullability,
      Constraints constraints,
      TypeLevel level) {
    super(className, apiType, nullability);
    this.enumClassName = enumClassName;
    this.members = members;
    this.constraints = constraints;
    this.level = level;
  }

  /**
   * The single place a {@link JavaEnumType} is constructed: an enum is ALWAYS represented
   * internally as a {@code String} (with the enum itself or a mapped custom type as api type). An
   * enum mapped to a custom type WITHOUT conversion is not represented as {@link JavaEnumType} at
   * all — see the fallback to {@link JavaObjectType} in the wrap methods.
   */
  private static JavaEnumType stringBacked(
      QualifiedClassName enumClassName,
      PList<EnumConstantName> members,
      Nullability nullability,
      Optional<ApiType> apiType,
      Constraints constraints,
      TypeLevel level) {
    return new JavaEnumType(
        QualifiedClassNames.STRING,
        apiType,
        enumClassName,
        members,
        nullability,
        constraints,
        level);
  }

  /**
   * Wraps the enum type of a discriminator property, which is used to determine the value of the
   * discriminator for a specific schema only. In contrast to {@link #wrap(EnumType, TypeMappings)},
   * no member-values pattern constraint is attached, as this type never gets validated itself: the
   * property of the dto is wrapped by {@link #wrap(EnumType, TypeMappings)} like any other member.
   * Do NOT use this method to create the type of a property.
   */
  public static JavaEnumType wrapForDiscriminator(EnumType enumType) {
    final QualifiedClassName enumClassName = QualifiedClassName.ofName(enumType.getName());
    return stringBacked(
        enumClassName,
        enumType.getMembers().map(EnumConstantName::ofString),
        Nullability.NOT_NULLABLE,
        Optional.of(ApiType.ofPluginType(PluginApiType.useEnumAsApiType(enumClassName))),
        Constraints.empty(),
        TypeLevel.INLINE);
  }

  public static NonGenericJavaType wrap(EnumType enumType, TypeMappings typeMappings) {
    final QualifiedClassName enumClassName = QualifiedClassName.ofName(enumType.getName());
    final Optional<PluginApiType> pluginApiType =
        Optional.of(PluginApiType.useEnumAsApiType(enumClassName));
    final TypeMapping typeMapping =
        enumType
            .getFormat()
            .map(
                format ->
                    TypeMapping.fromFormatMappings(
                        QualifiedClassNames.STRING,
                        pluginApiType,
                        format,
                        typeMappings.getFormatTypeMappings()))
            .orElseGet(
                () ->
                    TypeMapping.fromClassNameAndPluginApiType(
                        QualifiedClassNames.STRING, pluginApiType));

    final Nullability nullability =
        Nullability.leastRestrictive(
            enumType.getNullability(),
            typeMappings.isAllowNullableForEnums()
                ? enumType.getLegacyNullability()
                : Nullability.NOT_NULLABLE);

    return fromResolvedTypeMapping(
        enumClassName, enumType.getMembers(), nullability, typeMapping, TypeLevel.INLINE);
  }

  /**
   * Builds the java type for an enum whose type mapping is already resolved: falls back to a plain
   * object type when a mapping without conversion replaced the enum entirely, otherwise builds the
   * string-backed enum. A mapping without conversion is the only case where the api type is absent,
   * as the enum itself is used as api type for its {@code String} representation otherwise. Neither
   * the member-value pattern constraint nor a nested enum class apply to the replacing custom type,
   * even if the enum is mapped to a {@code String} as well.
   */
  private static NonGenericJavaType fromResolvedTypeMapping(
      QualifiedClassName enumClassName,
      PList<String> memberValues,
      Nullability nullability,
      TypeMapping typeMapping,
      TypeLevel level) {
    if (not(typeMapping.getApiType().isPresent())) {
      return JavaObjectType.fromClassNameAndNullability(typeMapping.getClassName(), nullability);
    }

    return stringBacked(
        enumClassName,
        memberValues.map(EnumConstantName::ofString),
        nullability,
        typeMapping.getApiType(),
        memberValuesPatternConstraint(memberValues),
        level);
  }

  private static Constraints memberValuesPatternConstraint(PList<String> memberValues) {
    final String pattern = memberValues.map(JavaEnumType::escapeRegexMetacharacters).mkString("|");
    return Constraints.ofPattern(Pattern.ofUnescapedString(pattern));
  }

  private static String escapeRegexMetacharacters(String value) {
    return value.replaceAll("([\\\\.\\[\\]{}()*+?^$|])", "\\\\$1");
  }

  /**
   * Wraps a referenced ({@code $ref}) enum which is generated as its own top-level DTO. In contrast
   * to {@link #wrap(EnumType, TypeMappings)}, the enum body is not generated as a nested class (see
   * {@link TypeLevel#TOP_LEVEL}) and a configured {@code dtoMapping} (keyed by the enum's class
   * name) may redirect it to a custom type.
   */
  public static NonGenericJavaType wrapAsObjectType(
      EnumObjectType enumObjectType, TypeMappings typeMappings) {
    final QualifiedClassName enumClassName =
        QualifiedClassName.ofPojoName(enumObjectType.getName());
    final TypeMapping typeMapping =
        TypeMapping.fromDtoMappings(
            enumClassName,
            QualifiedClassNames.STRING,
            Optional.of(PluginApiType.useEnumAsApiType(enumClassName)),
            typeMappings.getDtoMappings());

    return fromResolvedTypeMapping(
        enumClassName,
        enumObjectType.getMembers(),
        enumObjectType.getNullability(),
        typeMapping,
        TypeLevel.TOP_LEVEL);
  }

  public JavaEnumType asInnerClassOf(JavaName outerClassName) {
    if (level == TypeLevel.TOP_LEVEL) {
      return this;
    }

    final QualifiedClassName newEnumClassName = enumClassName.asInnerClassOf(outerClassName);

    final PluginApiType pluginApiType = PluginApiType.useEnumAsApiType(newEnumClassName);
    final Optional<ApiType> newApiType =
        apiType.map(
            api ->
                api.fold(
                    plugin -> ApiType.ofPluginType(pluginApiType),
                    ApiType::ofUserDefinedType,
                    (plugin, userDefined) -> ApiType.of(userDefined, pluginApiType)));

    return new JavaEnumType(
        className, newApiType, newEnumClassName, members, getNullability(), constraints, level);
  }

  public QualifiedClassName getEnumClassName() {
    return enumClassName;
  }

  /**
   * The content of the enum class generated as nested class within the referencing DTO. Empty for a
   * top-level enum, which is generated as its own DTO and must not be emitted a second time as
   * nested class.
   */
  public Optional<EnumContent> getNestedEnumContent(String description) {
    if (level == TypeLevel.TOP_LEVEL) {
      return Optional.empty();
    }
    return Optional.of(
        EnumContentBuilder.create()
            .className(JavaName.fromName(enumClassName.getClassName()))
            .description(description)
            .members(members)
            .build());
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public JavaEnumType withNullability(Nullability nullability) {
    return new JavaEnumType(
        className, apiType, enumClassName, members, nullability, constraints, level);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  public PList<EnumConstantName> getMembers() {
    return members;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaAnyType, T> onAnyType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return onEnumType.apply(this);
  }

  /** Whether the enum is generated inline (nested) or as its own top-level DTO. */
  public enum TypeLevel {
    INLINE,
    TOP_LEVEL
  }
}
