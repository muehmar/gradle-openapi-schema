package com.github.muehmar.openapischema.spring.rest;

import com.github.muehmar.openapischema.spring.AnyOfApiController;
import com.github.muehmar.openapischema.spring.OneOfApiController;
import com.github.muehmar.openapischema.spring.rest.anyof.AnyOfDelegate;
import com.github.muehmar.openapischema.spring.rest.anyof.AnyOfInterface;
import com.github.muehmar.openapischema.spring.rest.oneof.OneOfDelegate;
import com.github.muehmar.openapischema.spring.rest.oneof.OneOfInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import({OneOfDelegate.class, AnyOfDelegate.class})
public class ControllerUnitTest {

  @MockBean private OneOfInterface oneOfInterface;
  @MockBean private AnyOfInterface anyOfInterface;

  @Autowired private OneOfApiController oneOfApiController;
  @Autowired private AnyOfApiController anyOfApiController;
}
