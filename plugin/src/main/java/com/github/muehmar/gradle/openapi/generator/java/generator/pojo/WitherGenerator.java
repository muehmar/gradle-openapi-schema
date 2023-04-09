package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

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
        .append(method);
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
              member -> {
                final NormalWitherMethod normalWitherMethod = new NormalWitherMethod(pojo, member);
                if (member.isRequiredAndNullable() || member.isOptionalAndNotNullable()) {
                  final OptionalWitherMethod optionalWitherMethod =
                      new OptionalWitherMethod(pojo, member);
                  return PList.of(normalWitherMethod, optionalWitherMethod);
                } else if (member.isOptionalAndNullable()) {
                  return PList.of(normalWitherMethod, new TristateWitherMethod(pojo, member));
                } else {
                  return PList.single(normalWitherMethod);
                }
              });
    }

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

    abstract PList<String> argument();

    abstract String constructorCall();
  }

  private static class NormalWitherMethod extends WitherMethod {
    public NormalWitherMethod(JavaPojo pojo, JavaPojoMember pojoMember) {
      super(pojo, pojoMember);
    }

    PList<String> argument() {
      return PList.single(
          String.format(
              "%s %s", pojoMember.getJavaType().getFullClassName(), pojoMember.getName()));
    }

    String constructorCall() {
      final String constructorCall =
          String.format(
              "new %s(%s)",
              pojo.getClassName(),
              pojo.getMembersOrEmpty().flatMap(JavaPojoMember::createFieldNames).mkString(", "));
      if (pojoMember.isRequiredAndNullable()) {
        return constructorCall.replaceAll(pojoMember.getIsPresentFlagName().asString(), "true");
      } else if (pojoMember.isOptionalAndNullable()) {
        return constructorCall.replaceAll(pojoMember.getIsNullFlagName().asString(), "false");
      } else {
        return constructorCall;
      }
    }
  }

  private static class OptionalWitherMethod extends WitherMethod {
    public OptionalWitherMethod(JavaPojo pojo, JavaPojoMember pojoMember) {
      super(pojo, pojoMember);
    }

    PList<String> argument() {
      return PList.single(
          String.format(
              "Optional<%s> %s",
              pojoMember.getJavaType().getFullClassName(), pojoMember.getName()));
    }

    String constructorCall() {
      final String constructorCall =
          String.format(
              "new %s(%s)",
              pojo.getClassName(),
              pojo.getMembersOrEmpty().flatMap(JavaPojoMember::createFieldNames).mkString(", "));
      if (pojoMember.isRequiredAndNullable()) {
        return constructorCall
            .replaceAll(
                pojoMember.getName().asString(),
                String.format("%s.orElse(null)", pojoMember.getName()))
            .replaceAll(
                pojoMember.getIsPresentFlagName().asString(),
                String.format("%s.isPresent()", pojoMember.getName()));
      } else if (pojoMember.isOptionalAndNotNullable()) {
        return constructorCall
            .replaceAll(
                pojoMember.getName().asString(),
                String.format("%s.orElse(null)", pojoMember.getName()))
            .replaceAll(
                pojoMember.getIsNullFlagName().asString(),
                String.format("!%s.isPresent()", pojoMember.getName()));
      } else {
        return constructorCall;
      }
    }
  }

  private static class TristateWitherMethod extends WitherMethod {
    public TristateWitherMethod(JavaPojo pojo, JavaPojoMember pojoMember) {
      super(pojo, pojoMember);
    }

    PList<String> argument() {
      return PList.single(
          String.format(
              "Tristate<%s> %s",
              pojoMember.getJavaType().getFullClassName(), pojoMember.getName()));
    }

    String constructorCall() {
      final String constructorCall =
          String.format(
              "new %s(%s)",
              pojo.getClassName(),
              pojo.getMembersOrEmpty().flatMap(JavaPojoMember::createFieldNames).mkString(", "));
      if (pojoMember.isOptionalAndNullable()) {
        return constructorCall
            .replaceAll(
                pojoMember.getName().asString(),
                String.format("%s.%s", pojoMember.getName(), pojoMember.tristateToProperty()))
            .replaceAll(
                pojoMember.getIsNullFlagName().asString(),
                String.format("%s.%s", pojoMember.getName(), pojoMember.tristateToIsNullFlag()));
      } else {
        return constructorCall;
      }
    }
  }
}
