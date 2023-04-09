package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class WitherGenerator {
  private WitherGenerator() {}

  public static Generator<JavaPojo, PojoSettings> generator() {
    return Generator.<JavaPojo, PojoSettings>emptyGen()
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
    protected final JavaPojo pojo;
    protected final JavaPojoMember pojoMember;

    public static PList<WitherMethod> fromPojo(JavaPojo pojo) {
      return pojo.getMembersOrEmpty()
          .flatMap(
              member ->
                  PList.of(
                      new NormalWitherMethod(pojo, member),
                      new OptionalWitherMethod(pojo, member),
                      new TristateWitherMethod(pojo, member)))
          .filter(WitherMethod::shouldBeUsed);
    }

    abstract boolean shouldBeUsed();

    public String javaDocString() {
      return pojoMember.getDescription();
    }

    public WitherMethod(JavaPojo pojo, JavaPojoMember pojoMember) {
      this.pojo = pojo;
      this.pojoMember = pojoMember;
    }

    String className() {
      return pojo.getClassName().asString();
    }

    String witherName() {
      return pojoMember.getWitherName().asString();
    }

    PList<String> argument() {
      return PList.single(
          String.format(
              argumentFormat(), pojoMember.getJavaType().getFullClassName(), pojoMember.getName()));
    }

    abstract String argumentFormat();

    String constructorCall() {
      final String constructorCall =
          String.format(
              "new %s(%s)",
              pojo.getClassName(),
              pojo.getMembersOrEmpty().flatMap(JavaPojoMember::createFieldNames).mkString(", "));
      return replacePropertiesInConstructorCall(constructorCall);
    }

    abstract String replacePropertiesInConstructorCall(String call);

    abstract Writer addRefs(Writer writer);
  }

  private static class NormalWitherMethod extends WitherMethod {
    public NormalWitherMethod(JavaPojo pojo, JavaPojoMember pojoMember) {
      super(pojo, pojoMember);
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
        return call.replaceAll(pojoMember.getIsPresentFlagName().asString(), "true");
      } else if (pojoMember.isOptionalAndNullable()) {
        return call.replaceAll(pojoMember.getIsNullFlagName().asString(), "false");
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
    public OptionalWitherMethod(JavaPojo pojo, JavaPojoMember pojoMember) {
      super(pojo, pojoMember);
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
                pojoMember.getName().asString(),
                String.format("%s.orElse(null)", pojoMember.getName()))
            .replaceAll(
                pojoMember.getIsPresentFlagName().asString(),
                String.format("%s.isPresent()", pojoMember.getName()));
      } else if (pojoMember.isOptionalAndNotNullable()) {
        return call.replaceAll(
                pojoMember.getName().asString(),
                String.format("%s.orElse(null)", pojoMember.getName()))
            .replaceAll(
                pojoMember.getIsNullFlagName().asString(),
                String.format("!%s.isPresent()", pojoMember.getName()));
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
    public TristateWitherMethod(JavaPojo pojo, JavaPojoMember pojoMember) {
      super(pojo, pojoMember);
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
                pojoMember.getName().asString(),
                String.format("%s.%s", pojoMember.getName(), pojoMember.tristateToProperty()))
            .replaceAll(
                pojoMember.getIsNullFlagName().asString(),
                String.format("%s.%s", pojoMember.getName(), pojoMember.tristateToIsNullFlag()));
      } else {
        return call;
      }
    }

    @Override
    Writer addRefs(Writer writer) {
      return writer.ref(OpenApiUtilRefs.TRISTATE);
    }
  }
}
