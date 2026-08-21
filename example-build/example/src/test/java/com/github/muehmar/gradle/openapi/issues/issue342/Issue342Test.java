package com.github.muehmar.gradle.openapi.issues.issue342;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Files referenced by another specification should not need to be full OpenAPI documents: plain
 * schema-collection files (schemas directly at the root or under 'components/schemas', both without
 * the 'openapi' and 'info' fields) must be supported.
 */
public class Issue342Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void serialize_when_membersFromSchemaOnlyReferencedFiles_then_correctJson() throws Exception {
    final MyGroupDto dto =
        MyGroupDto.fullBuilder()
            .setDateFrom(LocalDate.of(2024, 5, 17))
            .setMember(GroupMemberDto.builder().setNickname("nick").build())
            .build();

    assertEquals(
        "{\"dateFrom\":\"2024-05-17\",\"member\":{\"nickname\":\"nick\"}}",
        MAPPER.writeValueAsString(dto));
  }

  @Test
  void deserialize_when_membersFromSchemaOnlyReferencedFiles_then_correctDto() throws Exception {
    final MyGroupDto dto =
        MAPPER.readValue(
            "{\"dateFrom\":\"2024-05-17\",\"member\":{\"nickname\":\"nick\"}}", MyGroupDto.class);

    assertEquals(LocalDate.of(2024, 5, 17), dto.getDateFrom());
    assertEquals(
        Optional.of(GroupMemberDto.builder().setNickname("nick").build()), dto.getMemberOpt());
  }
}
