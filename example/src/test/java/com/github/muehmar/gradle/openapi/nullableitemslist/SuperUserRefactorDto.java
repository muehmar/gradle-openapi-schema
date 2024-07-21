package com.github.muehmar.gradle.openapi.nullableitemslist;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.github.muehmar.openapi.util.JacksonNullContainer;
import com.github.muehmar.openapi.util.NullableAdditionalProperty;
import com.github.muehmar.openapi.util.Tristate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.validation.Valid;

@JsonDeserialize(builder = SuperUserRefactorDto.Builder.class)
public class SuperUserRefactorDto {
  private final List<String> ids;
  private final List<String> usernames;
  private final boolean isUsernamesPresent;
  private final List<String> emails;
  private final boolean isEmailsNotNull;
  private final List<String> phones;
  private final boolean isPhonesNull;
  private final String superUserId;
  private final boolean isSuperUserIdNotNull;
  private final Map<String, Object> additionalProperties;

  public SuperUserRefactorDto(
      List<String> ids,
      List<String> usernames,
      boolean isUsernamesPresent,
      List<String> emails,
      boolean isEmailsNotNull,
      List<String> phones,
      boolean isPhonesNull,
      String superUserId,
      boolean isSuperUserIdNotNull,
      Map<String, Object> additionalProperties) {
    this.ids = ids;
    this.usernames = usernames;
    this.isUsernamesPresent = isUsernamesPresent;
    this.emails = emails;
    this.isEmailsNotNull = isEmailsNotNull;
    this.phones = phones;
    this.isPhonesNull = isPhonesNull;
    this.superUserId = superUserId;
    this.isSuperUserIdNotNull = isSuperUserIdNotNull;
    this.additionalProperties = Collections.unmodifiableMap(additionalProperties);
  }

  public ArrayList<Optional<Integer>> getIds() {
    return mapList(ids, Integer::parseInt, Optional::ofNullable, ArrayList::new, l -> l);
  }

  @JsonProperty("ids")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> getIdsRaw() {
    return ids;
  }

  @JsonIgnore
  public Optional<List<Optional<String>>> getUsernamesOpt() {
    return Optional.ofNullable(wrapNullableItemsList(usernames));
  }

  @JsonIgnore
  public List<Optional<String>> getUsernamesOr(List<Optional<String>> defaultValue) {
    return this.usernames == null ? defaultValue : wrapNullableItemsList(this.usernames);
  }

  @JsonProperty("usernames")
  private List<String> getUsernamesRaw() {
    return usernames;
  }

  @JsonIgnore
  public Optional<List<Optional<String>>> getEmailsOpt() {
    return Optional.ofNullable(wrapNullableItemsList(emails));
  }

  @JsonIgnore
  public List<Optional<String>> getEmailsOr(List<Optional<String>> defaultValue) {
    return this.emails == null ? defaultValue : wrapNullableItemsList(this.emails);
  }

  @JsonProperty("emails")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> getEmailsRaw() {
    return emails;
  }

  @JsonIgnore
  public Tristate<ArrayList<Optional<Integer>>> getPhonesTristate() {
    return mapList(
        phones,
        Integer::parseInt,
        Optional::ofNullable,
        ArrayList::new,
        l -> Tristate.ofNullableAndNullFlag(l, isPhonesNull));
  }

  @JsonProperty("phones")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Object getPhonesJackson() {
    return isPhonesNull ? new JacksonNullContainer<>(phones) : phones;
  }

  @JsonIgnore
  public Optional<String> getSuperUserIdOpt() {
    return Optional.ofNullable(superUserId);
  }

  @JsonIgnore
  public String getSuperUserIdOr(String defaultValue) {
    return this.superUserId == null ? defaultValue : this.superUserId;
  }

  @JsonProperty("superUserId")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String getSuperUserIdRaw() {
    return superUserId;
  }

  @JsonIgnore
  @Valid
  public UserDto getUserDto() {
    return asUserDto();
  }

  @JsonIgnore
  @Valid
  public SuperUserAllOfDto getSuperUserAllOfDto() {
    return asSuperUserAllOfDto();
  }

  @Valid
  @JsonAnyGetter
  private Map<String, Object> getAdditionalProperties_() {
    return additionalProperties;
  }

  @JsonIgnore
  public List<NullableAdditionalProperty<Object>> getAdditionalProperties() {
    return additionalProperties.entrySet().stream()
        .map(entry -> NullableAdditionalProperty.ofNullable(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
  }

  /**
   * Returns the additional property with {@code key} where the {@link Tristate} class represents
   * the possible three states of the property: present and non-null, present and null, absent.
   */
  public Tristate<Object> getAdditionalProperty(String key) {
    if (additionalProperties.containsKey(key)) {
      return Optional.ofNullable(additionalProperties.get(key))
          .map(Tristate::ofValue)
          .orElseGet(Tristate::ofNull);
    } else {
      return Tristate.ofAbsent();
    }
  }

  /** Returns the number of present properties of this object. */
  @JsonIgnore
  public int getPropertyCount() {
    return (ids != null ? 1 : 0)
        + (isUsernamesPresent ? 1 : 0)
        + (emails != null ? 1 : 0)
        + ((isPhonesNull || phones != null) ? 1 : 0)
        + (superUserId != null ? 1 : 0)
        + additionalProperties.size();
  }

  public SuperUserRefactorDto withIds(List<String> ids) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withIds_(List<Optional<String>> ids) {
    return new SuperUserRefactorDto(
        unwrapNullableItemsList(ids),
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withUsernames(List<String> usernames) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        true,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withUsernames(Optional<List<String>> usernames) {
    return new SuperUserRefactorDto(
        ids,
        usernames.orElse(null),
        usernames.isPresent(),
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withUsernames_(List<Optional<String>> usernames) {
    return new SuperUserRefactorDto(
        ids,
        unwrapNullableItemsList(usernames),
        true,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withUsernames_(Optional<List<Optional<String>>> usernames) {
    return new SuperUserRefactorDto(
        ids,
        unwrapNullableItemsList(usernames.orElse(null)),
        usernames.isPresent(),
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withEmails(List<String> emails) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withEmails(Optional<List<String>> emails) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails.orElse(null),
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withEmails_(List<Optional<String>> emails) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        unwrapNullableItemsList(emails),
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withEmails_(Optional<List<Optional<String>>> emails) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        unwrapNullableItemsList(emails.orElse(null)),
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withPhones(List<String> phones) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones,
        false,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withPhones(Tristate<List<String>> phones) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones.onValue(val -> val).onNull(() -> null).onAbsent(() -> null),
        phones.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false),
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withPhones_(List<Optional<String>> phones) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        unwrapNullableItemsList(phones),
        false,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withPhones_(Tristate<List<Optional<String>>> phones) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        unwrapNullableItemsList(phones.onValue(val -> val).onNull(() -> null).onAbsent(() -> null)),
        phones.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false),
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withSuperUserId(String superUserId) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  public SuperUserRefactorDto withSuperUserId(Optional<String> superUserId) {
    return new SuperUserRefactorDto(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId.orElse(null),
        isSuperUserIdNotNull,
        additionalProperties);
  }

  private static <T> List<Optional<T>> wrapNullableItemsList(List<T> list) {
    if (list == null) {
      return null;
    }
    return list.stream().map(Optional::ofNullable).collect(Collectors.toList());
  }

  private static <T> List<T> unwrapNullableItemsList(List<Optional<T>> list) {
    if (list == null) {
      return null;
    }
    return list.stream().map(value -> value.orElse(null)).collect(Collectors.toList());
  }

  private static <T> Optional<List<T>> unwrapOptionalNullableItemsList(
      Optional<List<Optional<T>>> list) {
    return list.map(l -> unwrapNullableItemsList(l));
  }

  private static <T> Tristate<List<T>> unwrapTristateNullableItemsList(
      Tristate<List<Optional<T>>> list) {
    return list.map(l -> unwrapNullableItemsList(l));
  }

  private UserDto asUserDto() {
    Map<String, Object> props = new HashMap<>(additionalProperties);
    if (superUserId != null) {
      props.put("superUserId", superUserId);
    }
    return new UserDto(
        ids, usernames, isUsernamesPresent, emails, isEmailsNotNull, phones, isPhonesNull, props);
  }

  private SuperUserAllOfDto asSuperUserAllOfDto() {
    Map<String, Object> props = new HashMap<>(additionalProperties);
    if (ids != null) {
      props.put("ids", ids);
    }
    if (isUsernamesPresent) {
      props.put("usernames", usernames);
    }
    if (emails != null) {
      props.put("emails", emails);
    }
    if (phones != null || isPhonesNull) {
      props.put("phones", phones);
    }
    return new SuperUserAllOfDto(superUserId, isSuperUserIdNotNull, props);
  }

  boolean isValid() {
    return new Validator().isValid();
  }

  private class Validator {

    private boolean isAdditionalPropertiesValid() {
      if (getAdditionalProperties_() != null) {
        return getAdditionalProperties_().values().stream()
            .allMatch(this::isAdditionalPropertiesValueValid);
      }

      return false;
    }

    private boolean isAdditionalPropertiesValueValid(Object additionalPropertiesValue) {
      return true;
    }

    private boolean isValid() {
      return asUserDto().isValid()
          && asSuperUserAllOfDto().isValid()
          && isAdditionalPropertiesValid();
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || this.getClass() != obj.getClass()) return false;
    final SuperUserRefactorDto other = (SuperUserRefactorDto) obj;
    return Objects.deepEquals(this.ids, other.ids)
        && Objects.deepEquals(this.usernames, other.usernames)
        && Objects.deepEquals(this.isUsernamesPresent, other.isUsernamesPresent)
        && Objects.deepEquals(this.emails, other.emails)
        && Objects.deepEquals(this.isEmailsNotNull, other.isEmailsNotNull)
        && Objects.deepEquals(this.phones, other.phones)
        && Objects.deepEquals(this.isPhonesNull, other.isPhonesNull)
        && Objects.deepEquals(this.superUserId, other.superUserId)
        && Objects.deepEquals(this.isSuperUserIdNotNull, other.isSuperUserIdNotNull)
        && Objects.deepEquals(this.additionalProperties, other.additionalProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        ids,
        usernames,
        isUsernamesPresent,
        emails,
        isEmailsNotNull,
        phones,
        isPhonesNull,
        superUserId,
        isSuperUserIdNotNull,
        additionalProperties);
  }

  @Override
  public String toString() {
    return "SuperUserDto{"
        + "ids="
        + ids
        + ", "
        + "usernames="
        + usernames
        + ", "
        + "isUsernamesPresent="
        + isUsernamesPresent
        + ", "
        + "emails="
        + emails
        + ", "
        + "isEmailsNotNull="
        + isEmailsNotNull
        + ", "
        + "phones="
        + phones
        + ", "
        + "isPhonesNull="
        + isPhonesNull
        + ", "
        + "superUserId="
        + "'"
        + superUserId
        + "'"
        + ", "
        + "isSuperUserIdNotNull="
        + isSuperUserIdNotNull
        + ", "
        + "additionalProperties="
        + additionalProperties
        + "}";
  }

  private static <A, B, C, D, E> E mapList(
      List<A> list,
      Function<A, B> mapListItemType,
      Function<B, C> wrapListItem,
      Function<List<C>, D> mapListType,
      Function<D, E> wrapListType) {
    if (list == null) {
      return wrapListType.apply(null);
    }

    final List<C> mappedListType =
        list.stream()
            .map(i -> i != null ? mapListItemType.apply(i) : null)
            .map(wrapListItem)
            .collect(Collectors.toList());

    return wrapListType.apply(mapListType.apply(mappedListType));
  }

  private static <S, T> List<T> mapListItem(List<S> list, Function<S, T> mapItem) {
    if (list == null) {
      return null;
    }
    return list.stream().map(mapItem).collect(Collectors.toList());
  }

  private static <A, B, C, D, E> List<E> unmapList(
      A list,
      Function<A, B> unwrapList,
      Function<B, List<C>> unmapListType,
      Function<C, D> unwrapListItem,
      Function<D, E> unmapListItem) {
    if (list == null) {
      return null;
    }

    final B unwrappedList = unwrapList.apply(list);

    if (unwrappedList == null) {
      return null;
    }

    final List<C> unmappedListType = unmapListType.apply(unwrappedList);

    if (unmappedListType == null) {
      return null;
    }

    return unmappedListType.stream()
        .map(i -> i != null ? unwrapListItem.apply(i) : null)
        .map(i -> i != null ? unmapListItem.apply(i) : null)
        .collect(Collectors.toList());
  }

  @JsonPOJOBuilder(withPrefix = "set")
  public static final class Builder {

    private Builder() {}

    private List<String> ids;
    private List<String> usernames;
    private boolean isUsernamesPresent = false;
    private List<String> emails;
    private boolean isEmailsNotNull = true;
    private List<String> phones;
    private boolean isPhonesNull = false;
    private String superUserId;
    private boolean isSuperUserIdNotNull = true;
    private Map<String, Object> additionalProperties = new HashMap<>();

    @JsonProperty("ids")
    private Builder setIdsJson(List<String> ids) {
      this.ids = ids;
      return this;
    }

    @JsonIgnore
    private Builder setIds(ArrayList<Integer> ids) {
      this.ids =
          unmapList(
              ids,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              Function.identity(),
              i -> i.toString());
      return this;
    }

    @JsonIgnore
    private Builder setIds_(ArrayList<Optional<Integer>> ids) {
      this.ids =
          unmapList(
              ids,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              i -> i.orElse(null),
              i -> i.toString());
      return this;
    }

    @JsonProperty("usernames")
    private Builder setUsernamesJson(List<String> usernames) {
      this.usernames = usernames;
      this.isUsernamesPresent = true;
      return this;
    }

    @JsonIgnore
    private Builder setUsernames(ArrayList<Integer> usernames) {
      this.usernames =
          unmapList(
              usernames,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              Function.identity(),
              i -> i.toString());
      this.isUsernamesPresent = true;
      return this;
    }

    @JsonIgnore
    private Builder setUsernames(Optional<ArrayList<Integer>> usernames) {
      this.usernames =
          unmapList(
              usernames,
              l -> l.orElse(null),
              l -> l.stream().collect(Collectors.toList()),
              Function.identity(),
              i -> i.toString());
      this.isUsernamesPresent = true;
      return this;
    }

    @JsonIgnore
    private Builder setUsernames_(ArrayList<Optional<Integer>> usernames) {
      this.usernames =
          unmapList(
              usernames,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              i -> i.orElse(null),
              i -> i.toString());
      this.isUsernamesPresent = true;
      return this;
    }

    @JsonIgnore
    private Builder setUsernames_(Optional<ArrayList<Optional<Integer>>> usernames) {
      this.usernames =
          unmapList(
              usernames,
              l -> l.orElse(null),
              l -> l.stream().collect(Collectors.toList()),
              i -> i.orElse(null),
              i -> i.toString());
      this.isUsernamesPresent = true;
      return this;
    }

    @JsonProperty("emails")
    public Builder setEmailsJson(List<String> emails) {
      this.emails = emails;
      this.isEmailsNotNull = emails != null;
      return this;
    }

    @JsonIgnore
    public Builder setEmails(ArrayList<Integer> emails) {
      this.emails =
          unmapList(
              emails,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              Function.identity(),
              i -> i.toString());
      this.isEmailsNotNull = emails != null;
      return this;
    }

    @JsonIgnore
    public Builder setEmails(Optional<ArrayList<Integer>> emails) {
      this.emails =
          unmapList(
              emails,
              l -> l.orElse(null),
              l -> l.stream().collect(Collectors.toList()),
              Function.identity(),
              i -> i.toString());
      this.isEmailsNotNull = true;
      return this;
    }

    @JsonIgnore
    public Builder setEmails_(ArrayList<Optional<Integer>> emails) {
      this.emails =
          unmapList(
              emails,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              i -> i.orElse(null),
              i -> i.toString());
      this.isEmailsNotNull = true;
      return this;
    }

    @JsonIgnore
    public Builder setEmails_(Optional<ArrayList<Optional<Integer>>> emails) {
      this.emails =
          unmapList(
              emails,
              l -> l.orElse(null),
              l -> l.stream().collect(Collectors.toList()),
              i -> i.orElse(null),
              i -> i.toString());
      this.isEmailsNotNull = true;
      return this;
    }

    @JsonProperty("phones")
    public Builder setPhonesJson(List<String> phones) {
      this.phones = phones;
      this.isPhonesNull = phones == null;
      return this;
    }

    @JsonIgnore
    public Builder setPhones(ArrayList<Integer> phones) {
      this.phones =
          unmapList(
              phones,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              Function.identity(),
              i -> i.toString());
      this.isPhonesNull = phones == null;
      return this;
    }

    @JsonIgnore
    public Builder setPhones(Tristate<ArrayList<Integer>> phones) {
      this.phones =
          unmapList(
              phones,
              l -> l.onValue(val -> val).onNull(() -> null).onAbsent(() -> null),
              l -> l.stream().collect(Collectors.toList()),
              Function.identity(),
              i -> i.toString());
      this.isPhonesNull = phones.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);
      return this;
    }

    @JsonIgnore
    public Builder setPhones_(ArrayList<Optional<Integer>> phones) {
      this.phones =
          unmapList(
              phones,
              Function.identity(),
              l -> l.stream().collect(Collectors.toList()),
              i -> i.orElse(null),
              i -> i.toString());
      isPhonesNull = false;
      return this;
    }

    @JsonIgnore
    public Builder setPhones_(Tristate<ArrayList<Optional<Integer>>> phones) {
      this.phones =
          unmapList(
              phones,
              l -> l.onValue(val -> val).onNull(() -> null).onAbsent(() -> null),
              l -> l.stream().collect(Collectors.toList()),
              i -> i.orElse(null),
              i -> i.toString());
      this.isPhonesNull = phones.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);
      return this;
    }

    @JsonProperty("superUserId")
    public Builder setSuperUserId(String superUserId) {
      this.superUserId = superUserId;
      this.isSuperUserIdNotNull = superUserId != null;
      return this;
    }

    @JsonIgnore
    public Builder setSuperUserId(Optional<String> superUserId) {
      this.superUserId = superUserId.orElse(null);
      this.isSuperUserIdNotNull = true;
      return this;
    }

    private Builder setUserDto(UserDto dto) {
      setIds_(null);
      setUsernames_(Optional.empty());
      setEmails_(Optional.empty());
      setPhones_(Tristate.ofAbsent());
      dto.getAdditionalProperties()
          .forEach(prop -> addAdditionalProperty(prop.getName(), prop.getValue().orElse(null)));
      return this;
    }

    private Builder setSuperUserAllOfDto(SuperUserAllOfDto dto) {
      setSuperUserId(dto.getSuperUserIdOpt());
      dto.getAdditionalProperties()
          .forEach(prop -> addAdditionalProperty(prop.getName(), prop.getValue().orElse(null)));
      return this;
    }

    @JsonAnySetter
    public Builder addAdditionalProperty(String key, Object value) {
      this.additionalProperties.put(key, value);
      return this;
    }

    public Builder addAdditionalProperty(String key, Tristate<Object> value) {
      value
          .onValue(val -> this.additionalProperties.put(key, val))
          .onNull(() -> this.additionalProperties.put(key, null))
          .onAbsent(() -> null);
      return this;
    }

    public Builder setAdditionalProperties(Map<String, Object> additionalProperties) {
      this.additionalProperties = new HashMap<>(additionalProperties);
      return this;
    }

    public SuperUserRefactorDto build() {
      additionalProperties.remove("ids");
      additionalProperties.remove("usernames");
      additionalProperties.remove("emails");
      additionalProperties.remove("phones");
      additionalProperties.remove("superUserId");

      return new SuperUserRefactorDto(
          ids,
          usernames,
          isUsernamesPresent,
          emails,
          isEmailsNotNull,
          phones,
          isPhonesNull,
          superUserId,
          isSuperUserIdNotNull,
          additionalProperties);
    }
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static BuilderStages.FullAllOfBuilderUser0 fullBuilder() {
    return new BuilderStages.FullAllOfBuilderUser0(new Builder());
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static BuilderStages.FullAllOfBuilderUser0 fullSuperUserDtoBuilder() {
    return new BuilderStages.FullAllOfBuilderUser0(new Builder());
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static BuilderStages.AllOfBuilderUser0 builder() {
    return new BuilderStages.AllOfBuilderUser0(new Builder());
  }

  /**
   * Instantiates a new staged builder. Explicit properties have precedence over additional
   * properties, i.e. an additional property with the same name as an explicit property will be
   * discarded.
   */
  public static BuilderStages.AllOfBuilderUser0 superUserDtoBuilder() {
    return new BuilderStages.AllOfBuilderUser0(new Builder());
  }

  public static final class BuilderStages {
    private BuilderStages() {}

    public static final class FullAllOfBuilderUser0 {
      private final Builder builder;

      private FullAllOfBuilderUser0(Builder builder) {
        this.builder = builder;
      }

      public FullAllOfBuilderUser1 setIds(ArrayList<Integer> ids) {
        return new FullAllOfBuilderUser1(builder.setIds(ids));
      }

      public FullAllOfBuilderUser1 setIds_(ArrayList<Optional<Integer>> ids) {
        return new FullAllOfBuilderUser1(builder.setIds_(ids));
      }

      public FullAllOfBuilderSuperUserAllOf setUserDto(UserDto dto) {
        return new FullAllOfBuilderSuperUserAllOf(builder.setUserDto(dto));
      }
    }

    public static final class FullAllOfBuilderUser1 {
      private final Builder builder;

      private FullAllOfBuilderUser1(Builder builder) {
        this.builder = builder;
      }

      public FullAllOfBuilderUser2 setUsernames(ArrayList<Integer> usernames) {
        return new FullAllOfBuilderUser2(builder.setUsernames(usernames));
      }

      public FullAllOfBuilderUser2 setUsernames_(ArrayList<Optional<Integer>> usernames) {
        return new FullAllOfBuilderUser2(builder.setUsernames_(usernames));
      }

      public FullAllOfBuilderUser2 setUsernames(Optional<ArrayList<Integer>> usernames) {
        return new FullAllOfBuilderUser2(builder.setUsernames(usernames));
      }

      public FullAllOfBuilderUser2 setUsernames_(Optional<ArrayList<Optional<Integer>>> usernames) {
        return new FullAllOfBuilderUser2(builder.setUsernames_(usernames));
      }
    }

    public static final class FullAllOfBuilderUser2 {
      private final Builder builder;

      private FullAllOfBuilderUser2(Builder builder) {
        this.builder = builder;
      }

      public FullAllOfBuilderUser3 setEmails(ArrayList<Integer> emails) {
        return new FullAllOfBuilderUser3(builder.setEmails(emails));
      }

      public FullAllOfBuilderUser3 setEmails_(ArrayList<Optional<Integer>> emails) {
        return new FullAllOfBuilderUser3(builder.setEmails_(emails));
      }

      public FullAllOfBuilderUser3 setEmails(Optional<ArrayList<Integer>> emails) {
        return new FullAllOfBuilderUser3(builder.setEmails(emails));
      }

      public FullAllOfBuilderUser3 setEmails_(Optional<ArrayList<Optional<Integer>>> emails) {
        return new FullAllOfBuilderUser3(builder.setEmails_(emails));
      }
    }

    public static final class FullAllOfBuilderUser3 {
      private final Builder builder;

      private FullAllOfBuilderUser3(Builder builder) {
        this.builder = builder;
      }

      public FullAllOfBuilderSuperUserAllOf0 setPhones(ArrayList<Integer> phones) {
        return new FullAllOfBuilderSuperUserAllOf0(builder.setPhones(phones));
      }

      public FullAllOfBuilderSuperUserAllOf0 setPhones_(ArrayList<Optional<Integer>> phones) {
        return new FullAllOfBuilderSuperUserAllOf0(builder.setPhones_(phones));
      }

      public FullAllOfBuilderSuperUserAllOf0 setPhones(Tristate<ArrayList<Integer>> phones) {
        return new FullAllOfBuilderSuperUserAllOf0(builder.setPhones(phones));
      }

      public FullAllOfBuilderSuperUserAllOf0 setPhones_(
          Tristate<ArrayList<Optional<Integer>>> phones) {
        return new FullAllOfBuilderSuperUserAllOf0(builder.setPhones_(phones));
      }
    }

    public static final class FullAllOfBuilderSuperUserAllOf0 {
      private final Builder builder;

      private FullAllOfBuilderSuperUserAllOf0(Builder builder) {
        this.builder = builder;
      }

      public FullOptPropertyBuilder0 setSuperUserId(String superUserId) {
        return new FullOptPropertyBuilder0(builder.setSuperUserId(superUserId));
      }

      public FullOptPropertyBuilder0 setSuperUserId(Optional<String> superUserId) {
        return new FullOptPropertyBuilder0(builder.setSuperUserId(superUserId));
      }
    }

    public static final class FullAllOfBuilderSuperUserAllOf {
      private final Builder builder;

      private FullAllOfBuilderSuperUserAllOf(Builder builder) {
        this.builder = builder;
      }

      public FullOptPropertyBuilder0 setSuperUserAllOfDto(SuperUserAllOfDto dto) {
        return new FullOptPropertyBuilder0(builder.setSuperUserAllOfDto(dto));
      }
    }

    public static final class FullOptPropertyBuilder0 {
      private final Builder builder;

      private FullOptPropertyBuilder0(Builder builder) {
        this.builder = builder;
      }

      public FullOptPropertyBuilder0 addAdditionalProperty(String key, Object value) {
        return new FullOptPropertyBuilder0(builder.addAdditionalProperty(key, value));
      }

      public FullOptPropertyBuilder0 addAdditionalProperty(String key, Tristate<Object> value) {
        return new FullOptPropertyBuilder0(builder.addAdditionalProperty(key, value));
      }

      public FullOptPropertyBuilder0 setAdditionalProperties(
          Map<String, Object> additionalProperties) {
        return new FullOptPropertyBuilder0(builder.setAdditionalProperties(additionalProperties));
      }

      public SuperUserRefactorDto build() {
        return builder.build();
      }
    }

    public static final class AllOfBuilderUser0 {
      private final Builder builder;

      private AllOfBuilderUser0(Builder builder) {
        this.builder = builder;
      }

      public AllOfBuilderUser1 setIds(ArrayList<Integer> ids) {
        return new AllOfBuilderUser1(builder.setIds(ids));
      }

      public AllOfBuilderUser1 setIds_(ArrayList<Optional<Integer>> ids) {
        return new AllOfBuilderUser1(builder.setIds_(ids));
      }

      public AllOfBuilderSuperUserAllOf setUserDto(UserDto dto) {
        return new AllOfBuilderSuperUserAllOf(builder.setUserDto(dto));
      }
    }

    public static final class AllOfBuilderUser1 {
      private final Builder builder;

      private AllOfBuilderUser1(Builder builder) {
        this.builder = builder;
      }

      public AllOfBuilderUser2 setUsernames(ArrayList<Integer> usernames) {
        return new AllOfBuilderUser2(builder.setUsernames(usernames));
      }

      public AllOfBuilderUser2 setUsernames_(ArrayList<Optional<Integer>> usernames) {
        return new AllOfBuilderUser2(builder.setUsernames_(usernames));
      }

      public AllOfBuilderUser2 setUsernames(Optional<ArrayList<Integer>> usernames) {
        return new AllOfBuilderUser2(builder.setUsernames(usernames));
      }

      public AllOfBuilderUser2 setUsernames_(Optional<ArrayList<Optional<Integer>>> usernames) {
        return new AllOfBuilderUser2(builder.setUsernames_(usernames));
      }
    }

    public static final class AllOfBuilderUser2 {
      private final Builder builder;

      private AllOfBuilderUser2(Builder builder) {
        this.builder = builder;
      }

      public AllOfBuilderUser3 setEmails(ArrayList<Integer> emails) {
        return new AllOfBuilderUser3(builder.setEmails(emails));
      }

      public AllOfBuilderUser3 setEmails_(ArrayList<Optional<Integer>> emails) {
        return new AllOfBuilderUser3(builder.setEmails_(emails));
      }

      public AllOfBuilderUser3 setEmails(Optional<ArrayList<Integer>> emails) {
        return new AllOfBuilderUser3(builder.setEmails(emails));
      }

      public AllOfBuilderUser3 setEmails_(Optional<ArrayList<Optional<Integer>>> emails) {
        return new AllOfBuilderUser3(builder.setEmails_(emails));
      }
    }

    public static final class AllOfBuilderUser3 {
      private final Builder builder;

      private AllOfBuilderUser3(Builder builder) {
        this.builder = builder;
      }

      public AllOfBuilderSuperUserAllOf0 setPhones(ArrayList<Integer> phones) {
        return new AllOfBuilderSuperUserAllOf0(builder.setPhones(phones));
      }

      public AllOfBuilderSuperUserAllOf0 setPhones_(ArrayList<Optional<Integer>> phones) {
        return new AllOfBuilderSuperUserAllOf0(builder.setPhones_(phones));
      }

      public AllOfBuilderSuperUserAllOf0 setPhones(Tristate<ArrayList<Integer>> phones) {
        return new AllOfBuilderSuperUserAllOf0(builder.setPhones(phones));
      }

      public AllOfBuilderSuperUserAllOf0 setPhones_(Tristate<ArrayList<Optional<Integer>>> phones) {
        return new AllOfBuilderSuperUserAllOf0(builder.setPhones_(phones));
      }
    }

    public static final class AllOfBuilderSuperUserAllOf0 {
      private final Builder builder;

      private AllOfBuilderSuperUserAllOf0(Builder builder) {
        this.builder = builder;
      }

      public PropertyBuilder0 setSuperUserId(String superUserId) {
        return new PropertyBuilder0(builder.setSuperUserId(superUserId));
      }

      public PropertyBuilder0 setSuperUserId(Optional<String> superUserId) {
        return new PropertyBuilder0(builder.setSuperUserId(superUserId));
      }
    }

    public static final class AllOfBuilderSuperUserAllOf {
      private final Builder builder;

      private AllOfBuilderSuperUserAllOf(Builder builder) {
        this.builder = builder;
      }

      public PropertyBuilder0 setSuperUserAllOfDto(SuperUserAllOfDto dto) {
        return new PropertyBuilder0(builder.setSuperUserAllOfDto(dto));
      }
    }

    public static final class PropertyBuilder0 {
      private final Builder builder;

      private PropertyBuilder0(Builder builder) {
        this.builder = builder;
      }

      public OptPropertyBuilder0 andAllOptionals() {
        return new OptPropertyBuilder0(builder);
      }

      public Builder andOptionals() {
        return builder;
      }

      public SuperUserRefactorDto build() {
        return builder.build();
      }
    }

    public static final class OptPropertyBuilder0 {
      private final Builder builder;

      private OptPropertyBuilder0(Builder builder) {
        this.builder = builder;
      }

      public OptPropertyBuilder0 addAdditionalProperty(String key, Object value) {
        return new OptPropertyBuilder0(builder.addAdditionalProperty(key, value));
      }

      public OptPropertyBuilder0 addAdditionalProperty(String key, Tristate<Object> value) {
        return new OptPropertyBuilder0(builder.addAdditionalProperty(key, value));
      }

      public OptPropertyBuilder0 setAdditionalProperties(Map<String, Object> additionalProperties) {
        return new OptPropertyBuilder0(builder.setAdditionalProperties(additionalProperties));
      }

      public SuperUserRefactorDto build() {
        return builder.build();
      }
    }
  }
}
