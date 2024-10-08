package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.wither;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriterBuilder.fullListAssigmentWriterBuilder;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.list.ListAssigmentWriter;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.WriteableParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

class ListWitherMethod extends WitherMethod {
  private final JavaPojoMember pojoMember;
  private final JavaArrayType javaArrayType;
  private final WitherListType witherListType;
  private final WitherItemType witherItemType;

  public ListWitherMethod(
      WitherGenerator.WitherContent witherContent,
      JavaPojoMember pojoMember,
      JavaArrayType javaArrayType,
      WitherListType witherListType,
      WitherItemType witherItemType) {
    super(witherContent, pojoMember);
    this.pojoMember = pojoMember;
    this.javaArrayType = javaArrayType;
    this.witherListType = witherListType;
    this.witherItemType = witherItemType;
  }

  public static PList<WitherMethod> fromContentAndMember(
      WitherGenerator.WitherContent witherContent, JavaPojoMember member) {
    return member
        .getJavaType()
        .onArrayType()
        .map(
            javaArrayType ->
                PList.of(
                    new ListWitherMethod(
                        witherContent,
                        member,
                        javaArrayType,
                        WitherListType.STANDARD,
                        WitherItemType.STANDARD),
                    new ListWitherMethod(
                        witherContent,
                        member,
                        javaArrayType,
                        WitherListType.OVERLOADED,
                        WitherItemType.STANDARD),
                    new ListWitherMethod(
                        witherContent,
                        member,
                        javaArrayType,
                        WitherListType.STANDARD,
                        WitherItemType.OVERLOADED),
                    new ListWitherMethod(
                        witherContent,
                        member,
                        javaArrayType,
                        WitherListType.OVERLOADED,
                        WitherItemType.OVERLOADED)))
        .orElse(PList.empty())
        .map(m -> m);
  }

  @Override
  boolean shouldBeUsed() {
    if (pojoMember.isRequiredAndNotNullable() && witherListType == WitherListType.OVERLOADED) {
      return false;
    }
    if (not(javaArrayType.isNullableItemsArrayType())
        && witherItemType == WitherItemType.OVERLOADED) {
      return false;
    }
    return true;
  }

  @Override
  String argumentType(WriteableParameterizedClassName parameterizedClassName) {
    final Function<WriteableParameterizedClassName, String> renderClassName =
        witherItemType == WitherItemType.STANDARD
            ? WriteableParameterizedClassName::asString
            : WriteableParameterizedClassName::asStringWrappingNullableValueType;

    if (witherListType == WitherListType.STANDARD) {
      return renderClassName.apply(parameterizedClassName);
    } else {
      final String formatString;
      if (pojoMember.isRequiredAndNullable() || pojoMember.isOptionalAndNotNullable()) {
        formatString = "Optional<%s>";
      } else {
        formatString = "Tristate<%s>";
      }
      return String.format(formatString, renderClassName.apply(parameterizedClassName));
    }
  }

  @Override
  boolean isOverloadedWither() {
    return witherListType.equals(WitherListType.OVERLOADED);
  }

  @Override
  Map<JavaName, StringOrWriter> propertyNameReplacementForConstructorCall() {
    final Writer assignmentWriter =
        fullListAssigmentWriterBuilder()
            .member(pojoMember)
            .expressionOnly()
            .unwrapList(witherListType.unwrapListFunction(pojoMember))
            .unmapListType(javaArrayType)
            .unwrapListItem(witherItemType.unwrapListItemFunction())
            .unmapListItemType(javaArrayType)
            .build();
    final HashMap<JavaName, StringOrWriter> map = new HashMap<>();
    map.put(pojoMember.getName(), StringOrWriter.ofWriter(assignmentWriter));
    return map;
  }

  @Override
  Writer addRefs(Writer writer) {
    final boolean optionalList =
        (pojoMember.isRequiredAndNullable() || pojoMember.isOptionalAndNotNullable())
            && witherListType == WitherListType.OVERLOADED;
    final boolean optionalListItem =
        javaArrayType.isNullableItemsArrayType() && witherItemType == WitherItemType.OVERLOADED;
    final boolean optionalRef = optionalList || optionalListItem;

    final boolean tristateList =
        pojoMember.isOptionalAndNullable() && witherListType == WitherListType.OVERLOADED;

    return writer
        .refs(optionalRef ? singletonList(JavaRefs.JAVA_UTIL_OPTIONAL) : emptyList())
        .refs(tristateList ? singletonList(OpenApiUtilRefs.TRISTATE) : emptyList());
  }

  @Override
  String witherName() {
    final String suffix = witherItemType == WitherItemType.OVERLOADED ? "_" : "";
    return super.witherName().concat(suffix);
  }

  public enum WitherListType {
    STANDARD {
      @Override
      ListAssigmentWriter.UnwrapListFunction unwrapListFunction(JavaPojoMember member) {
        return ListAssigmentWriter.UnwrapListFunction.IDENTITY;
      }
    },
    OVERLOADED {
      @Override
      ListAssigmentWriter.UnwrapListFunction unwrapListFunction(JavaPojoMember member) {
        if (member.isRequiredAndNullable() || member.isOptionalAndNotNullable()) {
          return ListAssigmentWriter.UnwrapListFunction.UNWRAP_OPTIONAL;
        } else {
          return ListAssigmentWriter.UnwrapListFunction.UNWRAP_TRISTATE;
        }
      }
    };

    abstract ListAssigmentWriter.UnwrapListFunction unwrapListFunction(JavaPojoMember member);
  }

  public enum WitherItemType {
    STANDARD {
      @Override
      ListAssigmentWriter.UnwrapListItemFunction unwrapListItemFunction() {
        return ListAssigmentWriter.UnwrapListItemFunction.IDENTITY;
      }
    },
    OVERLOADED {
      @Override
      ListAssigmentWriter.UnwrapListItemFunction unwrapListItemFunction() {
        return ListAssigmentWriter.UnwrapListItemFunction.UNWRAP_OPTIONAL;
      }
    };

    abstract ListAssigmentWriter.UnwrapListItemFunction unwrapListItemFunction();
  }
}
