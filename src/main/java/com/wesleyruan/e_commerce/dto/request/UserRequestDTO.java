package com.wesleyruan.e_commerce.dto.request;

import java.time.LocalDate;


public record UserRequestDTO (
    String name,
    String email,
    String password,
    String phone,
    LocalDate dateOfBirth
){
    
}
