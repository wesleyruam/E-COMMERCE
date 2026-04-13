package com.wesleyruan.e_commerce.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LoginResponseDTO(
    String token,
    Long expiresIn,
    String refreshToken,
    Long refreshExpiresIn
) {}

