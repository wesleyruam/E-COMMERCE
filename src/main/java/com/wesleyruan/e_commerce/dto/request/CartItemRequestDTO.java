package com.wesleyruan.e_commerce.dto.request;

public record CartItemRequestDTO(
    Long productId,
    Integer quantity
) {
}
