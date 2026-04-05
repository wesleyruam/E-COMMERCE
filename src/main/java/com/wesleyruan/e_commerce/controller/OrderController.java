package com.wesleyruan.e_commerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wesleyruan.e_commerce.dto.response.OrderItemResponseDTO;
import com.wesleyruan.e_commerce.dto.response.OrderResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.service.OrderItemService;
import com.wesleyruan.e_commerce.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @PostMapping()
    public ResponseEntity<ServiceResponse<OrderResponseDTO>> createOrder() {
        ServiceResponse<OrderResponseDTO> response = orderService.createOrder();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping()
    public ResponseEntity<ServiceResponse<List<OrderResponseDTO>>> getOrders() {
        ServiceResponse<List<OrderResponseDTO>> response = orderService.getOrders();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceResponse<OrderResponseDTO>> getOrderById(@PathVariable Long id) {
        ServiceResponse<OrderResponseDTO> response = orderService.getOrderById(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping("/{id}/items")
    public ResponseEntity<ServiceResponse<List<OrderItemResponseDTO>>> getOrderItems(@PathVariable Long id) {
        ServiceResponse<List<OrderItemResponseDTO>> response = orderItemService.getOrderItems(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ServiceResponse<Void>> cancelOrder(@PathVariable Long id) {
        ServiceResponse<Void> response = orderService.cancelOrder(id);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
