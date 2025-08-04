package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory.StandardFactoryMethod.simpleBuilderMethodName;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.JavaGenerators;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.MethodGen;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.Value;

class SinglePropertyFactoryMethod {
  private static final String FACTORY_JAVA_DOC =
      "Convenience factory method for DTO's with a single property. Instantiates directly the DTO with the "
          + "given property. This method is only generated if the DTO has exactly one property.";

  private SinglePropertyFactoryMethod() {}

  static Generator<JavaObjectPojo, PojoSettings> singlePropertyFactoryMethod() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(methodWithJavaDoc(MethodType.STANDARD), SinglePropertyPojo::fromPojo)
        .appendSingleBlankLine()
        .appendOptional(methodWithJavaDoc(MethodType.OPTIONAL), SinglePropertyPojo::fromPojo)
        .appendSingleBlankLine()
        .appendOptional(methodWithJavaDoc(MethodType.TRISTATE), SinglePropertyPojo::fromPojo);
  }

  private static Generator<SinglePropertyPojo, PojoSettings> methodWithJavaDoc(
      MethodType methodType) {
    return JavaDocGenerator.<SinglePropertyPojo, PojoSettings>ofJavaDocString(FACTORY_JAVA_DOC)
        .append(method(methodType))
        .filter(methodType.filter);
  }

  private static Generator<SinglePropertyPojo, PojoSettings> method(MethodType methodType) {
    return JavaGenerators.<SinglePropertyPojo, PojoSettings>methodGen()
        .modifiers(JavaModifier.PUBLIC, JavaModifier.STATIC)
        .noGenericTypes()
        .returnType(spp -> spp.getPojo().getClassName())
        .methodName(spp -> spp.getMember().getName().prefixedMethodName("from"))
        .arguments(spp -> spp.getArgument(methodType))
        .doesNotThrow()
        .content(
            (spp, s, w) -> {
              final JavaPojoMember member = spp.getMember();
              return w.println(
                  "return %s().%s(%s).build();",
                  simpleBuilderMethodName(StagedBuilderVariant.FULL).apply(spp.getPojo()),
                  member.prefixedMethodName(s.getBuilderMethodPrefix()),
                  member.getName().startLowerCase());
            })
        .build()
        .append(methodType.ref)
        .append(RefsGenerator.fieldRefs(), SinglePropertyPojo::getMember);
  }

  @Value
  private static class SinglePropertyPojo {
    JavaObjectPojo pojo;
    JavaPojoMember member;

    public static Optional<SinglePropertyPojo> fromPojo(JavaObjectPojo pojo) {
      return pojo.getMembers()
          .headOption()
          .filter(ignore -> pojo.getMembers().size() == 1)
          .filter(ignore -> !pojo.getAllOfComposition().isPresent())
          .filter(ignore -> !pojo.getAnyOfComposition().isPresent())
          .filter(ignore -> !pojo.getOneOfComposition().isPresent())
          .filter(ignore -> pojo.getRequiredAdditionalProperties().isEmpty())
          .map(member -> new SinglePropertyPojo(pojo, member));
    }

    public PList<MethodGen.Argument> getArgument(MethodType methodType) {
      return PList.single(
          MethodGen.Argument.argument(
              String.format(
                  methodType.format,
                  member.getJavaType().getWriteableParameterizedClassName().asString()),
              member.getName().startLowerCase()));
    }

    boolean shouldHaveOptionalMethod() {
      return member.isRequiredAndNullable() || member.isOptionalAndNotNullable();
    }

    boolean shouldHaveTriStateMethod() {
      return member.isOptionalAndNullable();
    }
  }

  private enum MethodType {
    STANDARD("%s", Generator.emptyGen(), spp -> true),
    OPTIONAL(
        "Optional<%s>", RefsGenerator.optionalRef(), SinglePropertyPojo::shouldHaveOptionalMethod),
    TRISTATE(
        "Tristate<%s>",
        RefsGenerator.ref(OpenApiUtilRefs.TRISTATE), SinglePropertyPojo::shouldHaveTriStateMethod);
    private final String format;
    private final Generator<SinglePropertyPojo, PojoSettings> ref;
    private final Predicate<SinglePropertyPojo> filter;

    MethodType(
        String format,
        Generator<SinglePropertyPojo, PojoSettings> ref,
        Predicate<SinglePropertyPojo> filter) {
      this.format = format;
      this.ref = ref;
      this.filter = filter;
    }
  }
}
