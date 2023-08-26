package com.github.muehmar.gradle.openapi;

import au.com.origin.snapshots.exceptions.SnapshotMatchException;
import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

public class IntellijDiffSnapshotTestExtension implements InvocationInterceptor {

  @Override
  public void interceptTestMethod(
      Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext,
      ExtensionContext extensionContext)
      throws Throwable {
    try {
      invocation.proceed();
    } catch (SnapshotMatchException e) {
      final List<Throwable> failures = e.getFailures();
      if (failures.size() == 1) {
        throw failures.get(0);
      } else {
        throw e;
      }
    }
  }
}
