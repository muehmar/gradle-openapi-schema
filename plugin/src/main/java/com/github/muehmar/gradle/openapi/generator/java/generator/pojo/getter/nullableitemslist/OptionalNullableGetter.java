package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.nullableitemslist;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.CommonGetter.getterName;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.listmapping.MemberMapWriterBuilder.fullMemberMapWriterBuilder;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.GetterType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.GetterGroupsDefinition;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaGenerators;

class OptionalNullableGetter {
  private OptionalNullableGetter() {}

  public static Generator<JavaPojoMember, PojoSettings> optionalNullableGetter(
      GetterType getterType) {
    return tristateGetterMethod()
        .append(
            GetterGroupsDefinition.create()
                .generator()
                .filter(JavaPojoMember::isOptionalAndNullable))
        .filter(JavaPojoMember::isOptionalAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings> tristateGetterMethod() {
    final Generator<JavaPojoMember, PojoSettings> method =
        JavaGenerators.<JavaPojoMember, PojoSettings>methodGen()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(
                f ->
                    String.format(
                        "Tristate<%s>",
                        f.getJavaType()
                            .getParameterizedClassName()
                            .asStringWrappingNullableValueType()))
            .methodName(getterName())
            .noArguments()
            .doesNotThrow()
            .content(tristateGetterMethodContent())
            .build()
            .append(w -> w.ref(OpenApiUtilRefs.TRISTATE));
    return JacksonAnnotationGenerator.<JavaPojoMember>jsonIgnore().append(method);
  }

  private static Generator<JavaPojoMember, PojoSettings> tristateGetterMethodContent() {
    return (member, settings, writer) ->
        fullMemberMapWriterBuilder()
            .member(member)
            .prefix("return ")
            .mapListItemTypeNotNecessary()
            .wrapOptionalListItem()
            .mapListTypeNotNecessary()
            .wrapTristateList()
            .trailingSemicolon()
            .build();
  }
}
