package com.github.muehmar.openapischema.spring.rest;

import com.github.muehmar.openapischema.spring.AnyofApiController;
import com.github.muehmar.openapischema.spring.OneofApiController;
import com.github.muehmar.openapischema.spring.rest.anyof.AnyOfDelegate;
import com.github.muehmar.openapischema.spring.rest.anyof.AnyOfInterface;
import com.github.muehmar.openapischema.spring.rest.oneof.OneOfDelegate;
import com.github.muehmar.openapischema.spring.rest.oneof.OneOfInterface;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(value = {ControllerUnitTest.MockConfig.class, OneOfDelegate.class, AnyOfDelegate.class})
public class ControllerUnitTest {

  @Autowired protected OneOfInterface oneOfInterface;
  @Autowired protected AnyOfInterface anyOfInterface;

  @Autowired private OneofApiController oneOfController;
  @Autowired private AnyofApiController anyOfController;

  @TestConfiguration
  static class MockConfig {
    @Bean
    public OneOfInterface oneOfInterface() {
      return Mockito.mock(OneOfInterface.class);
    }

    @Bean
    public AnyOfInterface anyOfInterface() {
      return Mockito.mock(AnyOfInterface.class);
    }
  }
}
