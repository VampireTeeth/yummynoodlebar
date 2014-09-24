package com.yummynoodlebar.rest.controller;

import com.yummynoodlebar.core.events.orders.CreateOrderEvent;
import com.yummynoodlebar.core.services.OrderService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static com.yummynoodlebar.rest.controller.fixture.RestDataFixtures.YUMMY_ITEM;
import static com.yummynoodlebar.rest.controller.fixture.RestDataFixtures.standardOrderJSON;
import static com.yummynoodlebar.rest.controller.fixture.RestEventFixtures.orderCreated;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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
		this.mockMvc = standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	public void thatCreateOrderUsesHttpCreated() throws Exception {
		when(orderService.createOrder(any(CreateOrderEvent.class)))
				.thenReturn(orderCreated(key));

		this.mockMvc
				.perform(
						post("/aggregators/orders")
								.content(standardOrderJSON())
								.contentType(APPLICATION_JSON)
								.accept(APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(status().isCreated());
  }


  @Test
  public void thatCreateOrderRendersAsJson() throws Exception {
		when(orderService.createOrder(any(CreateOrderEvent.class)))
				.thenReturn(orderCreated(key));

		this.mockMvc
				.perform(
						post("/aggregators/orders")
								.content(standardOrderJSON())
								.contentType(APPLICATION_JSON)
								.accept(APPLICATION_JSON))
				.andExpect(jsonPath("$.items['" + YUMMY_ITEM + "']").value(12))
				.andExpect(jsonPath("$.key").value(keyString));
  }
  
}
