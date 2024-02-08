package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaPojoWrapResultTest {
  @ParameterizedTest
  @MethodSource("wrapObjectPojoWithPropertyScope")
  void getTypeOrDefault_when_wrapObjectPojoWithPropertyScope_then_returnCorrectPojoType(
      PropertyScope memberPropertyScope, PojoType typeArgument, PojoType expecedType) {
    final PojoMember pojoMember1 = PojoMembers.requiredString(memberPropertyScope);
    final ObjectPojo objectPojo =
        ObjectPojoBuilder.create()
            .name(componentName("Object", "Dto"))
            .description("Description")
            .nullability(NOT_NULLABLE)
            .members(PList.single(pojoMember1))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();

    final JavaPojoWrapResult wrapResult = JavaPojo.wrap(objectPojo, TypeMappings.empty());

    assertEquals(expecedType, wrapResult.getTypeOrDefault(typeArgument).getType());
  }

  public static Stream<Arguments> wrapObjectPojoWithPropertyScope() {
    return Stream.of(
        Arguments.arguments(PropertyScope.DEFAULT, PojoType.REQUEST, PojoType.DEFAULT),
        Arguments.arguments(PropertyScope.DEFAULT, PojoType.RESPONSE, PojoType.DEFAULT),
        Arguments.arguments(PropertyScope.DEFAULT, PojoType.DEFAULT, PojoType.DEFAULT),
        Arguments.arguments(PropertyScope.READ_ONLY, PojoType.REQUEST, PojoType.REQUEST),
        Arguments.arguments(PropertyScope.READ_ONLY, PojoType.RESPONSE, PojoType.RESPONSE),
        Arguments.arguments(PropertyScope.READ_ONLY, PojoType.DEFAULT, PojoType.DEFAULT),
        Arguments.arguments(PropertyScope.WRITE_ONLY, PojoType.REQUEST, PojoType.REQUEST),
        Arguments.arguments(PropertyScope.WRITE_ONLY, PojoType.RESPONSE, PojoType.RESPONSE),
        Arguments.arguments(PropertyScope.WRITE_ONLY, PojoType.DEFAULT, PojoType.DEFAULT));
  }
}
