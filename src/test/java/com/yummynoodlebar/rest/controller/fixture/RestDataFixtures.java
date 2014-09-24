package com.yummynoodlebar.rest.controller.fixture;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.yummynoodlebar.core.events.orders.AllOrdersEvent;
import com.yummynoodlebar.core.events.orders.OrderDetails;
import com.yummynoodlebar.rest.domain.Order;

public class RestDataFixtures {

	public static final String YUMMY_ITEM = "yummy1";

	public static AllOrdersEvent allOrders() {
		List<OrderDetails> orders = new ArrayList<OrderDetails>();

		orders.add(standardOrderDetails());
		orders.add(standardOrderDetails());
		orders.add(standardOrderDetails());

		return new AllOrdersEvent(orders);
	}

	public static Order standardOrder() {
		Order order = new Order();

		order.setItems(Collections.singletonMap(YUMMY_ITEM, 12));

		return order;
	}

	public static OrderDetails customKeyOrderDetails(UUID key) {
		OrderDetails orderdetails = new OrderDetails(key);

		orderdetails.setOrderItems(Collections.singletonMap(YUMMY_ITEM, 12));

		return orderdetails;
	}

	public static OrderDetails standardOrderDetails() {
		return customKeyOrderDetails(UUID.randomUUID());
	}

	public static String standardOrderJSON() {
		return "{ \"items\": { \"yummy1\": 12, \"yummy15\": 42 } }";
  }
}
