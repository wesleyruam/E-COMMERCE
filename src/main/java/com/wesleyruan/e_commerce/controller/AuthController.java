package com.wesleyruan.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wesleyruan.e_commerce.dto.request.LoginRequestDTO;
import com.wesleyruan.e_commerce.dto.response.LoginResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ServiceResponse<LoginResponseDTO>> login(@RequestBody @Valid LoginRequestDTO request){
        ServiceResponse<LoginResponseDTO> response  = authService.login(request);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
