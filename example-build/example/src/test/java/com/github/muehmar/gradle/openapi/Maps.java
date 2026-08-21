package com.github.muehmar.gradle.openapi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Maps {
  private Maps() {}

  public static <K, V> Map<K, V> map(K key, V value) {
    return Collections.singletonMap(key, value);
  }

  public static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2) {
    final HashMap<K, V> map = new HashMap<>();
    map.put(key1, value1);
    map.put(key2, value2);
    return Collections.unmodifiableMap(map);
  }
}
