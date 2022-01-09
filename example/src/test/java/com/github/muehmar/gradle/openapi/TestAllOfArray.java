package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import OpenApiSchema.example.api.model.ReportsDto;
import OpenApiSchema.example.api.model.ReportsInvoiceDto;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TestAllOfArray {
  @Test
  void newBuilder_when_calledForAllOfInArray_then_allPropertiesPresent() {
    final ReportsInvoiceDto reportsInvoiceDto =
        ReportsInvoiceDto.newBuilder()
            .setColorKey(15)
            .setColorName("Blue")
            .setBoolFlag(true)
            .setIntFlag(1)
            .andAllOptionals()
            .build();
    final ArrayList<ReportsInvoiceDto> invoiceReports = new ArrayList<>();
    invoiceReports.add(reportsInvoiceDto);
    final ReportsDto reportsDto =
        ReportsDto.newBuilder().andAllOptionals().setInvoice(invoiceReports).build();

    assertEquals(Optional.of(1), reportsDto.getInvoiceOptional().map(ArrayList::size));
  }
}
