package com.github.muehmar.openapischema.spring.rest;

import com.github.muehmar.openapischema.spring.rest.anyof.AnyOfController;
import com.github.muehmar.openapischema.spring.rest.anyof.AnyOfInterface;
import com.github.muehmar.openapischema.spring.rest.oneof.OneOfController;
import com.github.muehmar.openapischema.spring.rest.oneof.OneOfInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

public class ControllerUnitTest {

  @MockBean private OneOfInterface oneOfInterface;
  @MockBean private AnyOfInterface anyOfInterface;

  @Autowired private OneOfController oneOfController;
  @Autowired private AnyOfController anyOfController;
}
