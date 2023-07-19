package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;

public class WitherGenerator {
  private WitherGenerator() {}

  public static Generator<WitherContent, PojoSettings> witherGenerator() {
    return Generator.<WitherContent, PojoSettings>emptyGen()
        .appendList(method(), WitherMethod::fromPojo, newLine());
  }

  private static Generator<WitherMethod, PojoSettings> method() {
    final MethodGen<WitherMethod, PojoSettings> method =
        MethodGenBuilder.<WitherMethod, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(WitherMethod::className)
            .methodName(WitherMethod::witherName)
            .arguments(WitherMethod::argument)
            .content(methodContent())
            .build();
    return JavaDocGenerator.<PojoSettings>javaDoc()
        .contraMap(WitherMethod::javaDocString)
        .append(method)
        .append((wm, s, w) -> wm.addRefs(w));
  }

  private static Generator<WitherMethod, PojoSettings> methodContent() {
    return Generator.<WitherMethod, PojoSettings>emptyGen()
        .append((wm, s, w) -> w.println("return %s;", wm.constructorCall()));
  }

  private abstract static class WitherMethod {
    protected final WitherContent witherContent;
    protected final JavaPojoMember pojoMember;

    public static PList<WitherMethod> fromPojo(WitherContent witherContent) {
      return witherContent
          .getMembersForWithers()
          .flatMap(
              member ->
                  PList.of(
                      new NormalWitherMethod(witherContent, member),
                      new OptionalWitherMethod(witherContent, member),
                      new TristateWitherMethod(witherContent, member)))
          .filter(WitherMethod::shouldBeUsed);
    }

    abstract boolean shouldBeUsed();

    public String javaDocString() {
      return pojoMember.getDescription();
    }

    public WitherMethod(WitherContent witherContent, JavaPojoMember pojoMember) {
      this.witherContent = witherContent;
      this.pojoMember = pojoMember;
    }

    String className() {
      return witherContent.getClassName().asString();
    }

    String witherName() {
      return pojoMember.getWitherName().asString();
    }

    PList<String> argument() {
      return PList.single(
          String.format(
              argumentFormat(),
              pojoMember.getJavaType().getFullClassName(),
              pojoMember.getNameAsIdentifier()));
    }

    abstract String argumentFormat();

    String constructorCall() {
      final String constructorCall =
          String.format(
              "new %s(%s%s)",
              witherContent.getClassName(),
              witherContent
                  .getMembersForConstructorCall()
                  .flatMap(JavaPojoMember::createFieldNames)
                  .mkString(", "),
              witherContent
                  .getAdditionalProperties()
                  .map(props -> String.format(", %s", JavaAdditionalProperties.getPropertyName()))
                  .orElse(""));
      return replacePropertiesInConstructorCall(constructorCall);
    }

    abstract String replacePropertiesInConstructorCall(String call);

    abstract Writer addRefs(Writer writer);
  }

  private static class NormalWitherMethod extends WitherMethod {
    public NormalWitherMethod(WitherContent witherContent, JavaPojoMember pojoMember) {
      super(witherContent, pojoMember);
    }

    @Override
    boolean shouldBeUsed() {
      return true;
    }

    @Override
    String argumentFormat() {
      return "%s %s";
    }

    @Override
    String replacePropertiesInConstructorCall(String call) {
      if (pojoMember.isRequiredAndNullable()) {
        return call.replaceAll(pojoMember.getIsPresentFlagName().wordBoundaryPattern(), "true");
      } else if (pojoMember.isOptionalAndNullable()) {
        return call.replaceAll(pojoMember.getIsNullFlagName().wordBoundaryPattern(), "false");
      } else {
        return call;
      }
    }

    @Override
    Writer addRefs(Writer writer) {
      return writer;
    }
  }

  private static class OptionalWitherMethod extends WitherMethod {
    public OptionalWitherMethod(WitherContent witherContent, JavaPojoMember pojoMember) {
      super(witherContent, pojoMember);
    }

    @Override
    boolean shouldBeUsed() {
      return pojoMember.isRequiredAndNullable() || pojoMember.isOptionalAndNotNullable();
    }

    @Override
    String argumentFormat() {
      return "Optional<%s> %s";
    }

    @Override
    String replacePropertiesInConstructorCall(String call) {
      if (pojoMember.isRequiredAndNullable()) {
        return call.replaceAll(
                pojoMember.getNameAsIdentifier().wordBoundaryPattern(),
                String.format("%s.orElse(null)", pojoMember.getNameAsIdentifier()))
            .replaceAll(
                pojoMember.getIsPresentFlagName().wordBoundaryPattern(),
                String.format("%s.isPresent()", pojoMember.getNameAsIdentifier()));
      } else if (pojoMember.isOptionalAndNotNullable()) {
        return call.replaceAll(
                pojoMember.getNameAsIdentifier().wordBoundaryPattern(),
                String.format("%s.orElse(null)", pojoMember.getNameAsIdentifier()))
            .replaceAll(
                pojoMember.getIsNullFlagName().wordBoundaryPattern(),
                String.format("!%s.isPresent()", pojoMember.getNameAsIdentifier()));
      } else {
        return call;
      }
    }

    @Override
    Writer addRefs(Writer writer) {
      return writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL);
    }
  }

  private static class TristateWitherMethod extends WitherMethod {
    public TristateWitherMethod(WitherContent witherContent, JavaPojoMember pojoMember) {
      super(witherContent, pojoMember);
    }

    @Override
    boolean shouldBeUsed() {
      return pojoMember.isOptionalAndNullable();
    }

    @Override
    String argumentFormat() {
      return "Tristate<%s> %s";
    }

    @Override
    String replacePropertiesInConstructorCall(String call) {
      if (pojoMember.isOptionalAndNullable()) {
        return call.replaceAll(
                pojoMember.getNameAsIdentifier().wordBoundaryPattern(),
                String.format(
                    "%s.%s", pojoMember.getNameAsIdentifier(), pojoMember.tristateToProperty()))
            .replaceAll(
                pojoMember.getIsNullFlagName().wordBoundaryPattern(),
                String.format(
                    "%s.%s", pojoMember.getNameAsIdentifier(), pojoMember.tristateToIsNullFlag()));
      } else {
        return call;
      }
    }

    @Override
    Writer addRefs(Writer writer) {
      return writer.ref(OpenApiUtilRefs.TRISTATE);
    }
  }

  @Value
  @PojoBuilder(builderName = "WitherContentBuilder")
  public static class WitherContent {
    JavaIdentifier className;
    PList<JavaPojoMember> membersForWithers;
    PList<JavaPojoMember> membersForConstructorCall;
    Optional<JavaAdditionalProperties> additionalProperties;
  }
}
