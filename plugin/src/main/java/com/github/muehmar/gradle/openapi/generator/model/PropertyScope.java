package com.github.muehmar.gradle.openapi.generator.model;

public enum PropertyScope {
  READ_ONLY(true, false),
  WRITE_ONLY(false, true),
  DEFAULT(true, true);

  private final boolean usedInResponse;
  private final boolean usedInRequest;

  PropertyScope(boolean usedInResponse, boolean usedInRequest) {
    this.usedInResponse = usedInResponse;
    this.usedInRequest = usedInRequest;
  }

  public boolean isUsedInResponse() {
    return usedInResponse;
  }

  public boolean isUsedInRequest() {
    return usedInRequest;
  }
}
