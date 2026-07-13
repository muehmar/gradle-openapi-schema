package com.github.muehmar.gradle.openapi.issues.issue382;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/**
 * A dtoMapping with conversion on a member of composed schemas must be preserved in the
 * composition container: {@code JavaObjectType.withNullability} drops the ApiType during the
 * least-restrictive member merge, so the container reverts to the raw DTO type and the generated
 * {@code setHomeDto} passes the custom type into the raw-typed setter — the generated code does
 * not even compile.
 *
 * <p>NOTE: this module only compiles once the issue is fixed; the compile failure of the
 * generated {@code LocationDto} is the primary demonstration.
 */
public class Issue382Test {

  @Test
  void getAddressOpt_when_addressDtoMappedToCustomType_then_containerUsesCustomAddress()
      throws NoSuchMethodException {
    final Method getAddressOpt = LocationDto.class.getMethod("getAddressOpt");

    assertTrue(
        getAddressOpt.getGenericReturnType().getTypeName().contains("CustomAddress"),
        "container member must keep the dtoMapping api type");
  }
}
