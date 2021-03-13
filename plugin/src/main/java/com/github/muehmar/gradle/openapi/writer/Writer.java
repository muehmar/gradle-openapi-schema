package com.github.muehmar.gradle.openapi.writer;

public interface Writer {
  default Writer print(char value) {
    return print("" + value);
  }

  default Writer print(int value) {
    return print("" + value);
  }

  default Writer print(String string) {
    return print(string, new Object[0]);
  }

  Writer print(String string, Object... args);

  Writer println();

  default Writer println(char value) {
    print(value);
    return println();
  }

  default Writer println(int value) {
    print(value);
    return println();
  }

  default Writer println(String string) {
    print(string);
    return println();
  }

  default Writer println(String string, Object... args) {
    print(string, args);
    return println();
  }

  Writer tab(int tabs);

  Writer ref(String ref);

  boolean close(String path);
}
