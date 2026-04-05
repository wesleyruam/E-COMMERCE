package com.wesleyruan.e_commerce.dto.response;

import java.time.Instant;
import java.util.List;

import com.wesleyruan.e_commerce.domain.enums.OrderStatusEnum;

public record OrderResponseDTO(
    Long id,
    Double total,
    OrderStatusEnum status,
    String paymentId,
    String paymentUrl,
    Instant createdAt,
    List<OrderItemResponseDTO> items
) {
}
