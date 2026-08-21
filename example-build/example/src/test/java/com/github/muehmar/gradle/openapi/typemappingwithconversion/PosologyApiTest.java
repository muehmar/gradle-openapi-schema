package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomList.customList;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

public class PosologyApiTest {
  @Test
  void buildAndGetItems_when_used_then_returnCorrectItems() {
    final PosologyDto posologyDto =
        PosologyDto.fromItems(
            CustomList.fromList(
                Stream.of("1", "2", "3", "0").map(CustomString::new).collect(toList())));

    assertEquals(
        CustomList.fromList(Stream.of("1", "2", "3", "0").map(CustomString::new).collect(toList())),
        posologyDto.getItems());
  }

  @Test
  void withItems_when_used_then_itemsSetCorrectly() {
    final PosologyDto posologyDto =
        PosologyDto.fromItems(
            CustomList.fromList(
                Stream.of("1", "2", "3", "0").map(CustomString::new).collect(toList())));

    final PosologyDto changedPosology =
        posologyDto.withItems(
            customList(customString("2"), customString("1"), customString("0"), customString("3")));

    assertEquals(
        CustomList.fromList(Stream.of("2", "1", "0", "3").map(CustomString::new).collect(toList())),
        changedPosology.getItems());
  }
}
