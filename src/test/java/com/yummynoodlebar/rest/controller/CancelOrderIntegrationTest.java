package com.yummynoodlebar.rest.controller;

import java.util.UUID;

import org.hamcrest.Matchers;
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

import com.yummynoodlebar.core.events.orders.DeleteOrderEvent;
import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.rest.controller.fixture.RestEventFixtures;

public class CancelOrderIntegrationTest {

	MockMvc mockMvc;

	@InjectMocks
	OrderCommandsController controller;

	@Mock
	OrderService orderService;

	UUID key = UUID.fromString("f3512d26-72f6-4290-9265-63ad69eccc13");

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders
				.standaloneSetup(controller)
				.setMessageConverters(new MappingJackson2HttpMessageConverter())
				.build();
	}

	@Test
	public void thatDeleteOrderUsesHttpOkOnSuccess() throws Exception {
		Mockito.when(
				orderService.deleteOrder(Mockito.any(DeleteOrderEvent.class)))
				.thenReturn(RestEventFixtures.orderDeleted(key));

		this.mockMvc
				.perform(
						MockMvcRequestBuilders.delete(
								"/aggregators/orders/{id}", key.toString())
								.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isOk());

		Mockito.verify(orderService).deleteOrder(
				Mockito.argThat(Matchers.<DeleteOrderEvent> hasProperty("key",
						Matchers.equalTo(key))));
	}

	@Test
	public void thatDeleteOrderUsesHttpNotFoundOnEntityLookupFailure()
			throws Exception {
		Mockito.when(
				orderService.deleteOrder(Mockito.any(DeleteOrderEvent.class)))
				.thenReturn(RestEventFixtures.orderDeletedNotFound(key));
		this.mockMvc
				.perform(
						MockMvcRequestBuilders.delete(
								"/aggregators/orders/{id}", key.toString())
								.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
				.andExpect(MockMvcResultMatchers.status().isNotFound());
	}

	@Test
	public void thatDeleteOrderUsesHttpForbiddenOnEntityDeletionFailure()
			throws Exception {
		Mockito.when(
				orderService.deleteOrder(Mockito.any(DeleteOrderEvent.class)))
				.thenReturn(RestEventFixtures.orderDeletedFailed(key));
		this.mockMvc
				.perform(
						MockMvcRequestBuilders.delete(
								"/aggregators/orders/{id}", key.toString())
								.accept(MediaType.APPLICATION_JSON))
				.andDo(MockMvcResultHandlers.print())
      .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
  
}
