package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.BuildMethod;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import lombok.Value;

@PojoBuilder(builderName = "SetterBuilder")
@Value
public class SetterBuilderImpl {
  SetterType type;
  Predicate<SetterMember> includeInBuilder;
  String typeFormat;
  UnaryOperator<Writer> addRefs;

  public enum SetterType {
    DEFAULT,
    NULLABLE_ITEMS_LIST
  }

  @BuildMethod
  public static Setter buildSetter(SetterBuilderImpl setter) {
    return new Setter() {
      @Override
      public Setter forType(SetterType setterType) {
        return SetterBuilder.fullSetterBuilder()
            .type(setterType)
            .includeInBuilder(setter.includeInBuilder)
            .typeFormat(setter.typeFormat)
            .addRefs(setter.addRefs)
            .build();
      }

      @Override
      public boolean includeInBuilder(SetterMember member) {
        return setter.includeInBuilder.test(member)
            && (setter.type.equals(SetterType.DEFAULT)
                || member.getMember().getJavaType().isNullableItemsArrayType());
      }

      @Override
      public String methodName(SetterMember member, PojoSettings settings) {
        if (setter.type.equals(SetterType.DEFAULT)) {
          return member
              .getMember()
              .prefixedMethodName(settings.getBuilderMethodPrefix())
              .asString();
        } else {
          return member
              .getMember()
              .prefixedMethodName(settings.getBuilderMethodPrefix())
              .asString()
              .concat("_");
        }
      }

      @Override
      public String argumentType(SetterMember member) {
        final String unwrappedArgumentType;
        if (setter.type.equals(SetterType.DEFAULT)) {
          unwrappedArgumentType =
              member.getMember().getJavaType().getParameterizedClassName().asString();
        } else {
          unwrappedArgumentType =
              member
                  .getMember()
                  .getJavaType()
                  .getParameterizedClassName()
                  .asStringWrappingNullableValueType();
        }
        return String.format(setter.typeFormat, unwrappedArgumentType);
      }

      @Override
      public Writer addRefs(Writer writer) {
        return setter.addRefs.apply(writer);
      }
    };
  }
}
