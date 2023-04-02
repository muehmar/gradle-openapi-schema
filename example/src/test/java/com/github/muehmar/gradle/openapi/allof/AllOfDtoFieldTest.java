package com.github.muehmar.gradle.openapi.allof;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.allof.model.BaseOrderDto;
import OpenApiSchema.example.api.allof.model.InvoiceAllOfDto;
import OpenApiSchema.example.api.allof.model.InvoiceDto;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

class AllOfDtoFieldTest {
  @Test
  void getDeclaredFields_when_invoiceHasAllFieldsFromBaseOrderAndInvoiceAllOf() {
    final ArrayList<Field> allFields = new ArrayList<>();
    allFields.addAll(Arrays.asList(InvoiceAllOfDto.class.getDeclaredFields()));
    allFields.addAll(Arrays.asList(BaseOrderDto.class.getDeclaredFields()));
    final List<Field> invoiceFields = Arrays.asList(InvoiceDto.class.getDeclaredFields());

    assertEquals(allFields.size(), invoiceFields.size());

    final long missingFields =
        invoiceFields.stream()
            .filter(field -> allFields.stream().noneMatch(areSame(field)))
            .count();

    assertEquals(0, missingFields);
  }

  private static Predicate<Field> areSame(Field field) {
    return other ->
        other.getName().equals(field.getName()) && other.getType().equals(field.getType());
  }
}
