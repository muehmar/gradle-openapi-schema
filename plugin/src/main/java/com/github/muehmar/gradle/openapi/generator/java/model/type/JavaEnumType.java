package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  private final PList<EnumConstantName> members;

  private JavaEnumType(
      QualifiedClassName className, PList<EnumConstantName> members, EnumType enumType) {
    super(className, enumType);
    this.members = members;
  }

  public static JavaEnumType wrap(EnumType enumType) {
    final QualifiedClassName className = QualifiedClassName.ofName(enumType.getName());
    return new JavaEnumType(
        className, enumType.getMembers().map(EnumConstantName::ofString), enumType);
  }

  public JavaEnumType asInnerClassOf(JavaIdentifier outerClassName) {
    return new JavaEnumType(
        qualifiedClassName.asInnerClassOf(outerClassName), members, (EnumType) getType());
  }

  @Override
  public boolean isJavaArray() {
    return false;
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
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
}
