package com.yummynoodlebar.rest.controller;

import com.yummynoodlebar.core.events.orders.RequestOrderDetailsEvent;
import com.yummynoodlebar.core.services.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static com.yummynoodlebar.rest.controller.fixture.RestEventFixtures.orderDetailsEvent;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

/**
 * Created by steven on 27/09/14.
 */
public class ViewOrderContentNegotiationIntegrationTest {
  MockMvc mockMvc;
  @Mock
  OrderService orderService;
  
  @InjectMocks
  OrderQueriesController controller;

  UUID key = UUID.fromString("f3512d26-72f6-4290-9265-63ad69eccc13");

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.mockMvc = standaloneSetup(controller)
            .setMessageConverters(new MappingJackson2HttpMessageConverter(),
                    new Jaxb2RootElementHttpMessageConverter())
            .build();
  }

  @Test
  public void thatViewOrderRenderXMLCorrectly() throws Exception {

    when(orderService.requestOrderDetails(any(RequestOrderDetailsEvent.class)))
            .thenReturn(orderDetailsEvent(key));

    mockMvc.perform(get("/aggregators/orders/{id}", key.toString()).accept(MediaType.TEXT_XML))
            .andDo(print())
            .andExpect(content().contentType(MediaType.TEXT_XML))
            .andExpect(xpath("/order/key").string(key.toString()));
  }

  @Test
  public void thatViewOrderRenderJSONCorrectly() throws Exception {
    when(orderService.requestOrderDetails(any(RequestOrderDetailsEvent.class)))
            .thenReturn(orderDetailsEvent(key));

    mockMvc.perform(get("/aggregators/orders/{id}", key.toString()).accept(MediaType.APPLICATION_JSON))
            .andDo(print())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.key").value(key.toString()));
  }
}
