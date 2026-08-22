package com.github.muehmar.gradle.openapi.issues.issue383;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/**
 * A formatTypeMapping for an enum format must also be applied when the enum schema is referenced
 * via {@code $ref} (it is applied for the identical inline enum and for referenced non-enum string
 * schemas, see also issue #113). The mapping is silently ignored for the referenced enum because
 * the format is discarded when mapping component enum schemas to pojos.
 *
 * <p>The test uses reflection so it compiles both before and after the fix.
 */
public class Issue383Test {

  @Test
  void getRefKind_when_enumFormatMappedToCustomType_then_returnsCustomKind()
      throws NoSuchMethodException {
    final Method getRefKind = KindHolderDto.class.getMethod("getRefKind");

    assertEquals(
        CustomKind.class,
        getRefKind.getReturnType(),
        "formatTypeMapping for 'issue383kind' should be applied to the referenced enum property");
  }
}
