package com.github.muehmar.gradle.openapi.warnings;

import ch.bluecare.commons.data.PList;
import lombok.Value;

/** A collection of warning types which let the generation fail if they occur. */
@Value
public class FailingWarningTypes {
  PList<WarningType> types;
}
