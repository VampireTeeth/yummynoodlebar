package com.yummynoodlebar.rest.controller;

import com.yummynoodlebar.core.events.orders.CreateOrderEvent;
import com.yummynoodlebar.core.events.orders.DeleteOrderEvent;
import com.yummynoodlebar.core.events.orders.OrderCreatedEvent;
import com.yummynoodlebar.core.events.orders.OrderDeletedEvent;
import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.rest.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@Controller
@RequestMapping("/aggregators/orders")
public class OrderCommandsController {

  private static Logger LOG = LoggerFactory.getLogger(OrderCommandsController.class);

  @Autowired
  private OrderService orderService;

  @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
  public ResponseEntity<Order> cancelOrder(@PathVariable String id) {
    OrderDeletedEvent deleted = orderService.deleteOrder(new DeleteOrderEvent(UUID.fromString(id)));
    if(!deleted.isEntityFound()) {
      return new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
    }
    Order order = Order.fromOrderDetails(deleted.getDetails());
    if(deleted.isDeletionCompleted()) {
      return new ResponseEntity<Order>(order, HttpStatus.OK);
    }
    return new ResponseEntity<Order>(HttpStatus.FORBIDDEN);
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<Order> createOrder(@RequestBody Order order, UriComponentsBuilder builder) {
    OrderCreatedEvent created = orderService.createOrder(new CreateOrderEvent(order.toOrderDetails()));
    String newKey = created.getNewOrderKey().toString();
    URI uri = builder.path("/aggregators/orders/{id}").buildAndExpand(newKey).toUri();
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(uri);
    Order newOrder = Order.fromOrderDetails(created.getDetails());
    return new ResponseEntity<Order>(newOrder, headers, HttpStatus.CREATED);
  }
}