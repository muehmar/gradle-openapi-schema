package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SetterBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleMemberSetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AllOfBuilderGenerator {
  private AllOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> allOfBuilderGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(allOfMemberGenerator(), AllOfMember::fromObjectPojo, newLine());
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
            .argumentFormat("%s %s")
            .addRefs(writer -> writer)
            .build();
    return singleMemberSetterGenerator(setter);
  }

  private static Generator<AllOfMember, PojoSettings> optionalSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(AllOfMember::isJavaOptional)
            .argumentFormat("Optional<%s> %s")
            .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .build();
    return singleMemberSetterGenerator(setter);
  }

  private static Generator<AllOfMember, PojoSettings> tristateSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(AllOfMember::isJavaTristate)
            .argumentFormat("Tristate<%s> %s")
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
        .singleArgument(m -> String.format("%s dto", m.allOfPojo.getClassName()))
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

    private static PList<AllOfMember> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getAllOfComposition()
          .map(allOfComposition -> fromParentPojoAndAllOfComposition(pojo, allOfComposition))
          .orElse(PList.empty());
    }

    private static PList<AllOfMember> fromParentPojoAndAllOfComposition(
        JavaObjectPojo pojo, JavaAllOfComposition allOfComposition) {
      return allOfComposition
          .getPojos()
          .toPList()
          .flatMap(
              allOfPojo ->
                  fromParentPojoAndAllOfCompositionAndAllOfPojo(pojo, allOfComposition, allOfPojo));
    }

    private static PList<AllOfMember> fromParentPojoAndAllOfCompositionAndAllOfPojo(
        JavaObjectPojo pojo, JavaAllOfComposition allOfComposition, JavaObjectPojo allOfPojo) {
      return allOfPojo
          .getAllMembers()
          .zipWithIndex()
          .map(
              p ->
                  new AllOfMember(
                      AllOfBuilderName.of(pojo, allOfComposition, allOfPojo, p.second()),
                      allOfPojo,
                      p.first(),
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
