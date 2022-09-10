package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  public static final String ILLEGAL_FIELD_CHARACTERS_PATTERN = "[^A-Za-z0-9$_]";
  private final PList<String> members;

  private JavaEnumType(ClassName className, PList<String> members) {
    super(className);
    // FIXME: Create JavaEnumMemberName type
    this.members =
        members
            .map(JavaEnumType::toUppercaseSnakeCase)
            .map(JavaEnumType::toAsciiJavaName)
            .map(Name::asString);
  }

  public static JavaEnumType wrap(EnumType enumType) {
    final ClassName className = ClassName.ofName(enumType.getName());
    return new JavaEnumType(className, enumType.getMembers());
  }

  @Override
  public JavaType asPrimitive() {
    return this;
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
  }

  public PList<String> getMembers() {
    return members;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayType, T> onArrayType,
      Function<JavaBooleanType, T> onBooleanType,
      Function<JavaEnumType, T> onEnumType,
      Function<JavaMapType, T> onMapType,
      Function<JavaNoType, T> onNoType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return onEnumType.apply(this);
  }

  private static Name toUppercaseSnakeCase(String name) {
    if (name.toUpperCase().equals(name)) {
      return Name.ofString(name);
    }

    final String converted =
        name.trim()
            .replaceAll("([A-Z])", "_$1")
            .toUpperCase()
            .replaceFirst("^_", "")
            .replaceAll("_+", "_");
    return Name.ofString(converted);
  }

  private static Name toAsciiJavaName(Name fieldName) {
    return fieldName.map(
        str ->
            str.replaceAll(ILLEGAL_FIELD_CHARACTERS_PATTERN + "+", "_")
                .replaceAll("_+", "_")
                .replaceFirst("^([0-9])", "_$1"));
  }
}
