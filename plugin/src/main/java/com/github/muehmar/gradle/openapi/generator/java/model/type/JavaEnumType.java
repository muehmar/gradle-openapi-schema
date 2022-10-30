package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaEnumType extends NonGenericJavaType {
  private final PList<EnumConstantName> members;

  private JavaEnumType(ClassName className, PList<EnumConstantName> members) {
    super(className);
    this.members = members;
  }

  public static JavaEnumType wrap(EnumType enumType) {
    final ClassName className = ClassName.ofName(enumType.getName());
    return new JavaEnumType(className, enumType.getMembers().map(EnumConstantName::ofString));
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
      Function<JavaNoType, T> onNoType,
      Function<JavaNumericType, T> onNumericType,
      Function<JavaIntegerType, T> onIntegerType,
      Function<JavaObjectType, T> onObjectType,
      Function<JavaStringType, T> onStringType) {
    return onEnumType.apply(this);
  }
}
