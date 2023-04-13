package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;

public interface UtilsGenerator {
  PList<GeneratedFile> generateUtils();
}
