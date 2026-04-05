package com.wesleyruan.e_commerce.dto.response;

import java.util.List;

import com.wesleyruan.e_commerce.domain.model.CartItemModel;

public record CartResponseDTO(
    List<CartItemModel> items
) {
    
}
