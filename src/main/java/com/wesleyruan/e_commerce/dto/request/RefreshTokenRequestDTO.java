package com.wesleyruan.e_commerce.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDTO(
    @NotBlank String refreshToken
) {}
