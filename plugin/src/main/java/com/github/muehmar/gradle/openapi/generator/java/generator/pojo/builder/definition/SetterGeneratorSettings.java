package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.definition.SetterGeneratorSetting.S_NULLABLE_CONTAINER_VALUE;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.TRISTATE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter.FlagAssignments;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import lombok.Value;

@Value
public class SetterGeneratorSettings {
  PList<SetterGeneratorSetting> settings;

  public static SetterGeneratorSettings empty() {
    return new SetterGeneratorSettings(PList.empty());
  }

  public boolean isNullableContainerValue() {
    return settings.exists(S_NULLABLE_CONTAINER_VALUE::equals);
  }

  public boolean isOptionalSetter() {
    return settings.exists(SetterGeneratorSetting.S_OPTIONAL_SETTER::equals);
  }

  public boolean isTristateSetter() {
    return settings.exists(SetterGeneratorSetting.S_TRISTATE_SETTER::equals);
  }

  public Generator<JavaPojoMember, PojoSettings> flagAssigment() {
    return (isOptionalSetter() || isTristateSetter())
        ? FlagAssignments.forWrappedMemberSetter()
        : FlagAssignments.forStandardMemberSetter();
  }

  public String typeFormat() {
    if (isTristateSetter()) {
      return "Tristate<%s>";
    } else if (isOptionalSetter()) {
      return "Optional<%s>";
    } else {
      return "%s";
    }
  }

  public Generator<JavaPojoMember, PojoSettings> wrappingRefs() {
    return wrapperTypeRef().append(nullableContainerValueRef());
  }

  private Generator<JavaPojoMember, PojoSettings> wrapperTypeRef() {
    if (isTristateSetter()) {
      return ref(TRISTATE);
    } else if (isOptionalSetter()) {
      return ref(JAVA_UTIL_OPTIONAL);
    } else {
      return Generator.emptyGen();
    }
  }

  private Generator<JavaPojoMember, PojoSettings> nullableContainerValueRef() {
    if (isNullableContainerValue()) {
      return ref(JAVA_UTIL_OPTIONAL);
    } else {
      return Generator.emptyGen();
    }
  }
}
