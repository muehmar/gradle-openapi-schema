package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.PackageNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaBooleanType extends NonGenericJavaType {
  private static final QualifiedClassName JAVA_CLASS_NAME =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("Boolean"));
  private static final QualifiedClassName JAVA_PRIMITIVE =
      QualifiedClassName.ofPackageAndName(PackageNames.JAVA_LANG, Name.ofString("boolean"));

  private JavaBooleanType(QualifiedClassName className, Type type) {
    super(className, type);
  }

  public static JavaBooleanType wrap(TypeMappings typeMappings) {
    final QualifiedClassName className =
        JAVA_CLASS_NAME.mapWithClassMappings(typeMappings.getClassTypeMappings());
    return new JavaBooleanType(className, BooleanType.create());
  }

  public static JavaBooleanType create() {
    return new JavaBooleanType(JAVA_CLASS_NAME, BooleanType.create());
  }

  public static JavaBooleanType createPrimitive() {
    return new JavaBooleanType(JAVA_PRIMITIVE, BooleanType.create());
  }

  @Override
  public boolean isJavaArray() {
    return false;
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
