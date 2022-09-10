package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaEnumPojo implements JavaPojo {
  public static final String ILLEGAL_FIELD_CHARACTERS_PATTERN = "[^A-Za-z0-9$_]";
  private final PojoName name;
  private final String description;
  private final PList<String> members;

  private JavaEnumPojo(PojoName name, String description, PList<String> members) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    // FIXME: Create JavaEnumMemberName type
    this.members =
        members
            .map(JavaEnumPojo::toUppercaseSnakeCase)
            .map(JavaEnumPojo::toAsciiJavaName)
            .map(Name::asString);
  }

  public static JavaEnumPojo wrap(EnumPojo enumPojo) {
    return new JavaEnumPojo(enumPojo.getName(), enumPojo.getDescription(), enumPojo.getMembers());
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public PList<String> getMembers() {
    return members;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onEnumPojo.apply(this);
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
