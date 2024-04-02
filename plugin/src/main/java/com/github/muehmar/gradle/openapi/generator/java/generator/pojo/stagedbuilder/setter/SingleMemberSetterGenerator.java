package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.fieldRefs;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.Setter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.SetterMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class SingleMemberSetterGenerator {
  private SingleMemberSetterGenerator() {}

  public static Generator<SetterMember, PojoSettings> singleMemberSetterGenerator(Setter setter) {
    return singleMemberSetterGenerator(PList.single(setter));
  }

  public static Generator<SetterMember, PojoSettings> singleMemberSetterGenerator(
      PList<Setter> setters) {
    return setters
        .map(SingleMemberSetterGenerator::setterMethodForSetter)
        .reduce((gen1, gen2) -> gen1.appendSingleBlankLine().append(gen2))
        .orElse(Generator.emptyGen());
  }

  private static Generator<SetterMember, PojoSettings> setterMethodForSetter(Setter setter) {
    final Generator<SetterMember, PojoSettings> method =
        MethodGenBuilder.<SetterMember, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(SetterMember::nextStageClassName)
            .methodName(setter::methodName)
            .singleArgument(
                m -> new Argument(setter.argumentType(m), m.getMember().getName().asString()))
            .doesNotThrow()
            .content(
                (m, s, w) ->
                    w.println(
                        "return new %s(builder.%s(%s));",
                        m.nextStageClassName(), setter.methodName(m, s), m.getMember().getName()))
            .build()
            .append(fieldRefs(), SetterMember::getMember);
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .<SetterMember>contraMap(m -> m.getMember().getDescription())
        .append(method)
        .append(setter::addRefs)
        .filter(setter::includeInBuilder);
  }
}
