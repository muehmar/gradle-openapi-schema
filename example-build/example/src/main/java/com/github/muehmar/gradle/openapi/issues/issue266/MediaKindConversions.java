package com.github.muehmar.gradle.openapi.issues.issue266;

/**
 * Holds the conversion towards {@link MediaKind}, i.e. a conversion class which differs from the
 * mapped type itself. The generated dto therefore needs an import for this class, which is only
 * added if the refs registered by the rendered conversion are transferred to the writer of the dto.
 */
public class MediaKindConversions {

  private MediaKindConversions() {}

  public static MediaKind fromDto(MappedDiscriminatorKindDto dto) {
    return new MediaKind(dto.getValue());
  }
}
