package com.github.muehmar.gradle.openapi.generator.java.ref;

import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import io.github.muehmar.codegenerator.Generator;
import java.util.function.Function;

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

  public static String jsonParserRef(PojoSettings settings) {
    return jsonRefString(settings, "jackson.core.JsonParser");
  }

  public static String deserializationContextRef(PojoSettings settings) {
    return jsonRefString(settings, "jackson.databind.DeserializationContext");
  }

  public static String jsonDeserializerRef(PojoSettings settings) {
    return jsonRefString(
        settings, "jackson.databind.JsonDeserializer", "jackson.databind.ValueDeserializer");
  }

  public static String jsonPojoBuilderRef(PojoSettings settings) {
    return jsonRefString(settings, "jackson.databind.annotation.JsonPOJOBuilder");
  }

  public static String jsonDeserializeRef(PojoSettings settings) {
    return jsonRefString(settings, "jackson.databind.annotation.JsonDeserialize");
  }

  public static String xmlRootElementRef(PojoSettings settings) {
    return xmlRefString(settings, "jackson.dataformat.xml.annotation.JacksonXmlRootElement");
  }

  public static String xmlPropertyRef(PojoSettings settings) {
    return xmlRefString(settings, "jackson.dataformat.xml.annotation.JacksonXmlProperty");
  }

  public static String xmlElementWrapperRef(PojoSettings settings) {
    return xmlRefString(settings, "jackson.dataformat.xml.annotation.JacksonXmlElementWrapper");
  }

  private static String jsonRefString(PojoSettings settings, String refSuffix) {
    return jsonRefString(settings, refSuffix, refSuffix);
  }

  public static <A> Generator<A, PojoSettings> generator(
      Function<PojoSettings, String> refFunction) {
    return (a, settings, w) -> {
      final String ref = refFunction.apply(settings);
      return ref.isEmpty() ? w : w.ref(ref);
    };
  }

  private static String jsonRefString(
      PojoSettings settings, String jackson2RefSuffix, String jackson3RefSuffix) {
    if (settings.getJsonSupport() == JsonSupport.JACKSON_2) {
      return "com.fasterxml." + jackson2RefSuffix;
    } else if (settings.getJsonSupport() == JsonSupport.JACKSON_3) {
      return "tools." + jackson3RefSuffix;
    } else {
      return "";
    }
  }

  private static String xmlRefString(PojoSettings settings, String refSuffix) {
    return xmlRefString(settings, refSuffix, refSuffix);
  }

  private static String xmlRefString(
      PojoSettings settings, String jackson2RefSuffix, String jackson3RefSuffix) {
    if (settings.getXmlSupport() == XmlSupport.JACKSON_2) {
      return "com.fasterxml." + jackson2RefSuffix;
    } else if (settings.getXmlSupport() == XmlSupport.JACKSON_3) {
      return "tools." + jackson3RefSuffix;
    } else {
      return "";
    }
  }
}
