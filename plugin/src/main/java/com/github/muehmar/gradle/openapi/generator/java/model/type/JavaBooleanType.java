package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PackageNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.TypeMapping;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaBooleanType extends NonGenericJavaType {
  private static final QualifiedClassName INTERNAL_JAVA_CLASS_NAME =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Boolean"));
  private static final QualifiedClassName JAVA_PRIMITIVE =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("boolean"));

  private JavaBooleanType(
      QualifiedClassName className, Optional<ApiType> apiType, Nullability nullability) {
    super(className, apiType, nullability);
  }

  public static JavaBooleanType wrap(BooleanType booleanType, TypeMappings typeMappings) {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            INTERNAL_JAVA_CLASS_NAME,
            Optional.empty(),
            typeMappings.getClassTypeMappings(),
            PList.empty(),
            typeMappings.getTaskIdentifier());
    return new JavaBooleanType(
        typeMapping.getClassName(), typeMapping.getApiType(), booleanType.getNullability());
  }

  public static JavaBooleanType createPrimitive() {
    return new JavaBooleanType(JAVA_PRIMITIVE, Optional.empty(), NOT_NULLABLE);
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public JavaType withNullability(Nullability nullability) {
    return new JavaBooleanType(className, apiType, nullability);
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
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
    return onBooleanType.apply(this);
  }
}
