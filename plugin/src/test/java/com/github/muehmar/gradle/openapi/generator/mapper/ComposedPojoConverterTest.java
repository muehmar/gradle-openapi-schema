package com.github.muehmar.gradle.openapi.generator.mapper;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.SampleTypes;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

class ComposedPojoConverterTest {

  @Test
  void convert_when_twoPojosAndOneAllOfComposedPojos_then_composedPojoCreated() {

    final Name tiresName = Name.of("Tires");
    final Name colorName = Name.of("Color");

    final Pojo tiresPojo =
        Pojo.ofObject(
            tiresName,
            "Tires",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.of("tireKey"), "Key", SampleTypes.SampleType1, REQUIRED, NOT_NULLABLE),
                new PojoMember(
                    Name.of("tireName"), "Name", SampleTypes.SampleType2, REQUIRED, NOT_NULLABLE)));

    final Pojo colorPojo =
        Pojo.ofObject(
            colorName,
            "Colors",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.of("colorKey"), "Key", SampleTypes.SampleType2, OPTIONAL, NOT_NULLABLE),
                new PojoMember(
                    Name.of("colorName"),
                    "Name",
                    SampleTypes.SampleType1,
                    OPTIONAL,
                    NOT_NULLABLE)));

    final ComposedPojo composedPojo =
        new ComposedPojo(
            Name.of("Composed"),
            "Description",
            "Dto",
            ComposedPojo.CompositionType.ALL_OF,
            PList.of(colorName, tiresName),
            PList.empty());

    final PList<Pojo> resultingPojos =
        ComposedPojoConverter.convert(PList.single(composedPojo), PList.of(colorPojo, tiresPojo))
            .sort(Comparator.comparing(pojo -> pojo.getName().asString()));

    assertEquals(3, resultingPojos.size());

    assertEquals(colorPojo, resultingPojos.apply(0));
    assertEquals(tiresPojo, resultingPojos.apply(2));

    assertEquals(
        Pojo.ofObject(
            composedPojo.getName(),
            composedPojo.getDescription(),
            composedPojo.getSuffix(),
            colorPojo.getMembers().concat(tiresPojo.getMembers())),
        resultingPojos.apply(1));
  }
}
