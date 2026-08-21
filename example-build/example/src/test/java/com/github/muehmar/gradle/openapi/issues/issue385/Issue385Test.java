package com.github.muehmar.gradle.openapi.issues.issue385;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.gradle.openapi.util.XmlMapper;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Issue 385: an xml-only Jackson config ({@code jsonSupport} 'none' with a Jackson {@code
 * xmlSupport}) generated the json annotations reused by Jackson XML without imports and the
 * ZonedDateTime deserializer in the wrong dialect.
 *
 * <p>The actual assertion of this issue is that the generated code compiles at all; the test below
 * merely exercises the {@code date-time} property (and with it the generated
 * ZonedDateTimeDeserializer) at runtime.
 */
class Issue385Test {
  private static final XmlMapper XML_MAPPER = MapperFactory.xmlMapper();

  @Test
  void deserialize_when_xmlWithDateTimeProperty_then_zonedDateTimeReturned() throws Exception {
    final String xml =
        "<User><name>john</name><registered>2026-08-05T10:15:30Z</registered></User>";

    final UserDto dto = XML_MAPPER.readValue(xml, UserDto.class);

    assertEquals("john", dto.getName());
    assertEquals(Optional.of(ZonedDateTime.parse("2026-08-05T10:15:30Z")), dto.getRegisteredOpt());
  }
}
