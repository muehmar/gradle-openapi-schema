package com.github.muehmar.gradle.openapi.writer;

import java.nio.file.Path;
import lombok.Value;

@Value
public class GeneratedFile {
  Path file;
  String content;
}
