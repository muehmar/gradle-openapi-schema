package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Maps.map;
import static com.github.muehmar.gradle.openapi.Optionals.opt;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.MapObjectDto.fullMapObjectDtoBuilder;
import static java.util.Optional.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.Tristate;
import org.junit.jupiter.api.Test;

public class MapObjectApiTest {
  @Test
  void stagedBuilder_when_standardSetterUsed_then_getterReturnsCorrectValue() {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(map("usernames-k-1", customString("usernames-v-1")))
            .setEmailsMap(map("emails-k-1", customString("emails-v-1")))
            .setPhonesMap(map("phones-k-1", customString("phones-v-1")))
            .build();

    assertEquals(map("id-k-1", customString("id-v-1")), mapObjectDto.getIdsMap());
    assertEquals(
        opt(map("usernames-k-1", customString("usernames-v-1"))),
        mapObjectDto.getUsernamesMapOpt());
    assertEquals(
        opt(map("emails-k-1", customString("emails-v-1"))), mapObjectDto.getEmailsMapOpt());
    assertEquals(
        Tristate.ofValue(map("phones-k-1", customString("phones-v-1"))),
        mapObjectDto.getPhonesMapTristate());
  }

  @Test
  void stagedBuilder_when_overloadedSettersUsed_then_getterReturnCorrectValues() {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(opt(map("usernames-k-1", customString("usernames-v-1"))))
            .setEmailsMap(opt(map("emails-k-1", customString("emails-v-1"))))
            .setPhonesMap(Tristate.ofValue(map("phones-k-1", customString("phones-v-1"))))
            .build();

    assertEquals(map("id-k-1", customString("id-v-1")), mapObjectDto.getIdsMap());
    assertEquals(
        opt(map("usernames-k-1", customString("usernames-v-1"))),
        mapObjectDto.getUsernamesMapOpt());
    assertEquals(
        opt(map("emails-k-1", customString("emails-v-1"))), mapObjectDto.getEmailsMapOpt());
    assertEquals(
        Tristate.ofValue(map("phones-k-1", customString("phones-v-1"))),
        mapObjectDto.getPhonesMapTristate());
  }

  @Test
  void stagedBuilder_when_overloadedSettersUsedWithoutValues_then_getterReturnCorrectValues() {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(empty())
            .setEmailsMap(empty())
            .setPhonesMap(Tristate.ofAbsent())
            .build();

    assertEquals(map("id-k-1", customString("id-v-1")), mapObjectDto.getIdsMap());
    assertEquals(empty(), mapObjectDto.getUsernamesMapOpt());
    assertEquals(empty(), mapObjectDto.getEmailsMapOpt());
    assertEquals(Tristate.ofAbsent(), mapObjectDto.getPhonesMapTristate());
  }

  @Test
  void withers_when_standardWithersUsed_then_propertiesSetCorrectly() {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(map("usernames-k-1", customString("usernames-v-1")))
            .setEmailsMap(map("emails-k-1", customString("emails-v-1")))
            .setPhonesMap(map("phones-k-1", customString("phones-v-1")))
            .build();

    final MapObjectDto changedDto =
        mapObjectDto
            .withIdsMap(map("id-k-2", customString("id-v-2")))
            .withUsernamesMap(map("usernames-k-2", customString("usernames-v-2")))
            .withEmailsMap(map("emails-k-2", customString("emails-v-2")))
            .withPhonesMap(map("phones-k-2", customString("phones-v-2")));

    assertEquals(map("id-k-2", customString("id-v-2")), changedDto.getIdsMap());
    assertEquals(
        opt(map("usernames-k-2", customString("usernames-v-2"))), changedDto.getUsernamesMapOpt());
    assertEquals(opt(map("emails-k-2", customString("emails-v-2"))), changedDto.getEmailsMapOpt());
    assertEquals(
        Tristate.ofValue(map("phones-k-2", customString("phones-v-2"))),
        changedDto.getPhonesMapTristate());
  }

  @Test
  void withers_when_overloadedWithersUsed_then_propertiesSetCorrectly() {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(opt(map("usernames-k-1", customString("usernames-v-1"))))
            .setEmailsMap(opt(map("emails-k-1", customString("emails-v-1"))))
            .setPhonesMap(Tristate.ofValue(map("phones-k-1", customString("phones-v-1"))))
            .build();

    final MapObjectDto changedDto =
        mapObjectDto
            .withIdsMap(map("id-k-2", customString("id-v-2")))
            .withUsernamesMap(opt(map("usernames-k-2", customString("usernames-v-2"))))
            .withEmailsMap(opt(map("emails-k-2", customString("emails-v-2"))))
            .withPhonesMap(Tristate.ofValue(map("phones-k-2", customString("phones-v-2"))));

    assertEquals(map("id-k-2", customString("id-v-2")), changedDto.getIdsMap());
    assertEquals(
        opt(map("usernames-k-2", customString("usernames-v-2"))), changedDto.getUsernamesMapOpt());
    assertEquals(opt(map("emails-k-2", customString("emails-v-2"))), changedDto.getEmailsMapOpt());
    assertEquals(
        Tristate.ofValue(map("phones-k-2", customString("phones-v-2"))),
        changedDto.getPhonesMapTristate());
  }

  @Test
  void withers_when_overloadedWithersUsedWithoutValues_then_propertiesSetCorrectly() {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(map("usernames-k-1", customString("usernames-v-1")))
            .setEmailsMap(map("emails-k-1", customString("emails-v-1")))
            .setPhonesMap(map("phones-k-1", customString("phones-v-1")))
            .build();

    final MapObjectDto changedDto =
        mapObjectDto
            .withIdsMap(map("id-k-2", customString("id-v-2")))
            .withUsernamesMap(empty())
            .withEmailsMap(empty())
            .withPhonesMap(Tristate.ofAbsent());

    assertEquals(map("id-k-2", customString("id-v-2")), changedDto.getIdsMap());
    assertEquals(empty(), changedDto.getUsernamesMapOpt());
    assertEquals(empty(), changedDto.getEmailsMapOpt());
    assertEquals(Tristate.ofAbsent(), changedDto.getPhonesMapTristate());
  }
}
