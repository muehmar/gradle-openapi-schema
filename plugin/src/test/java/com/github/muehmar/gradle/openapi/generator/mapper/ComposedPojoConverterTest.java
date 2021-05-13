package com.github.muehmar.gradle.openapi.generator.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.data.SampleTypes;
import java.util.Comparator;
import org.junit.jupiter.api.Test;

class ComposedPojoConverterTest {

  @Test
  void convert_when_twoPojosAndOneAllOfComposedPojos_then_composedPojoCreated() {

    final Name tiresName = Name.of("Tires");
    final Name colorName = Name.of("Color");

    final Pojo tiresPojo =
        new Pojo(
            tiresName,
            "Tires",
            "Dto",
            PList.of(
                new PojoMember(Name.of("tireKey"), "Key", SampleTypes.SampleType1, false),
                new PojoMember(Name.of("tireName"), "Name", SampleTypes.SampleType2, false)),
            false);

    final Pojo colorPojo =
        new Pojo(
            colorName,
            "Colors",
            "Dto",
            PList.of(
                new PojoMember(Name.of("colorKey"), "Key", SampleTypes.SampleType2, true),
                new PojoMember(Name.of("colorName"), "Name", SampleTypes.SampleType1, true)),
            false);

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
        new Pojo(
            composedPojo.getName(),
            composedPojo.getDescription(),
            composedPojo.getSuffix(),
            colorPojo.getMembers().concat(tiresPojo.getMembers()),
            false),
        resultingPojos.apply(1));
  }
}
