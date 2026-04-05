package com.wesleyruan.e_commerce.dto.request;

public record ProductRequestDTO(
    String name, 
    String description,
    Double price,
    Integer stock) {
}
