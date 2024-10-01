package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Map;
import java.util.Optional;

abstract class WitherMethod {
  protected final WitherGenerator.WitherContent witherContent;
  protected final JavaPojoMember pojoMember;

  public static PList<WitherMethod> fromWitherContent(WitherGenerator.WitherContent witherContent) {
    return witherContent
        .getMembersForWithers()
        .flatMap(member -> fromMember(witherContent, member))
        .filter(WitherMethod::shouldBeUsed);
  }

  private static PList<WitherMethod> fromMember(
      WitherGenerator.WitherContent witherContent, JavaPojoMember member) {
    final PList<WitherMethod> witherMethods =
        PList.of(
            new NormalWitherMethod(witherContent, member),
            new OptionalWitherMethod(witherContent, member),
            new TristateWitherMethod(witherContent, member));

    return witherMethods
        .concat(ListWitherMethod.fromContentAndMember(witherContent, member))
        .concat(MapWitherMethod.fromContentAndMember(witherContent, member));
  }

  WitherMethod(WitherGenerator.WitherContent witherContent, JavaPojoMember pojoMember) {
    this.witherContent = witherContent;
    this.pojoMember = pojoMember;
  }

  abstract boolean shouldBeUsed();

  public JavaPojoMember getPojoMember() {
    return pojoMember;
  }

  public String javaDocString() {
    return pojoMember.getDescription();
  }

  String className() {
    return witherContent.getClassName().asString();
  }

  String witherName() {
    return pojoMember.getWitherName().asString();
  }

  PList<MethodGen.Argument> argument() {
    return PList.single(
        new MethodGen.Argument(
            String.format(
                argumentType(pojoMember.getJavaType().getWriteableParameterizedClassName())),
            pojoMember.getName().asString()));
  }

  abstract String argumentType(WriteableParameterizedClassName parameterizedClassName);

  Writer constructorCall() {
    final Writer newClassWriter =
        Writer.javaWriter().println("new %s(", witherContent.getClassName());

    final PList<StringOrWriter> members =
        witherContent
            .getTechnicalPojoMembers()
            .map(TechnicalPojoMember::getName)
            .map(
                name ->
                    Optional.ofNullable(propertyNameReplacementForConstructorCall().get(name))
                        .orElse(companionFlagReplacementForConstructorCall(name)));

    return members
        .zipWithIndex()
        .foldLeft(
            newClassWriter,
            (w, p) -> {
              final StringOrWriter memberValue = p.first();
              final int index = p.second();
              final String trailingComma = index == members.size() - 1 ? "" : ",";
              return memberValue.fold(
                  valueAsString -> w.tab(1).println("%s%s", valueAsString, trailingComma),
                  valueAsWriter ->
                      w.append(1, valueAsWriter.resetToLastNotEmptyLine().println(trailingComma)));
            })
        .println(");");
  }

  private StringOrWriter companionFlagReplacementForConstructorCall(JavaName propertyName) {
    if (propertyName.equals(pojoMember.getIsPresentFlagName())
        && pojoMember.isRequiredAndNullable()) {
      return StringOrWriter.ofString("true");
    } else if (propertyName.equals(pojoMember.getIsNotNullFlagName())
        && pojoMember.isOptionalAndNotNullable()) {
      return StringOrWriter.ofString("true");
    } else if (propertyName.equals(pojoMember.getIsNullFlagName())
        && pojoMember.isOptionalAndNullable()) {
      if (isOverloadedWither()) {
        final String flagValue =
            String.format(
                "%s.%s", pojoMember.getName().asString(), pojoMember.tristateToIsNullFlag());
        return StringOrWriter.ofString(flagValue);
      } else {
        return StringOrWriter.ofString("false");
      }
    }
    return StringOrWriter.ofString(propertyName.asString());
  }

  /**
   * Returns true in case this wither is an overloaded version, i.e. wrapped in Optional or
   * Tristate.
   */
  abstract boolean isOverloadedWither();

  /** */
  abstract Map<JavaName, StringOrWriter> propertyNameReplacementForConstructorCall();

  abstract Writer addRefs(Writer writer);
}
