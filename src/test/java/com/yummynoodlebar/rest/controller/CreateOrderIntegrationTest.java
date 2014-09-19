package com.yummynoodlebar.rest.controller;

import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.yummynoodlebar.core.events.orders.CreateOrderEvent;
import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.rest.controller.fixture.RestDataFixtures;
import com.yummynoodlebar.rest.controller.fixture.RestEventFixtures;

public class CreateOrderIntegrationTest {

	MockMvc mockMvc;

	@InjectMocks
	OrderCommandsController controller;

	@Mock
	OrderService orderService;

	String keyString = "f3512d26-72f6-4290-9265-63ad69eccc13";
	UUID key = UUID.fromString(keyString);

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	public void thatCreateOrderUsesHttpCreated() throws Exception {
		Mockito.when(
				orderService.createOrder(Mockito.any(CreateOrderEvent.class)))
				.thenReturn(RestEventFixtures.orderCreated(key));

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post("/aggregators/orders")
								.content(RestDataFixtures.standardOrderJSON())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isCreated());
  }


  @Test
  public void thatCreateOrderRendersAsJson() throws Exception {
		Mockito.when(
				orderService.createOrder(Mockito.any(CreateOrderEvent.class)))
				.thenReturn(RestEventFixtures.orderCreated(key));

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.post("/aggregators/orders")
								.content(RestDataFixtures.standardOrderJSON())
								.contentType(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.jsonPath("$.items['" +RestDataFixtures.YUMMY_ITEM+ "']").value(12))
				.andExpect(MockMvcResultMatchers.jsonPath("$.key").value(keyString));
  }
  
}
