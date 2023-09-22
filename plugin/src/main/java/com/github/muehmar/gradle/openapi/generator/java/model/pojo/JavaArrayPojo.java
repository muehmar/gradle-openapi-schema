package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherContentBuilder.fullWitherContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ConstructorContentBuilder.fullConstructorContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsContentBuilder.fullEqualsContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeContentBuilder.fullHashCodeContentBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringContentBuilder.fullToStringContentBuilder;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.WitherGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.PojoConstructorGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaMemberName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMemberBuilder;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaArrayPojo implements JavaPojo {
  private final JavaPojoName name;
  private final SchemaName schemaName;
  private final String description;
  private final JavaType itemType;
  private final Constraints constraints;
  private final JavaPojoMember arrayPojoMember;

  private JavaArrayPojo(
      JavaPojoName name,
      SchemaName schemaName,
      String description,
      JavaType itemType,
      Constraints constraints,
      JavaPojoMember arrayPojoMember) {
    this.name = name;
    this.schemaName = schemaName;
    this.description = Optional.ofNullable(description).orElse("");
    this.itemType = itemType;
    this.constraints = constraints;
    this.arrayPojoMember = arrayPojoMember;
  }

  public static JavaArrayPojo wrap(ArrayPojo arrayPojo, TypeMappings typeMappings) {
    final JavaType itemType = JavaType.wrap(arrayPojo.getItemType(), typeMappings);
    final JavaPojoMember arrayPojoMember = createItemTypeMember(arrayPojo, typeMappings);
    return new JavaArrayPojo(
        JavaPojoName.wrap(arrayPojo.getName()),
        arrayPojo.getName().getSchemaName(),
        arrayPojo.getDescription(),
        itemType,
        arrayPojo.getConstraints(),
        arrayPojoMember);
  }

  private static JavaPojoMember createItemTypeMember(
      ArrayPojo arrayPojo, TypeMappings typeMappings) {
    final ArrayType arrayType =
        ArrayType.ofItemType(arrayPojo.getItemType()).withConstraints(arrayPojo.getConstraints());
    final JavaArrayType javaArrayType = JavaArrayType.wrap(arrayType, typeMappings);
    final Name name = Name.ofString("value");
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(name))
        .description(arrayPojo.getDescription())
        .javaType(javaArrayType)
        .necessity(Necessity.REQUIRED)
        .nullability(Nullability.NOT_NULLABLE)
        .type(JavaPojoMember.MemberType.ARRAY_VALUE)
        .andAllOptionals()
        .build();
  }

  @Override
  public JavaName getSchemaName() {
    return JavaName.fromName(schemaName.asName());
  }

  @Override
  public JavaIdentifier getClassName() {
    return name.asIdentifier();
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public PojoType getType() {
    return PojoType.DEFAULT;
  }

  public JavaPojoMember getArrayPojoMember() {
    return arrayPojoMember;
  }

  public MemberGenerator.MemberContent getMemberContent() {
    return MemberContentBuilder.create()
        .isArrayPojo(true)
        .members(getArrayPojoMember().getTechnicalMembers())
        .build();
  }

  public HashCodeGenerator.HashCodeContent getHashCodeContent() {
    return fullHashCodeContentBuilder()
        .technicalPojoMembers(getArrayPojoMember().getTechnicalMembers())
        .build();
  }

  public EqualsGenerator.EqualsContent getEqualsContent() {
    return fullEqualsContentBuilder()
        .className(getClassName())
        .technicalPojoMembers(getArrayPojoMember().getTechnicalMembers())
        .build();
  }

  public ToStringGenerator.ToStringContent getToStringContent() {
    return fullToStringContentBuilder()
        .className(getClassName())
        .technicalPojoMembers(getArrayPojoMember().getTechnicalMembers())
        .build();
  }

  public PojoConstructorGenerator.ConstructorContent getConstructorContent() {
    return fullConstructorContentBuilder()
        .isArray(true)
        .className(getClassName())
        .members(getArrayPojoMember().getTechnicalMembers())
        .modifier(PUBLIC)
        .additionalProperties(Optional.empty())
        .build();
  }

  public WitherGenerator.WitherContent getWitherContent() {
    return fullWitherContentBuilder()
        .className(getClassName())
        .membersForWithers(PList.single(getArrayPojoMember()))
        .technicalPojoMembers(getArrayPojoMember().getTechnicalMembers())
        .build();
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onArrayPojo.apply(this);
  }

  public Constraints getConstraints() {
    return constraints;
  }
}
