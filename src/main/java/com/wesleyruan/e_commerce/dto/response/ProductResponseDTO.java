package com.wesleyruan.e_commerce.dto.response;

public record ProductResponseDTO(
    Long id,
    String name, 
    String description,
    Double price,
    Integer quantity,
    Boolean active
) {
    
}
