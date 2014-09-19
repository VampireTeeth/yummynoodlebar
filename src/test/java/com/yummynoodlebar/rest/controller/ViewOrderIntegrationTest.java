package com.yummynoodlebar.rest.controller;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.yummynoodlebar.core.events.orders.RequestOrderDetailsEvent;
import com.yummynoodlebar.core.services.OrderService;

import static org.mockito.Mockito.*;
import static com.yummynoodlebar.rest.controller.fixture.RestEventFixtures.*; 
import static com.yummynoodlebar.rest.controller.fixture.RestDataFixtures.*;


public class ViewOrderIntegrationTest {
  MockMvc mockMvc;

  @InjectMocks
  OrderQueriesController controller;

  @Mock
  OrderService orderService;

  UUID key = UUID.fromString("f3512d26-72f6-4290-9265-63ad69eccc13");

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
      .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
  }

  @Test
  public void thatViewOrderUsesHttpNotFound() throws Exception {
    when(orderService.requestOrderDetails(any(RequestOrderDetailsEvent.class)))
      .thenReturn(orderDetailsNotFound(key));
    this.mockMvc.perform(
      MockMvcRequestBuilders.get("/aggregators/orders/{id}", key.toString())
        .accept(MediaType.APPLICATION_JSON))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isNotFound());
  }

  @Test
  public void thatViewOrderUsesHttpOk() throws Exception {
    when(orderService.requestOrderDetails(any(RequestOrderDetailsEvent.class)))
      .thenReturn(orderDetailsEvent(key));
    this.mockMvc.perform(
      MockMvcRequestBuilders.get("/aggregators/orders/{id}", key.toString())
        .accept(MediaType.APPLICATION_JSON))
      .andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isOk());
  }

  @Test
  public void thatViewOrderRendersCorrectly() throws Exception{
    when(orderService.requestOrderDetails(any(RequestOrderDetailsEvent.class)))
      .thenReturn(orderDetailsEvent(key));
    this.mockMvc.perform(
      MockMvcRequestBuilders.get("/aggregators/orders/{id}", key.toString())
        .accept(MediaType.APPLICATION_JSON))
      .andExpect(MockMvcResultMatchers.jsonPath("$.items['"+YUMMY_ITEM+"']").value(12))
      .andExpect(MockMvcResultMatchers.jsonPath("$.key").value(key.toString()));
  }
}
