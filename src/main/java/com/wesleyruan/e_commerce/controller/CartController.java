package com.wesleyruan.e_commerce.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wesleyruan.e_commerce.dto.response.CartResponseDTO;
import com.wesleyruan.e_commerce.dto.response.ServiceResponse;
import com.wesleyruan.e_commerce.service.CartService;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping()
    public ResponseEntity<ServiceResponse<CartResponseDTO>> getCart(){
        ServiceResponse<CartResponseDTO> response = cartService.getCart();
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @PostMapping()
    public ResponseEntity<ServiceResponse<String>> addToCart(@RequestParam Long productId, @RequestParam Integer quantity){
        ServiceResponse<String> response = cartService.addToCart(productId, quantity);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ServiceResponse<String>> removeFromCart(@PathVariable Long productId){
        ServiceResponse<String> response = cartService.removeFromCart(productId);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    

}
