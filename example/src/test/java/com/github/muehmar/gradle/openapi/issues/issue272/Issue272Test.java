package com.github.muehmar.gradle.openapi.issues.issue272;

import static com.github.muehmar.gradle.openapi.issues.issue272.RoleDto.fullRoleDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import org.junit.jupiter.api.Test;

public class Issue272Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void serialize_when_uppercaseProperty_then_serializedCorrectly() throws Exception {
    final OAuthDto oAuthDto =
        OAuthDto.fullOAuthDtoBuilder()
            .setOAuth1("oauth-1")
            .setOAuth2("oauth-2")
            .setOAuth3("oauth-3")
            .setOAuth4("oauth-4")
            .build();
    final RoleDto roleDto =
        fullRoleDtoBuilder()
            .setOAuthDto(oAuthDto)
            .setTC1("value-1")
            .setTC2("value-2")
            .setTC3("value-3")
            .setTC4("value-4")
            .build();

    final String json = MAPPER.writeValueAsString(roleDto);
    assertEquals(
        "{\"TC1\":\"value-1\",\"TC2\":\"value-2\",\"TC3\":\"value-3\",\"TC4\":\"value-4\",\"oAuth1\":\"oauth-1\",\"oAuth2\":\"oauth-2\",\"oAuth3\":\"oauth-3\",\"oAuth4\":\"oauth-4\"}",
        json);
  }
}
