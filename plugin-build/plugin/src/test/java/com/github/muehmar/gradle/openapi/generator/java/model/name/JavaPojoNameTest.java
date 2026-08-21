package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JavaPojoNameTest {
  @Test
  void append_when_otherPojoNameSupplied_then_appendedCorrectly() {
    final JavaPojoName javaPojoName = JavaPojoNames.patientName();

    final JavaPojoName appendedPojoName = javaPojoName.append(JavaPojoNames.invoiceName());

    assertEquals("PatientInvoiceDto", appendedPojoName.asString());
  }
}
