package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;
import org.gradle.api.GradleException;

public class FileSpecificationReader implements SpecificationReader {
  @Override
  public String read(MainDirectory mainDirectory, OpenApiSpec specification) {
    try (final FileInputStream fileInputStream =
            new FileInputStream(specification.asPathWithMainDirectory(mainDirectory).toFile());
        final BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(fileInputStream))) {
      return bufferedReader.lines().collect(Collectors.joining("\n"));
    } catch (IOException e) {
      throw new GradleException(
          "Unable to read the specification '"
              + specification.asPathWithMainDirectory(mainDirectory).toString()
              + "'",
          e);
    }
  }
}
