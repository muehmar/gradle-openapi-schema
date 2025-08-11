package com.github.muehmar.gradle.openapi.writer;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class BaseDirFileWriter implements FileWriter {
  private final String outputDir;

  public BaseDirFileWriter(String outputDir) {
    this.outputDir = outputDir;
  }

  @Override
  public void writeFile(GeneratedFile file) {
    try {
      final Path completePath = Path.of(outputDir).resolve(file.getFile());
      completePath.toFile().mkdirs();

      Files.delete(completePath);

      try (final PrintWriter pw =
          new PrintWriter(
              new OutputStreamWriter(
                  Files.newOutputStream(completePath.toFile().toPath()), StandardCharsets.UTF_8))) {

        pw.append(file.getContent());
        pw.flush();
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
