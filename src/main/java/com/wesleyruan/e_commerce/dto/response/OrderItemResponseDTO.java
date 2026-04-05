package com.wesleyruan.e_commerce.dto.response;

public record OrderItemResponseDTO(
    Long id,
    Long productId,
    String productName,
    Integer quantity,
    Double price
) {
}
