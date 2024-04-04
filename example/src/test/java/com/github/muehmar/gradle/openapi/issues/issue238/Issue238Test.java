package com.github.muehmar.gradle.openapi.issues.issue238;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class Issue238Test {
  @Test
  void fullBuilder_when_usedWithOptionalPropsAnyOf_then_canSetLevel() {
    final UserOrAdminOptionalPropsDto dto =
        UserOrAdminOptionalPropsDto.fullBuilder()
            .setAdminDto(AdminDto.builder().setAdminname("adminname").build())
            .setLevel(5)
            .build();

    assertEquals(Optional.of(5), dto.getLevelOpt());
  }

  @Test
  void builder_when_usedWithOptionalPropsAnyOf_then_canSetLevel() {
    final UserOrAdminOptionalPropsDto dto =
        UserOrAdminOptionalPropsDto.builder()
            .setAdminDto(AdminDto.builder().setAdminname("adminname").build())
            .andAllOptionals()
            .setLevel(5)
            .build();

    assertEquals(Optional.of(5), dto.getLevelOpt());
  }

  @Test
  void fullBuilder_when_usedWithRequiredPropsAnyOf_then_canSetLevel() {
    final UserOrAdminRequiredPropsDto dto =
        UserOrAdminRequiredPropsDto.fullBuilder()
            .setAdminDto(AdminDto.builder().setAdminname("adminname").build())
            .setLevel(5)
            .build();

    assertEquals(5, dto.getLevel());
  }

  @Test
  void builder_when_usedWithRequiredPropsAnyOf_then_canSetLevel() {
    final UserOrAdminRequiredPropsDto dto =
        UserOrAdminRequiredPropsDto.builder()
            .setAdminDto(AdminDto.builder().setAdminname("adminname").build())
            .setLevel(5)
            .andAllOptionals()
            .build();

    assertEquals(5, dto.getLevel());
  }

  @Test
  void fullBuilder_when_usedWithRequiredAdditionalPropsAnyOf_then_canSetLevel() {
    final UserOrAdminRequiredAdditionalPropsDto dto =
        UserOrAdminRequiredAdditionalPropsDto.fullBuilder()
            .setAdminDto(AdminDto.builder().setAdminname("adminname").build())
            .setLevel(5)
            .build();

    assertEquals(5, dto.getLevel());
  }

  @Test
  void builder_when_usedWithRequiredAdditionalPropsAnyOf_then_canSetLevel() {
    final UserOrAdminRequiredAdditionalPropsDto dto =
        UserOrAdminRequiredAdditionalPropsDto.builder()
            .setAdminDto(AdminDto.builder().setAdminname("adminname").build())
            .setLevel(5)
            .andAllOptionals()
            .build();

    assertEquals(5, dto.getLevel());
  }
}
