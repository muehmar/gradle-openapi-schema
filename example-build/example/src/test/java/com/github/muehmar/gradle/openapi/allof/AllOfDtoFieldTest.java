package com.github.muehmar.gradle.openapi.allof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class AllOfDtoFieldTest {
  @Test
  void getDeclaredFields_when_invoiceHasAllFieldsFromBaseOrderAndInvoiceAllOf() {
    final ArrayList<Field> allFields = new ArrayList<>();
    allFields.addAll(removeAdditionalPropertiesField(InvoiceAllOfDto.class.getDeclaredFields()));
    allFields.addAll(removeAdditionalPropertiesField(BaseOrderDto.class.getDeclaredFields()));
    final List<Field> invoiceFields =
        removeAdditionalPropertiesField(InvoiceDto.class.getDeclaredFields());

    assertEquals(allFields.size(), invoiceFields.size());

    final long missingFields =
        invoiceFields.stream()
            .filter(field -> allFields.stream().noneMatch(areSame(field)))
            .count();

    assertEquals(0, missingFields);
  }

  private static List<Field> removeAdditionalPropertiesField(Field[] fields) {
    return Arrays.stream(fields)
        .filter(f -> !f.getName().equals("additionalProperties"))
        .collect(Collectors.toList());
  }

  private static Predicate<Field> areSame(Field field) {
    return other ->
        other.getName().equals(field.getName()) && other.getType().equals(field.getType());
  }
}
