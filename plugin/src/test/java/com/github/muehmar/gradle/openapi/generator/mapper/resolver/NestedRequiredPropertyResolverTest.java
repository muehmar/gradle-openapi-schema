package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NestedRequiredPropertyResolverTest {

  private static final PojoMember FIRST_NAME =
      new PojoMember(
          Name.ofString("firstName"),
          "firstName",
          StringType.noFormat(),
          PropertyScope.DEFAULT,
          Necessity.OPTIONAL,
          Nullability.NOT_NULLABLE);
  private static final PojoMember MIDDLE_NAME =
      new PojoMember(
          Name.ofString("middleName"),
          "middleName",
          StringType.noFormat(),
          PropertyScope.DEFAULT,
          Necessity.OPTIONAL,
          Nullability.NOT_NULLABLE);
  private static final PojoMember LAST_NAME =
      new PojoMember(
          Name.ofString("lastName"),
          "lastName",
          StringType.noFormat(),
          PropertyScope.DEFAULT,
          REQUIRED,
          Nullability.NOT_NULLABLE);
  private static final PojoMember STREET =
      new PojoMember(
          Name.ofString("street"),
          "street",
          StringType.noFormat(),
          PropertyScope.DEFAULT,
          REQUIRED,
          Nullability.NOT_NULLABLE);
  private static final PojoMember HOUSR_NR =
      new PojoMember(
          Name.ofString("housenr"),
          "housenr",
          IntegerType.formatInteger(),
          PropertyScope.DEFAULT,
          Necessity.OPTIONAL,
          Nullability.NOT_NULLABLE);
  private static final PojoMember ZIP =
      new PojoMember(
          Name.ofString("zip"),
          "zip",
          IntegerType.formatInteger(),
          PropertyScope.DEFAULT,
          Necessity.OPTIONAL,
          Nullability.NOT_NULLABLE);
  private static final PojoMember CITY_CODE =
      new PojoMember(
          Name.ofString("cityCode"),
          "cityCode",
          IntegerType.formatInteger(),
          PropertyScope.DEFAULT,
          Necessity.OPTIONAL,
          Nullability.NOT_NULLABLE);
  private static final PojoMember CITY_NAME =
      new PojoMember(
          Name.ofString("cityName"),
          "cityName",
          IntegerType.formatInteger(),
          PropertyScope.DEFAULT,
          REQUIRED,
          Nullability.NOT_NULLABLE);

  @Test
  void resolve_when_allOfPojoWithRequiredPropertiesPojo_then_correctResolved() {
    final ObjectPojo cityDto =
        Pojos.objectPojo(PList.of(ZIP, CITY_CODE, CITY_NAME))
            .withName(componentName("City", "Dto"));
    final ObjectPojo addressDto =
        Pojos.objectPojo(
                PList.of(
                    STREET,
                    HOUSR_NR,
                    PojoMembers.ofType(ObjectType.ofName(cityDto.getName().getPojoName()))
                        .withName(Name.ofString("city"))))
            .withName(componentName("Address", "Dto"));
    final ObjectPojo userDto =
        Pojos.objectPojo(
                PList.of(
                    FIRST_NAME,
                    MIDDLE_NAME,
                    LAST_NAME,
                    PojoMembers.ofType(ObjectType.ofName(addressDto.getName().getPojoName()))
                        .withName(Name.ofString("address"))
                        .withNecessity(OPTIONAL)))
            .withName(componentName("User", "Dto"));

    final ObjectPojo requiredCityPropsDto =
        Pojos.objectPojo(PList.empty())
            .withRequiredAdditionalProperties(PList.of(ZIP.getName()))
            .withName(componentName("RequiredCityProps", "Dto"));

    final ObjectPojo requiredAddressPropsDto =
        Pojos.objectPojo(
                PList.of(
                    PojoMembers.ofType(
                            ObjectType.ofName(requiredCityPropsDto.getName().getPojoName()))
                        .withName(Name.ofString("city"))))
            .withRequiredAdditionalProperties(PList.of(HOUSR_NR.getName()))
            .withName(componentName("RequiredAddressProps", "Dto"));

    final ObjectPojo requiredUserPropsDto =
        Pojos.objectPojo(
                PList.of(
                    PojoMembers.ofType(
                            ObjectType.ofName(requiredAddressPropsDto.getName().getPojoName()))
                        .withNecessity(REQUIRED)
                        .withName(Name.ofString("address"))))
            .withRequiredAdditionalProperties(PList.of(FIRST_NAME.getName()))
            .withName(componentName("RequiredUserProps", "Dto"));

    final ObjectPojo updateUserDto =
        Pojos.allOfPojo(userDto, requiredUserPropsDto).withName(componentName("UpdateUser", "Dto"));

    // method call
    final PList<Pojo> resolvedPojos =
        NestedRequiredPropertyResolver.resolve(
            PList.of(
                cityDto,
                addressDto,
                userDto,
                requiredCityPropsDto,
                requiredAddressPropsDto,
                requiredUserPropsDto,
                updateUserDto));

    assertEquals(
        PList.of(
                "UpdateUserUserDto",
                "RequiredCityPropsDto",
                "RequiredUserPropsDto",
                "RequiredAddressPropsDto",
                "UpdateUserDto",
                "UpdateUserCityDto",
                "UserDto",
                "UpdateUserAddressDto",
                "AddressDto",
                "CityDto")
            .toHashSet(),
        resolvedPojos.map(p -> p.getName().getPojoName().asString()).toHashSet());

    final ObjectPojo resolvedCityDto =
        cityDto
            .withMembers(PList.of(ZIP.withNecessity(REQUIRED), CITY_CODE, CITY_NAME))
            .withName(componentName("UpdateUserCity", "Dto", "City"));
    final ObjectPojo resolvedAddressDto =
        addressDto
            .withMembers(
                PList.of(
                    STREET,
                    HOUSR_NR.withNecessity(REQUIRED),
                    PojoMembers.ofType(ObjectType.ofName(resolvedCityDto.getName().getPojoName()))
                        .withName(Name.ofString("city"))))
            .withName(componentName("UpdateUserAddress", "Dto", "Address"));
    final ObjectPojo resolvedUserDto =
        userDto
            .withMembers(
                PList.of(
                    FIRST_NAME.withNecessity(REQUIRED),
                    MIDDLE_NAME,
                    LAST_NAME,
                    PojoMembers.ofType(
                            ObjectType.ofName(resolvedAddressDto.getName().getPojoName()))
                        .withNecessity(REQUIRED)
                        .withName(Name.ofString("address"))))
            .withName(componentName("UpdateUserUser", "Dto", "User"));
    final ObjectPojo resolvedUpdateUserDto =
        updateUserDto.withAllOfComposition(
            Optional.of(AllOfComposition.fromPojos(NonEmptyList.single(resolvedUserDto))));

    final ObjectPojo actualResolvedUpdateUser = findObjectPojo(resolvedPojos, "UpdateUser");
    assertEquals(resolvedUpdateUserDto, actualResolvedUpdateUser);

    final ObjectPojo actualResolvedUser = findObjectPojo(resolvedPojos, "UpdateUserUser");
    assertEquals(resolvedUserDto, actualResolvedUser);

    final ObjectPojo actualResolvedAddress = findObjectPojo(resolvedPojos, "UpdateUserAddress");
    assertEquals(resolvedAddressDto, actualResolvedAddress);

    final ObjectPojo actualResolvedCity = findObjectPojo(resolvedPojos, "UpdateUserCity");
    assertEquals(resolvedCityDto, actualResolvedCity);
  }

  @Test
  void resolve_when_allOfPojoWithRequiredPropertiesPojo_then_correctResolved2() {
    final ObjectPojo cityDto =
        Pojos.objectPojo(PList.of(ZIP, CITY_CODE, CITY_NAME))
            .withName(componentName("City", "Dto"));

    final ObjectPojo streetCommonDto =
        Pojos.objectPojo(PList.of(STREET, HOUSR_NR)).withName(componentName("StreetCommon", "Dto"));
    final ObjectPojo addressDto =
        Pojos.objectPojo(
                PList.of(
                    PojoMembers.ofType(ObjectType.ofName(cityDto.getName().getPojoName()))
                        .withName(Name.ofString("city"))))
            .withAllOfComposition(
                Optional.of(AllOfComposition.fromPojos(NonEmptyList.of(streetCommonDto))))
            .withName(componentName("Address", "Dto"));
    final ObjectPojo userDto =
        Pojos.objectPojo(
                PList.of(
                    FIRST_NAME,
                    MIDDLE_NAME,
                    LAST_NAME,
                    PojoMembers.ofType(ObjectType.ofName(addressDto.getName().getPojoName()))
                        .withName(Name.ofString("address"))
                        .withNecessity(OPTIONAL)))
            .withName(componentName("User", "Dto"));

    final ObjectPojo requiredCityPropsDto =
        Pojos.objectPojo(PList.empty())
            .withRequiredAdditionalProperties(PList.of(ZIP.getName()))
            .withName(componentName("RequiredCityProps", "Dto"));

    final ObjectPojo requiredAddressPropsDto =
        Pojos.objectPojo(
                PList.of(
                    PojoMembers.ofType(
                            ObjectType.ofName(requiredCityPropsDto.getName().getPojoName()))
                        .withName(Name.ofString("city"))))
            .withRequiredAdditionalProperties(PList.of(HOUSR_NR.getName()))
            .withName(componentName("RequiredAddressProps", "Dto"));

    final ObjectPojo requiredUserPropsDto =
        Pojos.objectPojo(
                PList.of(
                    PojoMembers.ofType(
                            ObjectType.ofName(requiredAddressPropsDto.getName().getPojoName()))
                        .withNecessity(REQUIRED)
                        .withName(Name.ofString("address"))))
            .withRequiredAdditionalProperties(PList.of(FIRST_NAME.getName()))
            .withName(componentName("RequiredUserProps", "Dto"));

    final ObjectPojo updateUserDto =
        Pojos.allOfPojo(userDto, requiredUserPropsDto).withName(componentName("UpdateUser", "Dto"));

    // method call
    final PList<Pojo> resolvedPojos =
        NestedRequiredPropertyResolver.resolve(
            PList.of(
                cityDto,
                streetCommonDto,
                addressDto,
                userDto,
                requiredCityPropsDto,
                requiredAddressPropsDto,
                requiredUserPropsDto,
                updateUserDto));

    assertEquals(
        PList.of(
                "UpdateUserUserDto",
                "RequiredCityPropsDto",
                "RequiredUserPropsDto",
                "RequiredAddressPropsDto",
                "UpdateUserDto",
                "UpdateUserCityDto",
                "UserDto",
                "UpdateUserAddressDto",
                "AddressDto",
                "UpdateUserStreetCommonDto",
                "StreetCommonDto",
                "CityDto")
            .toHashSet(),
        resolvedPojos.map(p -> p.getName().getPojoName().asString()).toHashSet());

    final ObjectPojo resolvedCityDto =
        cityDto
            .withMembers(PList.of(ZIP.withNecessity(REQUIRED), CITY_CODE, CITY_NAME))
            .withName(componentName("UpdateUserCity", "Dto", "City"));
    final ObjectPojo resolvedStreetCommonDto =
        streetCommonDto
            .withMembers(PList.of(STREET, HOUSR_NR.withNecessity(REQUIRED)))
            .withName(componentName("UpdateUserStreetCommon", "Dto", "StreetCommon"));
    final ObjectPojo resolvedAddressDto =
        addressDto
            .withMembers(
                PList.of(
                    PojoMembers.ofType(ObjectType.ofName(resolvedCityDto.getName().getPojoName()))
                        .withName(Name.ofString("city"))))
            .withAllOfComposition(
                Optional.of(AllOfComposition.fromPojos(NonEmptyList.of(resolvedStreetCommonDto))))
            .withName(componentName("UpdateUserAddress", "Dto", "Address"));
    final ObjectPojo resolvedUserDto =
        userDto
            .withMembers(
                PList.of(
                    FIRST_NAME.withNecessity(REQUIRED),
                    MIDDLE_NAME,
                    LAST_NAME,
                    PojoMembers.ofType(
                            ObjectType.ofName(resolvedAddressDto.getName().getPojoName()))
                        .withNecessity(REQUIRED)
                        .withName(Name.ofString("address"))))
            .withName(componentName("UpdateUserUser", "Dto", "User"));
    final ObjectPojo resolvedUpdateUserDto =
        updateUserDto.withAllOfComposition(
            Optional.of(AllOfComposition.fromPojos(NonEmptyList.single(resolvedUserDto))));

    final ObjectPojo actualResolvedUpdateUser = findObjectPojo(resolvedPojos, "UpdateUser");
    assertEquals(resolvedUpdateUserDto, actualResolvedUpdateUser);

    final ObjectPojo actualResolvedUser = findObjectPojo(resolvedPojos, "UpdateUserUser");
    assertEquals(resolvedUserDto, actualResolvedUser);

    final ObjectPojo actualResolvedAddress = findObjectPojo(resolvedPojos, "UpdateUserAddress");
    assertEquals(resolvedAddressDto, actualResolvedAddress);

    final ObjectPojo actualResolvedStreetCommonDto =
        findObjectPojo(resolvedPojos, "UpdateUserStreetCommon");
    assertEquals(resolvedStreetCommonDto, actualResolvedStreetCommonDto);

    final ObjectPojo actualResolvedCity = findObjectPojo(resolvedPojos, "UpdateUserCity");
    assertEquals(resolvedCityDto, actualResolvedCity);
  }

  @Test
  void resolve_when_noPropertyGetsPromoted_then_noChanges() {
    final ObjectPojo userDto =
        Pojos.objectPojo(PList.of(FIRST_NAME, MIDDLE_NAME, LAST_NAME))
            .withName(componentName("User", "Dto"));
    final ObjectPojo requiredAdditionalPropertiesDto =
        Pojos.objectPojo(PList.empty())
            .withRequiredAdditionalProperties(PList.of(STREET.getName()))
            .withName(componentName("RequiredAddProp", "Dto"));
    final ObjectPojo parentPojo =
        Pojos.allOfPojo(userDto, requiredAdditionalPropertiesDto)
            .withName(componentName("Parent", "Dto"));

    final PList<Pojo> resolvedPojos =
        NestedRequiredPropertyResolver.resolve(
            PList.of(userDto, requiredAdditionalPropertiesDto, parentPojo));

    assertEquals(
        PList.of("RequiredAddPropDto", "UserDto", "ParentDto").toHashSet(),
        resolvedPojos.map(p -> p.getName().getPojoName().asString()).toHashSet());

    assertEquals(userDto, findObjectPojo(resolvedPojos, "User"));
    assertEquals(requiredAdditionalPropertiesDto, findObjectPojo(resolvedPojos, "RequiredAddProp"));
    assertEquals(parentPojo, findObjectPojo(resolvedPojos, "Parent"));
    assertEquals(
        PList.of(userDto, requiredAdditionalPropertiesDto, parentPojo).toHashSet(),
        resolvedPojos.toHashSet());
  }

  private static ObjectPojo findObjectPojo(PList<Pojo> pojos, String pojoName) {
    final Optional<ObjectPojo> objectPojo =
        pojos
            .flatMapOptional(Pojo::asObjectPojo)
            .find(pojo -> pojo.getName().getPojoName().getName().asString().equals(pojoName));
    assertTrue(objectPojo.isPresent(), "No object pojo found with name " + pojoName);
    return objectPojo.get();
  }
}
