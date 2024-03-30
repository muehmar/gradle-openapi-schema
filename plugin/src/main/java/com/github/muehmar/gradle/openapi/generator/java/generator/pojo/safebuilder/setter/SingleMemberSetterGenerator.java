package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.fieldRefs;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.Setter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class SingleMemberSetterGenerator {
  private SingleMemberSetterGenerator() {}

  public static <T extends SetterMember> Generator<T, PojoSettings> singleMemberSetterGenerator(
      Setter<T> setter) {
    return singleMemberSetterGenerator(PList.single(setter));
  }

  public static <T extends SetterMember> Generator<T, PojoSettings> singleMemberSetterGenerator(
      PList<Setter<T>> setters) {
    return setters
        .map(
            setter ->
                SingleMemberSetterGenerator.<T>builderSetter(setter.argumentFormat())
                    .append(setter::addRefs)
                    .filter(setter::includeInBuilder))
        .reduce((gen1, gen2) -> gen1.appendSingleBlankLine().append(gen2))
        .orElse(Generator.emptyGen());
  }

  private static <T extends SetterMember> Generator<T, PojoSettings> builderSetter(
      String argumentFormat) {
    final Generator<T, PojoSettings> method =
        MethodGenBuilder.<T, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(T::nextStageClassName)
            .methodName(SetterMember::builderMethodName)
            .singleArgument(
                m ->
                    new Argument(
                        String.format(argumentFormat, m.argumentType()),
                        m.getMember().getName().asString()))
            .doesNotThrow()
            .content(
                (m, s, w) ->
                    w.println(
                        "return new %s(builder.%s(%s));",
                        m.nextStageClassName(), m.builderMethodName(s), m.getMember().getName()))
            .build()
            .append(fieldRefs(), SetterMember::getMember);
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .<T>contraMap(m -> m.getMember().getDescription())
        .append(method);
  }
}
