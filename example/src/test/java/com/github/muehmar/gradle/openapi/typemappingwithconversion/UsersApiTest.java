package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Optionals.opt;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.customList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.emptyCustomList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.ListObjectDto.fullListObjectDtoBuilder;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.AdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class UsersApiTest {
  @Test
  void stagedBuilder_when_standardSetterUsed_then_getterReturnCorrectValues() {
    final ListObjectDto ListObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    assertEquals(customList(opt(customString("id-1"))), ListObjectDto.getIds());
    assertEquals(opt(customList(opt(customString("username-1")))), ListObjectDto.getUsernamesOpt());
    assertEquals(opt(customList(opt(customString("email-1")))), ListObjectDto.getEmailsOpt());
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-1")))),
        ListObjectDto.getPhonesTristate());
  }

  @Test
  void stagedBuilder_when_overloadedSettersUsed_then_getterReturnCorrectValues() {
    final ListObjectDto ListObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(opt(customList(customString("username-1"))))
            .setEmails(opt(customList(customString("email-1"))))
            .setPhones(Tristate.ofValue(customList(customString("phone-1"))))
            .build();

    assertEquals(customList(opt(customString("id-1"))), ListObjectDto.getIds());
    assertEquals(opt(customList(opt(customString("username-1")))), ListObjectDto.getUsernamesOpt());
    assertEquals(opt(customList(opt(customString("email-1")))), ListObjectDto.getEmailsOpt());
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-1")))),
        ListObjectDto.getPhonesTristate());
  }

  @Test
  void stagedBuilder_when_overloadedSettersUsedWithoutValues_then_getterReturnCorrectValues() {
    final ListObjectDto ListObjectDto =
        fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(empty())
            .setEmails(empty())
            .setPhones(Tristate.ofAbsent())
            .build();

    assertEquals(customList(opt(customString("id-1"))), ListObjectDto.getIds());
    assertEquals(empty(), ListObjectDto.getUsernamesOpt());
    assertEquals(empty(), ListObjectDto.getEmailsOpt());
    assertEquals(Tristate.ofAbsent(), ListObjectDto.getPhonesTristate());
  }

  @Test
  void stagedBuilder_when_standardNullableItemsSetterUsed_then_getterReturnCorrectValues() {
    final ListObjectDto listObjectDto =
        fullListObjectDtoBuilder()
            .setIds_(customList(opt(customString("id-1")), empty()))
            .setUsernames_(customList(opt(customString("username-1")), empty()))
            .setEmails_(customList(opt(customString("email-1")), empty()))
            .setPhones_(customList(opt(customString("phone-1")), empty()))
            .build();

    assertEquals(customList(opt(customString("id-1")), empty()), listObjectDto.getIds());
    assertEquals(
        opt(customList(opt(customString("username-1")), empty())), listObjectDto.getUsernamesOpt());
    assertEquals(
        opt(customList(opt(customString("email-1")), empty())), listObjectDto.getEmailsOpt());
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-1")), empty())),
        listObjectDto.getPhonesTristate());
  }

  @Test
  void stagedBuilder_when_overloadedNullableItemsSetterUsed_then_getterReturnCorrectValues() {
    final ListObjectDto listObjectDto =
        fullListObjectDtoBuilder()
            .setIds_(customList(opt(customString("id-1")), empty()))
            .setUsernames_(opt(customList(opt(customString("username-1")), empty())))
            .setEmails_(opt(customList(opt(customString("email-1")), empty())))
            .setPhones_(Tristate.ofValue(customList(opt(customString("phone-1")), empty())))
            .build();

    assertEquals(customList(opt(customString("id-1")), empty()), listObjectDto.getIds());
    assertEquals(
        opt(customList(opt(customString("username-1")), empty())), listObjectDto.getUsernamesOpt());
    assertEquals(
        opt(customList(opt(customString("email-1")), empty())), listObjectDto.getEmailsOpt());
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-1")), empty())),
        listObjectDto.getPhonesTristate());
  }

  @Test
  void
      stagedBuilder_when_overloadedNullableItemsSetterUsedWithoutValues_then_getterReturnCorrectValues() {
    final ListObjectDto listObjectDto =
        fullListObjectDtoBuilder()
            .setIds_(customList(opt(customString("id-1")), empty()))
            .setUsernames_(empty())
            .setEmails_(empty())
            .setPhones_(Tristate.ofAbsent())
            .build();

    assertEquals(customList(opt(customString("id-1")), empty()), listObjectDto.getIds());
    assertEquals(empty(), listObjectDto.getUsernamesOpt());
    assertEquals(empty(), listObjectDto.getEmailsOpt());
    assertEquals(Tristate.ofAbsent(), listObjectDto.getPhonesTristate());
  }

  @Test
  void stagedBuilder_when_additionalPropertiesSettersUsed_then_returnsCorrectProperties() {
    final ListObjectDto listObjectDto =
        fullListObjectDtoBuilder()
            .setIds(emptyCustomList())
            .setUsernames(emptyCustomList())
            .setEmails(emptyCustomList())
            .setPhones(emptyCustomList())
            .setAdditionalProperties(singletonMap("key-0", customString("value-0")))
            .addAdditionalProperty("key-1", customString("value-1"))
            .addAdditionalProperty("key-2", opt(customString("value-2")))
            .build();

    assertEquals(opt(customString("value-0")), listObjectDto.getAdditionalProperty("key-0"));
    assertEquals(
        Arrays.asList(
            new AdditionalProperty<>("key-0", customString("value-0")),
            new AdditionalProperty<>("key-1", customString("value-1")),
            new AdditionalProperty<>("key-2", customString("value-2"))),
        listObjectDto.getAdditionalProperties().stream()
            .sorted(comparing(AdditionalProperty::getName))
            .collect(toList()));
  }

  @Test
  void withers_when_standardWithersUsed_then_propertiesSetCorrectly() {
    final ListObjectDto listObjectDto =
        ListObjectDto.fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    final ListObjectDto changedDto =
        listObjectDto
            .withIds(customList(customString("id-2")))
            .withUsernames(customList(customString("username-2")))
            .withEmails(customList(customString("email-2")))
            .withPhones(customList(customString("phone-2")));

    assertEquals(customList(opt(customString("id-2"))), changedDto.getIds());
    assertEquals(customList(opt(customString("username-2"))), changedDto.getUsernamesOr(null));
    assertEquals(customList(opt(customString("email-2"))), changedDto.getEmailsOr(null));
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-2")))), changedDto.getPhonesTristate());
  }

  @Test
  void withers_when_overloadedWithersUsed_then_propertiesSetCorrectly() {
    final ListObjectDto listObjectDto =
        ListObjectDto.fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    final ListObjectDto changedDto =
        listObjectDto
            .withUsernames(opt(customList(customString("username-2"))))
            .withEmails(opt(customList(customString("email-2"))))
            .withPhones(Tristate.ofValue(customList(customString("phone-2"))));

    assertEquals(customList(opt(customString("username-2"))), changedDto.getUsernamesOr(null));
    assertEquals(customList(opt(customString("email-2"))), changedDto.getEmailsOr(null));
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-2")))), changedDto.getPhonesTristate());
  }

  @Test
  void withers_when_overloadedWithersUsedWithoutValues_then_propertiesSetCorrectly() {
    final ListObjectDto listObjectDto =
        ListObjectDto.fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    final ListObjectDto changedDto =
        listObjectDto.withUsernames(empty()).withEmails(empty()).withPhones(Tristate.ofAbsent());

    assertEquals(empty(), changedDto.getUsernamesOpt());
    assertEquals(empty(), changedDto.getEmailsOpt());
    assertEquals(Tristate.ofAbsent(), changedDto.getPhonesTristate());
  }

  @Test
  void withers_when_standardNullableItemsListWithersUsed_then_propertiesSetCorrectly() {
    final ListObjectDto listObjectDto =
        ListObjectDto.fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    final ListObjectDto changedDto =
        listObjectDto
            .withIds_(customList(opt(customString("id-2")), empty()))
            .withUsernames_(customList(opt(customString("username-2")), empty()))
            .withEmails_(customList(opt(customString("email-2")), empty()))
            .withPhones_(customList(opt(customString("phone-2")), empty()));

    assertEquals(customList(opt(customString("id-2")), empty()), changedDto.getIds());
    assertEquals(
        customList(opt(customString("username-2")), empty()), changedDto.getUsernamesOr(null));
    assertEquals(customList(opt(customString("email-2")), empty()), changedDto.getEmailsOr(null));
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-2")), empty())),
        changedDto.getPhonesTristate());
  }

  @Test
  void withers_when_overloadedNullableItemsListWithersUsed_then_propertiesSetCorrectly() {
    final ListObjectDto listObjectDto =
        ListObjectDto.fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    final ListObjectDto changedDto =
        listObjectDto
            .withUsernames_(opt(customList(opt(customString("username-2")), empty())))
            .withEmails_(opt(customList(opt(customString("email-2")), empty())))
            .withPhones_(Tristate.ofValue(customList(opt(customString("phone-2")), empty())));

    assertEquals(
        customList(opt(customString("username-2")), empty()), changedDto.getUsernamesOr(null));
    assertEquals(customList(opt(customString("email-2")), empty()), changedDto.getEmailsOr(null));
    assertEquals(
        Tristate.ofValue(customList(opt(customString("phone-2")), empty())),
        changedDto.getPhonesTristate());
  }

  @Test
  void
      withers_when_overloadedNullableItemsListWithersUsedWithoutValues_then_propertiesSetCorrectly() {
    final ListObjectDto listObjectDto =
        ListObjectDto.fullListObjectDtoBuilder()
            .setIds(customList(customString("id-1")))
            .setUsernames(customList(customString("username-1")))
            .setEmails(customList(customString("email-1")))
            .setPhones(customList(customString("phone-1")))
            .build();

    final ListObjectDto changedDto =
        listObjectDto.withUsernames_(empty()).withEmails_(empty()).withPhones_(Tristate.ofNull());

    assertEquals(empty(), changedDto.getUsernamesOpt());
    assertEquals(empty(), changedDto.getEmailsOpt());
    assertEquals(Tristate.ofNull(), changedDto.getPhonesTristate());
  }
}
