package com.github.muehmar.gradle.openapi.writer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/** Writes the content to a file. */
public class FileWriter extends BaseWriter {

  private final String basePath;

  public FileWriter(String basePath) {
    this.basePath = basePath;
  }

  @Override
  public boolean close(String subPath) {
    try {
      final Path file = Paths.get(basePath).resolve(subPath);
      file.toFile().mkdirs();

      Files.delete(file);

      try (final PrintWriter writer =
          new PrintWriter(
              new OutputStreamWriter(
                  new FileOutputStream(file.toFile()), StandardCharsets.UTF_8))) {

        writer.append(sb.toString());
        writer.flush();
      }
      return false;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
