package com.wesleyruan.e_commerce.dto.response;

import java.time.Instant;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.wesleyruan.e_commerce.domain.enums.RolesEnum;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponseDTO(
    long id,
    String name,
    String email,
    String phone,
    RolesEnum role,
    LocalDate dateOfBirth,
    Instant createdAt,
    Instant updatedAt
) {
    
}
