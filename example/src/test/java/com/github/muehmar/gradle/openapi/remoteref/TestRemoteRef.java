package com.github.muehmar.gradle.openapi.remoteref;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.remoteref.model.CityDto;
import OpenApiSchema.example.api.remoteref.model.UserDto;
import org.junit.jupiter.api.Test;

class TestRemoteRef {
  @Test
  void cityDtoCreated() {
    final CityDto city =
        CityDto.newBuilder().setName("Winterthur").andOptionals().setZip(8400).build();
    final UserDto user = UserDto.newBuilder().setName("Name").setCity(city).andOptionals().build();

    assertEquals("Name", user.getName());
    assertEquals(city, user.getCity());
  }
}
