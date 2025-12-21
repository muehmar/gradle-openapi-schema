package com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonJson;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.Filters.isJacksonXml;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.ZONED_DATE_TIME_DESERIALIZER_CLASSNAME;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberXml;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;
import java.util.function.Function;

public class JacksonAnnotationGenerator {
  private JacksonAnnotationGenerator() {}

  public static Generator<JavaPojoMember, PojoSettings> jsonProperty() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append((f, s, w) -> w.println("@JsonProperty(\"%s\")", f.getName().getOriginalName()))
        .append(w -> w.ref(JacksonRefs.JSON_PROPERTY))
        .filter(isJacksonJson());
  }

  public static Generator<JavaPojoMember, PojoSettings> jsonFormat() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .append(Generator.constant("@JsonFormat(shape = JsonFormat.Shape.STRING)"))
        .append(w -> w.ref(JacksonRefs.JSON_FORMAT))
        .filter(
            m ->
                m.getJavaType().getQualifiedClassName().equals(QualifiedClassNames.ZONED_DATE_TIME))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonIgnore() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonIgnore"))
        .append(w -> w.ref(JacksonRefs.JSON_IGNORE))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonIncludeNonNull() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonInclude(JsonInclude.Include.NON_NULL)"))
        .append(w -> w.ref(JacksonRefs.JSON_INCLUDE))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonValue() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonValue"))
        .append(w -> w.ref(JacksonRefs.JSON_VALUE))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonCreator() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonCreator"))
        .append(w -> w.ref(JacksonRefs.JSON_CREATOR))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonAnyGetter() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonAnyGetter"))
        .append(w -> w.ref(JacksonRefs.JSON_ANY_GETTER))
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonAnySetter() {
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonAnySetter"))
        .append(w -> w.ref(JacksonRefs.JSON_ANY_SETTER))
        .filter(isJacksonJson());
  }

  private static <A> Generator<A, PojoSettings> jsonPojoBuilder(Optional<String> prefix) {
    final String prefixString =
        prefix.map(p -> String.format("(withPrefix = \"%s\")", p)).orElse("");
    return Generator.<A, PojoSettings>emptyGen()
        .append(w -> w.println("@JsonPOJOBuilder%s", prefixString))
        .append(JacksonRefs.jsonPojoBuilderRef())
        .filter(isJacksonJson());
  }

  public static <A> Generator<A, PojoSettings> jsonPojoBuilderWithPrefix(String prefix) {
    return jsonPojoBuilder(Optional.of(prefix));
  }

  public static <T extends JavaPojo> Generator<T, PojoSettings> jsonDeserializeForBuilder() {
    return Generator.<T, PojoSettings>emptyGen()
        .append(
            (pojo, settings, writer) ->
                writer.println("@JsonDeserialize(builder = %s.Builder.class)", pojo.getClassName()))
        .append(JacksonRefs.jsonDeserializeRef())
        .filter(isJacksonJson());
  }

  public static <T extends JavaPojoMember> Generator<T, PojoSettings> jsonDeserializeForMember() {
    return Generator.<T, PojoSettings>emptyGen()
        .append(
            (member, settings, writer) ->
                writer.println(
                    "@JsonDeserialize(using = %s.class)", ZONED_DATE_TIME_DESERIALIZER_CLASSNAME))
        .append(JacksonRefs.jsonDeserializeRef())
        .append(w -> w.ref(OpenApiUtilRefs.ZONED_DATE_TIME_DESERIALIZER))
        .filter(
            member ->
                member
                    .getJavaType()
                    .getQualifiedClassName()
                    .equals(QualifiedClassNames.ZONED_DATE_TIME))
        .filter(isJacksonJson());
  }

  public static Generator<JavaObjectPojo, PojoSettings> jacksonXmlRootElement() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) -> {
              final String name =
                  pojo.getPojoXml()
                      .getName()
                      .orElse(pojo.getSchemaName().getOriginalName().asString());
              final String annotationValues =
                  PList.of(Optional.of(name).map(n -> String.format("localName = \"%s\"", n)))
                      .flatMapOptional(Function.identity())
                      .mkString(", ");
              return w.println("@JacksonXmlRootElement(%s)", annotationValues);
            })
        .append(JacksonRefs.xmlRootElementRef())
        .filter(isJacksonXml());
  }

  public static Generator<JavaPojoMember, PojoSettings> jacksonXmlProperty() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendConditionally(
            (m, s, w) -> {
              final JavaPojoMemberXml xml = m.getMemberXml();
              final PList<String> annotationValues =
                  xml.getArrayXml()
                      .map(
                          arrayXml ->
                              arrayXml
                                  .getItemName()
                                  .map(
                                      name ->
                                          PList.single(
                                              Optional.of(
                                                  String.format("localName = \"%s\"", name))))
                                  .orElse(PList.empty()))
                      .orElseGet(
                          () -> {
                            final Optional<String> name =
                                Optional.of(
                                    xml.getName().orElse(m.getName().getOriginalName().asString()));
                            return PList.of(
                                name.map(n -> String.format("localName = \"%s\"", n)),
                                xml.getIsAttribute()
                                    .map(flag -> String.format("isAttribute = %s", flag)));
                          })
                      .flatMapOptional(Function.identity());

              return NonEmptyList.fromIter(annotationValues)
                  .map(
                      values ->
                          w.println("@JacksonXmlProperty(%s)", values.toPList().mkString(", "))
                              .ref(JacksonRefs.xmlPropertyRef(s)))
                  .orElse(w);
            },
            m -> m.getMemberXml().hasDefinition())
        .filter(isJacksonXml());
  }

  public static Generator<JavaPojoMember, PojoSettings> jacksonXmlElementWrapper() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendOptional(
            (arrayXml, s, w) -> {
              if (arrayXml.getWrapped().isPresent() || arrayXml.getWrapperName().isPresent()) {

                final String annotationValues =
                    PList.of(
                            arrayXml
                                .getWrapperName()
                                .map(n -> String.format("localName = \"%s\"", n)),
                            arrayXml
                                .getWrapped()
                                .map(flag -> String.format("useWrapping = %s", flag)))
                        .flatMapOptional(Function.identity())
                        .mkString(", ");
                return w.println("@JacksonXmlElementWrapper(%s)", annotationValues)
                    .ref(JacksonRefs.xmlElementWrapperRef(s));
              } else {
                return w;
              }
            },
            m -> m.getMemberXml().getArrayXml())
        .filter(isJacksonXml());
  }
}
