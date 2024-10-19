package com.github.muehmar.gradle.openapi.multitypepoc;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class SuperObject {
  private final MultiType feature;
  private final String hello;

  public SuperObject(
      @JsonProperty("feature") MultiType feature, @JsonProperty("hello") String hello) {
    this.feature = feature;
    this.hello = hello;
  }

  @JsonProperty("feature")
  public MultiType getFeature() {
    return feature;
  }

  @JsonProperty("hello")
  public String getHello() {
    return hello;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final SuperObject that = (SuperObject) o;
    return Objects.equals(feature, that.feature) && Objects.equals(hello, that.hello);
  }

  @Override
  public int hashCode() {
    return Objects.hash(feature, hello);
  }

  @Override
  public String toString() {
    return "SuperObject{" + "feature=" + feature + ", hello='" + hello + '\'' + '}';
  }
}
