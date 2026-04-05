package com.wesleyruan.e_commerce.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wesleyruan.e_commerce.domain.enums.OrderStatusEnum;
import com.wesleyruan.e_commerce.domain.model.CartItemModel;
import com.wesleyruan.e_commerce.domain.model.CartModel;
import com.wesleyruan.e_commerce.domain.model.OrderItemModel;
import com.wesleyruan.e_commerce.domain.model.OrderModel;
import com.wesleyruan.e_commerce.dto.response.OrderItemResponseDTO;
import com.wesleyruan.e_commerce.dto.response.OrderResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.exception.BadRequestException;
import com.wesleyruan.e_commerce.exception.NotFoundException;
import com.wesleyruan.e_commerce.exception.UnauthorizedException;
import com.wesleyruan.e_commerce.repository.CartItemRepository;
import com.wesleyruan.e_commerce.repository.OrderItemRepository;
import com.wesleyruan.e_commerce.repository.OrderRepository;
import com.wesleyruan.e_commerce.util.SecurityUtils;

import jakarta.transaction.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private CartService cartService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private SecurityUtils securityUtils;

    @Transactional
    public ServiceResponse<OrderResponseDTO> createOrder() {
        CartModel cart = cartService.getUserCart();
        List<CartItemModel> cartItems = cartItemRepository.findByCartId(cart.getId());

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Double total = cartItems.stream()
            .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
            .sum();

        OrderModel order = new OrderModel();
        order.setUser(cart.getUser());
        order.setTotal(total);
        order.setStatus(OrderStatusEnum.CREATED);
        order = orderRepository.save(order);

        List<OrderItemModel> orderItems = orderItemService.createOrderItems(order, cartItems);

        cartService.deleteCart(cart);

        return ServiceResponse.created(toOrderDTO(order, toItemDTOs(orderItems)), "Order created successfully.");
    }

    public ServiceResponse<List<OrderResponseDTO>> getOrders() {
        List<OrderModel> orders = orderRepository.findByUserId(securityUtils.getUserId());

        List<OrderResponseDTO> response = orders.stream().map(order -> {
            List<OrderItemResponseDTO> itemDTOs = toItemDTOs(orderItemRepository.findByOrderId(order.getId()));
            return toOrderDTO(order, itemDTOs);
        }).toList();

        return ServiceResponse.success(response);
    }

    public ServiceResponse<OrderResponseDTO> getOrderById(Long id) {
        OrderModel order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getUser().getId() != securityUtils.getUserId()) {
            throw new UnauthorizedException("Access denied");
        }

        List<OrderItemResponseDTO> itemDTOs = toItemDTOs(orderItemRepository.findByOrderId(order.getId()));

        return ServiceResponse.success(toOrderDTO(order, itemDTOs));
    }

    public ServiceResponse<Void> cancelOrder(Long id) {
        OrderModel order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));

        if (order.getUser().getId() != securityUtils.getUserId()) {
            throw new UnauthorizedException("Access denied");
        }

        if (order.getStatus() == OrderStatusEnum.PAID) {
            throw new BadRequestException("Cannot cancel a paid order");
        }

        if (order.getStatus() == OrderStatusEnum.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        order.setStatus(OrderStatusEnum.CANCELLED);
        orderRepository.save(order);

        return ServiceResponse.noContent();
    }

    private List<OrderItemResponseDTO> toItemDTOs(List<OrderItemModel> items) {
        return items.stream().map(item -> new OrderItemResponseDTO(
            item.getId(),
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getPrice()
        )).toList();
    }

    private OrderResponseDTO toOrderDTO(OrderModel order, List<OrderItemResponseDTO> items) {
        return new OrderResponseDTO(
            order.getId(),
            order.getTotal(),
            order.getStatus(),
            order.getPaymentId(),
            order.getPaymentUrl(),
            order.getCreatedAt(),
            items
        );
    }
}
