package com.github.muehmar.gradle.openapi.generator.java.ref;

import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import io.github.muehmar.codegenerator.Generator;

public class JacksonRefs {
  private JacksonRefs() {}

  public static final String JSON_VALUE = "com.fasterxml.jackson.annotation.JsonValue";
  public static final String JSON_CREATOR = "com.fasterxml.jackson.annotation.JsonCreator";
  public static final String JSON_ANY_GETTER = "com.fasterxml.jackson.annotation.JsonAnyGetter";
  public static final String JSON_ANY_SETTER = "com.fasterxml.jackson.annotation.JsonAnySetter";
  public static final String JSON_IGNORE = "com.fasterxml.jackson.annotation.JsonIgnore";
  public static final String JSON_INCLUDE = "com.fasterxml.jackson.annotation.JsonInclude";
  public static final String JSON_PROPERTY = "com.fasterxml.jackson.annotation.JsonProperty";
  public static final String JSON_FORMAT = "com.fasterxml.jackson.annotation.JsonFormat";

  public static <A> Generator<A, PojoSettings> jsonPojoBuilderRef() {
    return (a, settings, w) -> {
      if (settings.getJsonSupport() == JsonSupport.JACKSON_2) {
        return w.ref("com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder");
      } else if (settings.getJsonSupport() == JsonSupport.JACKSON_3) {
        return w.ref("tools.jackson.databind.annotation.JsonPOJOBuilder");
      } else {
        return w;
      }
    };
  }

  public static <A> Generator<A, PojoSettings> jsonDeserializeRef() {
    return (a, settings, w) -> {
      if (settings.getJsonSupport() == JsonSupport.JACKSON_2) {
        return w.ref("com.fasterxml.jackson.databind.annotation.JsonDeserialize");
      } else if (settings.getJsonSupport() == JsonSupport.JACKSON_3) {
        return w.ref("tools.jackson.databind.annotation.JsonDeserialize");
      } else {
        return w;
      }
    };
  }

  public static <A> Generator<A, PojoSettings> xmlRootElementRef() {
    return (a, settings, w) -> {
      if (settings.getXmlSupport() == XmlSupport.JACKSON_2) {
        return w.ref("com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement");
      } else if (settings.getXmlSupport() == XmlSupport.JACKSON_3) {
        return w.ref("tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement");
      } else {
        return w;
      }
    };
  }

  public static String xmlPropertyRef(PojoSettings settings) {
    if (settings.getXmlSupport() == XmlSupport.JACKSON_2) {
      return "com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty";
    } else if (settings.getXmlSupport() == XmlSupport.JACKSON_3) {
      return "tools.jackson.dataformat.xml.annotation.JacksonXmlProperty";
    } else {
      return "";
    }
  }

  public static String xmlElementWrapperRef(PojoSettings settings) {
    if (settings.getXmlSupport() == XmlSupport.JACKSON_2) {
      return "com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper";
    } else if (settings.getXmlSupport() == XmlSupport.JACKSON_3) {
      return "tools.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper";
    } else {
      return "";
    }
  }
}
