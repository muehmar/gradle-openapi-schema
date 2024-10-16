package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import java.util.Objects;

public class FhirData {
  private String data1;
  private Integer data2;

  // Jackson
  public FhirData() {}

  public FhirData(String data1, Integer data2) {
    this.data1 = data1;
    this.data2 = data2;
  }

  public String getData1() {
    return data1;
  }

  public void setData1(String data1) {
    this.data1 = data1;
  }

  public Integer getData2() {
    return data2;
  }

  public void setData2(Integer data2) {
    this.data2 = data2;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final FhirData fhirData = (FhirData) o;
    return Objects.equals(data1, fhirData.data1) && Objects.equals(data2, fhirData.data2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(data1, data2);
  }

  @Override
  public String toString() {
    return "FhirData{" + "data1='" + data1 + '\'' + ", data2=" + data2 + '}';
  }
}
