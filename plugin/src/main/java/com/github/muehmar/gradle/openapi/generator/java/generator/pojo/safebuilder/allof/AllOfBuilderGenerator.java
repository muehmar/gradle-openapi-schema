package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SetterBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AllOfBuilderGenerator {
  private AllOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> allOfBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            allOfMemberGenerator(),
            pojo -> AllOfMember.fromObjectPojo(builderVariant, pojo),
            newLine());
  }

  private static Generator<AllOfMember, PojoSettings> allOfMemberGenerator() {
    final PList<Generator<AllOfMember, PojoSettings>> content =
        PList.of(normalSetter(), optionalSetter(), tristateSetter(), dtoSetter());
    return SingleBuilderClassGenerator.singleBuilderClassGenerator(
        AllOfMember::builderClassName, content);
  }

  private static Generator<AllOfMember, PojoSettings> normalSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(ignore -> true)
            .typeFormat("%s")
            .addRefs(writer -> writer)
            .build();
    return singleMemberSetterGenerator(setter);
  }

  private static Generator<AllOfMember, PojoSettings> optionalSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(AllOfMember::isJavaOptional)
            .typeFormat("Optional<%s>")
            .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .build();
    return singleMemberSetterGenerator(setter);
  }

  private static Generator<AllOfMember, PojoSettings> tristateSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(AllOfMember::isJavaTristate)
            .typeFormat("Tristate<%s>")
            .addRefs(writer -> writer.ref(OpenApiUtilRefs.TRISTATE))
            .build();
    return singleMemberSetterGenerator(setter);
  }

  private static Generator<AllOfMember, PojoSettings> dtoSetter() {
    return MethodGenBuilder.<AllOfMember, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(AllOfMember::nextPojoBuilderClassName)
        .methodName(
            (allOfMember, settings) ->
                allOfMember
                    .allOfPojo
                    .prefixedClassNameForMethod(settings.getBuilderMethodPrefix())
                    .asString())
        .singleArgument(m -> new MethodGen.Argument(m.allOfPojo.getClassName().asString(), "dto"))
        .content(
            (m, s, w) ->
                w.println(
                    "return new %s(builder.%s(dto));",
                    m.nextPojoBuilderClassName(),
                    m.allOfPojo.prefixedClassNameForMethod(s.getBuilderMethodPrefix())))
        .build()
        .filter(AllOfMember::isFirstMember);
  }

  @Value
  private static class AllOfMember implements SingleMemberSetterGenerator.Member {
    AllOfBuilderName allOfBuilderName;
    JavaObjectPojo allOfPojo;
    JavaPojoMember member;
    int idx;

    private static PList<AllOfMember> fromObjectPojo(
        SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
      return pojo.getAllOfComposition()
          .map(
              allOfComposition ->
                  fromParentPojoAndAllOfComposition(builderVariant, pojo, allOfComposition))
          .orElse(PList.empty());
    }

    private static PList<AllOfMember> fromParentPojoAndAllOfComposition(
        SafeBuilderVariant builderVariant,
        JavaObjectPojo pojo,
        JavaAllOfComposition allOfComposition) {
      return allOfComposition
          .getPojos()
          .toPList()
          .flatMap(
              allOfPojo ->
                  fromParentPojoAndAllOfCompositionAndAllOfPojo(
                      builderVariant, pojo, allOfComposition, allOfPojo));
    }

    private static PList<AllOfMember> fromParentPojoAndAllOfCompositionAndAllOfPojo(
        SafeBuilderVariant builderVariant,
        JavaObjectPojo pojo,
        JavaAllOfComposition allOfComposition,
        JavaObjectPojo allOfPojo) {
      return allOfPojo
          .getAllMembers()
          .zipWithIndex()
          .map(
              p ->
                  new AllOfMember(
                      AllOfBuilderName.of(
                          builderVariant, pojo, allOfComposition, allOfPojo, p.second()),
                      allOfPojo,
                      p.first().asInnerEnumOf(allOfPojo.getClassName()),
                      p.second()));
    }

    @Override
    public String builderClassName() {
      return allOfBuilderName.currentName();
    }

    @Override
    public String nextBuilderClassName() {
      return allOfBuilderName.getNextBuilderName().currentName();
    }

    public String nextPojoBuilderClassName() {
      return allOfBuilderName.getNextPojoBuilderName().currentName();
    }

    private boolean isFirstMember() {
      return idx == 0;
    }

    public boolean isJavaOptional() {
      return member.isOptionalAndNotNullable() || member.isRequiredAndNullable();
    }

    public boolean isJavaTristate() {
      return member.isOptionalAndNullable();
    }
  }
}
