package com.github.muehmar.gradle.openapi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

class SuppliersTest {
  @Test
  void
      cached_when_calledWithSupplier_then_supplierCalledOnlyOnceWhenResultingSupplierIsCalledMultipleTimes() {
    final AtomicInteger atomicInteger = new AtomicInteger();
    final Supplier<Integer> supplier = Suppliers.cached(atomicInteger::incrementAndGet);

    assertEquals(1, supplier.get());
    assertEquals(1, supplier.get());
    assertEquals(1, supplier.get());
  }
}
