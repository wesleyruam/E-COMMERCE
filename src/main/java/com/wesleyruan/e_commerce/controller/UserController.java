package com.wesleyruan.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wesleyruan.e_commerce.dto.request.UserRequestDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.dto.response.UserResponseDTO;
import com.wesleyruan.e_commerce.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping() // criar um novo usuário
    public ResponseEntity<ServiceResponse<UserResponseDTO>> createUser(@RequestBody @Valid UserRequestDTO userRequestDTO){
        ServiceResponse<UserResponseDTO> response = userService.createUser(userRequestDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @GetMapping() // obter o perfil do usuário autenticado
    public ResponseEntity<ServiceResponse<UserResponseDTO>> getUserProfile() {
        ServiceResponse<UserResponseDTO> response = userService.getUserProfile();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping() // deletar o usuário autenticado
    public ResponseEntity<ServiceResponse<Void>> deleteUser() {
        ServiceResponse<Void> response = userService.deleteUser();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PatchMapping() // atualizar o perfil do usuário autenticado
    public ResponseEntity<ServiceResponse<UserResponseDTO>> updateUser(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        ServiceResponse<UserResponseDTO> response = userService.updateUser(userRequestDTO);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }



    
}
