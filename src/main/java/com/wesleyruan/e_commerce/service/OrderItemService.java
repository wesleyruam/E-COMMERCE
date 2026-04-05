package com.wesleyruan.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wesleyruan.e_commerce.domain.model.OrderItemModel;
import com.wesleyruan.e_commerce.domain.model.OrderModel;
import com.wesleyruan.e_commerce.dto.response.OrderItemResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.exception.NotFoundException;
import com.wesleyruan.e_commerce.exception.UnauthorizedException;
import com.wesleyruan.e_commerce.repository.OrderItemRepository;
import com.wesleyruan.e_commerce.repository.OrderRepository;
import com.wesleyruan.e_commerce.util.SecurityUtils;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SecurityUtils securityUtils;

    public List<OrderItemModel> createOrderItems(OrderModel order, List<com.wesleyruan.e_commerce.domain.model.CartItemModel> cartItems) {
        List<OrderItemModel> orderItems = cartItems.stream().map(cartItem -> {
            OrderItemModel item = new OrderItemModel();
            item.setOrder(order);
            item.setProduct(cartItem.getProduct());
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(cartItem.getProduct().getPrice()); // snapshot do preço no momento da compra
            return item;
        }).toList();

        return orderItemRepository.saveAll(orderItems);
    }

    public ServiceResponse<List<OrderItemResponseDTO>> getOrderItems(Long orderId) {
        OrderModel order = orderRepository.findById(orderId)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getUser().getId() != securityUtils.getUserId()) {
            throw new UnauthorizedException("Access denied");
        }

        List<OrderItemResponseDTO> response = orderItemRepository.findByOrderId(orderId)
            .stream().map(item -> new OrderItemResponseDTO(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getPrice()
            )).toList();

        return ServiceResponse.success(response);
    }
}
