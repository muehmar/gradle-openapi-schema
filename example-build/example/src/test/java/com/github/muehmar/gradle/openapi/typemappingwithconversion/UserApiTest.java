package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Optionals.opt;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.NullableAdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

public class UserApiTest {

  @Test
  void stagedBuilder_when_additionalPropertiesSettersUsed_then_returnsCorrectProperties() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setId(customString("id-1"))
            .setUsername(Optional.empty())
            .setEmail(Optional.empty())
            .setPhone(Tristate.ofNull())
            .setAdditionalProperties(singletonMap("key-0", customString("value-0")))
            .addAdditionalProperty("key-1", customString("value-1"))
            .addAdditionalProperty("key-2", Tristate.ofValue(customString("value-2")))
            .build();

    assertEquals(Tristate.ofValue(customString("value-0")), userDto.getAdditionalProperty("key-0"));
    assertEquals(
        Arrays.asList(
            NullableAdditionalProperty.ofNullable("key-0", customString("value-0")),
            NullableAdditionalProperty.ofNullable("key-1", customString("value-1")),
            NullableAdditionalProperty.ofNullable("key-2", customString("value-2"))),
        userDto.getAdditionalProperties().stream()
            .sorted(comparing(NullableAdditionalProperty::getName))
            .collect(Collectors.toList()));
  }

  @Test
  void withers_when_standardWithersUsed_then_propertiesSetCorrectly() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setId(customString("id-1"))
            .setUsername(Optional.empty())
            .setEmail(Optional.empty())
            .setPhone(Tristate.ofNull())
            .build();

    final UserDto changedDto =
        userDto
            .withId(customString("id-2"))
            .withUsername(customString("username"))
            .withEmail(customString("email"))
            .withPhone(customString("phone"));

    assertEquals(customString("id-2"), changedDto.getId());
    assertEquals(customString("username"), changedDto.getUsernameOr(null));
    assertEquals(customString("email"), changedDto.getEmailOr(null));
    assertEquals(Tristate.ofValue(customString("phone")), changedDto.getPhoneTristate());
  }

  @Test
  void withers_when_overloadedWithersUsed_then_propertiesSetCorrectly() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setId(customString("id-1"))
            .setUsername(Optional.empty())
            .setEmail(Optional.empty())
            .setPhone(Tristate.ofNull())
            .build();

    final UserDto changedDto =
        userDto
            .withId(customString("id-2"))
            .withUsername(opt(customString("username")))
            .withEmail(opt(customString("email")))
            .withPhone(Tristate.ofValue(customString("phone")));

    assertEquals(customString("id-2"), changedDto.getId());
    assertEquals(customString("username"), changedDto.getUsernameOr(null));
    assertEquals(customString("email"), changedDto.getEmailOr(null));
    assertEquals(Tristate.ofValue(customString("phone")), changedDto.getPhoneTristate());
  }

  @Test
  void withers_when_overloadedWithersUsedWithoutValues_then_propertiesSetCorrectly() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setId(customString("id-1"))
            .setUsername(customString("username"))
            .setEmail(customString("email"))
            .setPhone(customString("phone"))
            .build();

    final UserDto changedDto =
        userDto
            .withId(customString("id-2"))
            .withUsername(Optional.empty())
            .withEmail(Optional.empty())
            .withPhone(Tristate.ofAbsent());

    assertEquals(customString("id-2"), changedDto.getId());
    assertEquals(Optional.empty(), changedDto.getUsernameOpt());
    assertEquals(Optional.empty(), changedDto.getEmailOpt());
    assertEquals(Tristate.ofAbsent(), changedDto.getPhoneTristate());
  }

  @Test
  void withers_when_overloadedWithersUsedWithTristateNull_then_propertiesSetCorrectly() {
    final UserDto userDto =
        UserDto.fullUserDtoBuilder()
            .setId(customString("id-1"))
            .setUsername(customString("username"))
            .setEmail(customString("email"))
            .setPhone(customString("phone"))
            .build();

    final UserDto changedDto = userDto.withPhone(Tristate.ofNull());

    assertEquals(Tristate.ofNull(), changedDto.getPhoneTristate());
  }
}
