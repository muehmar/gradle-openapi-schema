package com.github.muehmar.gradle.openapi.generator.java.model.name;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;

/**
 * Representation of a fully parameterized class, i.e. contains all types of the type parameters (if
 * present).<br>
 * <br>
 * Parameterized classes like lists or maps contains also a value type, i.e. the type parameter
 * which denotes the type of the value in some sense. The type which is considered as value type
 * will always be the last type parameter. This means this class is sufficient for lists and maps
 * but not arbitrary parameterized classes which may not contain type parameters or are not the last
 * type parameter.<br>
 * <br>
 * Non-generic classes may be represented with this class too, they will just get rendered without
 * any type parameters.
 */
@EqualsAndHashCode
public class ParameterizedClassName {
  private final QualifiedClassName qualifiedClassName;
  private final PList<JavaType> genericTypes;
  private final Optional<JavaType> genericValueType;

  private ParameterizedClassName(
      QualifiedClassName qualifiedClassName,
      PList<JavaType> genericTypes,
      Optional<JavaType> genericValueType) {
    this.qualifiedClassName = qualifiedClassName;
    this.genericTypes = genericTypes;
    this.genericValueType = genericValueType;
  }

  public static ParameterizedClassName fromNonGenericClass(QualifiedClassName qualifiedClassName) {
    return new ParameterizedClassName(qualifiedClassName, PList.empty(), Optional.empty());
  }

  public static ParameterizedClassName fromGenericClass(
      QualifiedClassName qualifiedClassName, PList<JavaType> genericTypes) {
    return new ParameterizedClassName(
        qualifiedClassName,
        genericTypes.reverse().drop(1).reverse(),
        genericTypes.reverse().headOption());
  }

  private String asStringWithValueTypeAnnotations(
      Function<JavaType, String> createAnnotationsForValueType, RenderOption renderOption) {
    final Optional<String> annotatedValueType =
        genericValueType.map(
            type -> {
              final String typeFormat =
                  renderOption.equals(RenderOption.WRAPPING_NULLABLE_VALUE_TYPE)
                          && type.getNullability().isNullable()
                      ? "Optional<%s>"
                      : "%s";
              return String.format(
                      "%s " + typeFormat,
                      createAnnotationsForValueType.apply(type),
                      type.getParameterizedClassName())
                  .trim();
            });
    final PList<Name> formattedGenericTypes =
        this.genericTypes
            .map(JavaType::getParameterizedClassName)
            .map(ParameterizedClassName::asString)
            .concat(PList.fromOptional(annotatedValueType))
            .map(Name::ofString);
    return qualifiedClassName.getClassNameWithGenerics(formattedGenericTypes).asString();
  }

  public String asStringWithValueTypeAnnotations(
      Function<JavaType, String> createAnnotationsForValueType) {
    return asStringWithValueTypeAnnotations(createAnnotationsForValueType, RenderOption.DEFAULT);
  }

  public String asString() {
    return asStringWithValueTypeAnnotations(ignore -> "");
  }

  public String asStringWrappingNullableValueType() {
    return asStringWithValueTypeAnnotations(
        ignore -> "", RenderOption.WRAPPING_NULLABLE_VALUE_TYPE);
  }

  @Override
  public String toString() {
    return asString();
  }

  public enum RenderOption {
    DEFAULT,
    WRAPPING_NULLABLE_VALUE_TYPE
  }
}
