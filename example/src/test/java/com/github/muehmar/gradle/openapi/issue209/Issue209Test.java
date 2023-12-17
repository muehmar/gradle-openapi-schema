package com.github.muehmar.gradle.openapi.issue209;

import static com.github.muehmar.gradle.openapi.issue209.NewUserAddressDto.fullNewUserAddressDtoBuilder;
import static com.github.muehmar.gradle.openapi.issue209.NewUserCityDto.fullNewUserCityDtoBuilder;
import static com.github.muehmar.gradle.openapi.issue209.NewUserDto.fullNewUserDtoBuilder;
import static com.github.muehmar.gradle.openapi.issue209.UpdateUserAddressDto.fullUpdateUserAddressDtoBuilder;
import static com.github.muehmar.gradle.openapi.issue209.UpdateUserDto.fullUpdateUserDtoBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;
import org.junit.jupiter.api.Test;

public class Issue209Test {
  @Test
  void newUserDtoBuilder_when_used_then_allPropertiesCorrectPromoted() {
    final NewUserCityDto cityDto =
        fullNewUserCityDtoBuilder()
            .setZip(1122)
            .setCityName("cityName")
            .setCityCode(Optional.of("cityCode"))
            .build();

    final NewUserAddressDto addressDto =
        fullNewUserAddressDtoBuilder()
            .setStreet("street")
            .setHouseNumber(Optional.of(1234))
            .setCity(cityDto)
            .build();

    final NewUserDto newUserDto =
        fullNewUserDtoBuilder()
            .setFirstName("firstName")
            .setMiddleName(Optional.of("middleName"))
            .setLastName("lastName")
            .setAddress(addressDto)
            .build();

    assertEquals("firstName", newUserDto.getFirstName());
    assertEquals(Optional.of("middleName"), newUserDto.getMiddleNameOpt());
    assertEquals("lastName", newUserDto.getLastName());

    assertEquals(addressDto, newUserDto.getAddress());

    assertEquals("street", addressDto.getStreet());
    assertEquals(Optional.of(cityDto), addressDto.getCityOpt());

    assertEquals("cityName", cityDto.getCityName());
    assertEquals(Optional.of("cityCode"), cityDto.getCityCodeOpt());
    assertEquals(1122, cityDto.getZip());
  }

  @Test
  void updateUserDtoBuilder_when_used_then_allPropertiesCorrectPromoted() {
    final UpdateUserCityDto cityDto =
        UpdateUserCityDto.fullUpdateUserCityDtoBuilder()
            .setCityCode("cityCode")
            .setCityName("cityName")
            .setZip(Optional.of(1122))
            .build();

    final UpdateUserAddressDto addressDto =
        fullUpdateUserAddressDtoBuilder()
            .setStreet("street")
            .setCity(cityDto)
            .setHouseNumber(Optional.of(1234))
            .build();

    final UpdateUserDto updateUserDto =
        fullUpdateUserDtoBuilder()
            .setFirstName(Optional.of("firstName"))
            .setMiddleName("middleName")
            .setLastName("lastName")
            .setAddress(addressDto)
            .build();

    assertEquals(Optional.of("firstName"), updateUserDto.getFirstNameOpt());
    assertEquals("middleName", updateUserDto.getMiddleName());
    assertEquals("lastName", updateUserDto.getLastName());

    assertEquals(Optional.of(addressDto), updateUserDto.getAddressOpt());

    assertEquals("street", addressDto.getStreet());
    assertEquals(cityDto, addressDto.getCity());

    assertEquals("cityName", cityDto.getCityName());
    assertEquals("cityCode", cityDto.getCityCode());
    assertEquals(Optional.of(1122), cityDto.getZipOpt());
  }
}
