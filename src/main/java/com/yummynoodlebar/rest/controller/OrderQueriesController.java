package com.yummynoodlebar.rest.controller;

import com.yummynoodlebar.core.events.orders.OrderDetails;
import com.yummynoodlebar.core.events.orders.OrderDetailsEvent;
import com.yummynoodlebar.core.events.orders.RequestAllOrdersEvent;
import com.yummynoodlebar.core.events.orders.RequestOrderDetailsEvent;
import com.yummynoodlebar.core.services.OrderService;
import com.yummynoodlebar.rest.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/aggregators/orders")
public class OrderQueriesController {
  private static Logger LOG = LoggerFactory.getLogger(OrderQueriesController.class);

  @Autowired
  private OrderService orderService;

  @RequestMapping(method = RequestMethod.GET)
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<Order> getAllOrders() {
    List<Order> orders = new ArrayList<Order>();

    for (OrderDetails details : orderService.requestAllOrders(new RequestAllOrdersEvent()).getOrdersDetails()) {
      orders.add(Order.fromOrderDetails(details));
    }
    return orders;
  }


  @RequestMapping(method = RequestMethod.GET, value = "/{id}")
  public ResponseEntity<Order> viewOrder(@PathVariable String id) {
    OrderDetailsEvent details = orderService.requestOrderDetails(new RequestOrderDetailsEvent(UUID.fromString(id)));
    if (!details.isEntityFound()) {
      return new ResponseEntity<Order>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<Order>(Order.fromOrderDetails(details.getOrderDetails()), HttpStatus.OK);
  }
}

