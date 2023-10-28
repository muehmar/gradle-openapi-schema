package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.function.Function;
import lombok.Value;

public class ConversionMethodGenerator {
  private ConversionMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> conversionMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(asSingleDtoMethod(), ConversionMethodGenerator::getComposedPojos, newLine());
  }

  private static Iterable<ParentAndComposedPojo> getComposedPojos(JavaObjectPojo pojo) {
    return PList.of(pojo.getAllOfComposition().map(JavaAllOfComposition::getPojos))
        .add(pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos))
        .add(pojo.getAnyOfComposition().map(JavaAnyOfComposition::getPojos))
        .flatMapOptional(Function.identity())
        .flatMap(NonEmptyList::toPList)
        .map(composedPojo -> new ParentAndComposedPojo(pojo, composedPojo));
  }

  private static Generator<ParentAndComposedPojo, PojoSettings> asSingleDtoMethod() {
    return MethodGenBuilder.<ParentAndComposedPojo, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(pc -> pc.getComposedPojo().getClassName().asString())
        .methodName(
            pc -> MethodNames.Composition.asConversionMethodName(pc.getComposedPojo()).asString())
        .noArguments()
        .content(asDtoMethodContent())
        .build();
  }

  private static Generator<ParentAndComposedPojo, PojoSettings> asDtoMethodContent() {
    final Generator<JavaName, PojoSettings> memberGen = (name, s, w) -> w.println("%s,", name);
    return Generator.<ParentAndComposedPojo, PojoSettings>emptyGen()
        .append(
            constant("Map<String, Object> props = new HashMap<>(%s);", additionalPropertiesName()))
        .appendList(addPropertyToMap(), ParentAndComposedPojo::getAdditionalPropertiesMembers)
        .append((pc, s, w) -> w.println("return new %s(", pc.getComposedPojo().getClassName()))
        .appendList(
            memberGen.indent(1),
            pojo -> pojo.getComposedPojoAndMembers().flatMap(PojoAndMember::getFieldNames))
        .append(constant("props"), 1)
        .append(w -> w.println(");"))
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP));
  }

  private static Generator<JavaPojoMember, PojoSettings> addPropertyToMap() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(addPropertyToMapCondition())
        .append(
            (m, s, w) ->
                w.println("props.put(\"%s\", %s);", m.getName().getOriginalName(), m.getName()),
            1)
        .append(constant("}"));
  }

  private static Generator<JavaPojoMember, PojoSettings> addPropertyToMapCondition() {
    return addPropertyToMapNotNullableCondition()
        .append(addPropertyToMapRequiredNullableCondition())
        .append(addPropertyToMapOptionalNullableCondition());
  }

  private static Generator<JavaPojoMember, PojoSettings> addPropertyToMapNotNullableCondition() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("if (%s != null) {", m.getName()))
        .filter(JavaPojoMember::isNotNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings>
      addPropertyToMapRequiredNullableCondition() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((m, s, w) -> w.println("if (%s) {", m.getIsPresentFlagName()))
        .filter(JavaPojoMember::isRequiredAndNullable);
  }

  private static Generator<JavaPojoMember, PojoSettings>
      addPropertyToMapOptionalNullableCondition() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(
            (m, s, w) -> w.println("if (%s != null || %s) {", m.getName(), m.getIsNullFlagName()))
        .filter(JavaPojoMember::isOptionalAndNullable);
  }

  @Value
  private static class ParentAndComposedPojo {
    JavaObjectPojo parentPojo;
    JavaObjectPojo composedPojo;

    PList<JavaPojoMember> getAdditionalPropertiesMembers() {
      return parentPojo
          .getAllMembers()
          .filter(
              m1 ->
                  not(
                      composedPojo
                          .getAllMembers()
                          .exists(m2 -> m1.getName().equals(m2.getName()))));
    }

    PList<PojoAndMember> getComposedPojoAndMembers() {
      return composedPojo.getAllMembers().map(m -> new PojoAndMember(composedPojo, m));
    }
  }

  @Value
  private static class PojoAndMember {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    private PList<JavaName> getFieldNames() {
      if (member.isRequiredAndNotNullable() || member.isOptionalAndNotNullable()) {
        return PList.single(member.getName());
      } else if (member.isRequiredAndNullable()) {
        return PList.of(member.getName(), member.getIsPresentFlagName());
      } else {
        return PList.of(member.getName(), member.getIsNullFlagName());
      }
    }
  }
}
